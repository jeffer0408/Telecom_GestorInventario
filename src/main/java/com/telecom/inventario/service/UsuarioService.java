package com.telecom.inventario.service;

import com.telecom.inventario.dto.LoginRequest;
import com.telecom.inventario.dto.RegistroEncargadoRequest;
import com.telecom.inventario.dto.UsuarioEditRequest;
import com.telecom.inventario.model.Usuario;
import com.telecom.inventario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final CodigoVerificacionService codigoVerificacionService;

    /* ---------- Login ---------- */
    public Usuario autenticar(LoginRequest datos) {
        Usuario usuario = usuarioRepository.findByUsername(datos.getUsername().trim())
                .orElseThrow(() -> new NegocioException("Usuario o contraseña incorrectos"));

        if (!passwordEncoder.matches(datos.getPassword(), usuario.getPasswordHash())) {
            throw new NegocioException("Usuario o contraseña incorrectos");
        }
        return usuario;
    }

    /* ---------- Registro publico (siempre como ENCARGADO, requiere codigo) ---------- */
    public Usuario registrarEncargado(RegistroEncargadoRequest datos) {
        validarDuplicados(datos.getUsername(), datos.getCorreo(), datos.getDni(), null);

        // El codigo enviado al Admin debe coincidir con el generado para este mismo correo solicitante
        codigoVerificacionService.validarYConsumir(datos.getCorreo().trim(), datos.getCodigo().trim());

        Usuario usuario = new Usuario();
        usuario.setNombre(datos.getNombre().trim());
        usuario.setCorreo(datos.getCorreo().trim());
        usuario.setDni(datos.getDni().trim());
        usuario.setCelular(datos.getCelular().trim());
        usuario.setUsername(datos.getUsername().trim());
        usuario.setPasswordHash(passwordEncoder.encode(datos.getPassword()));
        usuario.setRol(Usuario.Rol.ENCARGADO);

        return usuarioRepository.save(usuario);
    }

    /* ---------- Administrar empleados (solo Admin) ---------- */
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario actualizar(Long id, UsuarioEditRequest datos) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta no encontrada"));

        validarDuplicados(datos.getUsername(), datos.getCorreo(), datos.getDni(), id);

        usuario.setNombre(datos.getNombre().trim());
        usuario.setCorreo(datos.getCorreo().trim());
        usuario.setDni(datos.getDni().trim());
        usuario.setCelular(datos.getCelular().trim());
        usuario.setUsername(datos.getUsername().trim());
        usuario.setRol(datos.getRol());

        if (datos.getPassword() != null && !datos.getPassword().isBlank()) {
            usuario.setPasswordHash(passwordEncoder.encode(datos.getPassword()));
        }

        return usuarioRepository.save(usuario);
    }

    public void eliminar(Long id, Long idSolicitante) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta no encontrada"));

        if (usuario.getId().equals(idSolicitante)) {
            throw new NegocioException("No puedes eliminar tu propia cuenta mientras estas conectado");
        }
        if (usuario.getRol() == Usuario.Rol.ADMIN && usuarioRepository.countByRol(Usuario.Rol.ADMIN) <= 1) {
            throw new NegocioException("No puedes eliminar al unico Administrador del sistema");
        }

        usuarioRepository.delete(usuario);
    }

    /* ---------- Utilidades ---------- */
    private void validarDuplicados(String username, String correo, String dni, Long idExcluido) {
        usuarioRepository.findByUsername(username.trim()).ifPresent(u -> {
            if (idExcluido == null || !u.getId().equals(idExcluido)) {
                throw new NegocioException("Ese nombre de usuario ya esta en uso");
            }
        });
        usuarioRepository.findByCorreo(correo.trim()).ifPresent(u -> {
            if (idExcluido == null || !u.getId().equals(idExcluido)) {
                throw new NegocioException("Ese correo ya esta registrado");
            }
        });
        usuarioRepository.findByDni(dni.trim()).ifPresent(u -> {
            if (idExcluido == null || !u.getId().equals(idExcluido)) {
                throw new NegocioException("Ese DNI ya esta registrado");
            }
        });
    }
}
