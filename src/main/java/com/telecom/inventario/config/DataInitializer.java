package com.telecom.inventario.config;

import com.telecom.inventario.model.Usuario;
import com.telecom.inventario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.usuario}")
    private String adminUsuario;
    @Value("${app.admin.password}")
    private String adminPassword;
    @Value("${app.admin.nombre}")
    private String adminNombre;
    @Value("${app.admin.correo}")
    private String adminCorreo;
    @Value("${app.admin.dni}")
    private String adminDni;
    @Value("${app.admin.celular}")
    private String adminCelular;

    @Override
    public void run(String... args) {
        if (usuarioRepository.existsByUsername(adminUsuario)) {
            return;
        }

        Usuario admin = new Usuario();
        admin.setNombre(adminNombre);
        admin.setCorreo(adminCorreo);
        admin.setDni(adminDni);
        admin.setCelular(adminCelular);
        admin.setUsername(adminUsuario);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setRol(Usuario.Rol.ADMIN);

        usuarioRepository.save(admin);
        log.info("Cuenta Admin creada automaticamente (usuario: {})", adminUsuario);
    }
}
