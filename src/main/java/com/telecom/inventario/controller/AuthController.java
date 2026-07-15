package com.telecom.inventario.controller;

import com.telecom.inventario.dto.LoginRequest;
import com.telecom.inventario.dto.RegistroEncargadoRequest;
import com.telecom.inventario.dto.SolicitarCodigoRequest;
import com.telecom.inventario.model.Usuario;
import com.telecom.inventario.service.CodigoVerificacionService;
import com.telecom.inventario.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final CodigoVerificacionService codigoVerificacionService;

    @PostMapping("/login")
    public Usuario login(@Valid @RequestBody LoginRequest datos) {
        return usuarioService.autenticar(datos);
    }

    @PostMapping("/codigo")
    public void solicitarCodigo(@Valid @RequestBody SolicitarCodigoRequest datos) {
        codigoVerificacionService.generarYEnviar(datos.getCorreo());
    }

    @PostMapping("/registro")
    public Usuario registrar(@Valid @RequestBody RegistroEncargadoRequest datos) {
        return usuarioService.registrarEncargado(datos);
    }
}
