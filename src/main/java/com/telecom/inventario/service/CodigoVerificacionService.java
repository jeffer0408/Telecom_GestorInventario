package com.telecom.inventario.service;

import com.telecom.inventario.model.CodigoVerificacion;
import com.telecom.inventario.repository.CodigoVerificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class CodigoVerificacionService {

    private static final int MINUTOS_VALIDEZ = 15;
    private final SecureRandom random = new SecureRandom();

    private final CodigoVerificacionRepository codigoRepository;
    private final EmailService emailService;

    @Value("${app.admin.correo}")
    private String correoAdmin;

    /**
     * Genera un codigo de 6 digitos para el correo de quien intenta registrarse
     * como Encargado, y lo envia al correo del Admin (no al solicitante).
     */
    public void generarYEnviar(String correoSolicitante) {
        String codigo = String.format("%06d", random.nextInt(1_000_000));

        CodigoVerificacion cv = new CodigoVerificacion();
        cv.setCorreoSolicitante(correoSolicitante.trim());
        cv.setCodigo(codigo);
        cv.setUsado(false);
        cv.setFechaGeneracion(LocalDateTime.now());
        cv.setFechaExpiracion(LocalDateTime.now().plusMinutes(MINUTOS_VALIDEZ));
        codigoRepository.save(cv);

        String asunto = "Codigo de verificacion - Registro de Encargado";
        String cuerpo = "Se solicito el registro de una nueva cuenta de Encargado.\n\n"
                + "Correo del solicitante: " + correoSolicitante + "\n"
                + "Codigo de verificacion: " + codigo + "\n\n"
                + "Este codigo vence en " + MINUTOS_VALIDEZ + " minutos. "
                + "Compartelo con el solicitante unicamente si reconoces y autorizas su registro.";

        emailService.enviar(correoAdmin, asunto, cuerpo);
    }

    /**
     * Valida que el codigo ingresado sea el ultimo generado para ese correo,
     * que no este vencido ni ya usado, y lo marca como consumido.
     */
    public void validarYConsumir(String correoSolicitante, String codigoIngresado) {
        CodigoVerificacion cv = codigoRepository
                .findTopByCorreoSolicitanteAndUsadoFalseOrderByFechaGeneracionDesc(correoSolicitante.trim())
                .orElseThrow(() -> new NegocioException("No se encontro un codigo vigente para este correo. Solicitalo nuevamente."));

        if (cv.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new NegocioException("El codigo ha vencido. Solicita uno nuevo.");
        }
        if (!cv.getCodigo().equals(codigoIngresado.trim())) {
            throw new NegocioException("El codigo ingresado es incorrecto.");
        }

        cv.setUsado(true);
        codigoRepository.save(cv);
    }
}
