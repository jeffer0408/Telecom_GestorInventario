package com.telecom.inventario.repository;

import com.telecom.inventario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByCorreo(String correo);
    Optional<Usuario> findByDni(String dni);

    boolean existsByUsername(String username);
    boolean existsByCorreo(String correo);
    boolean existsByDni(String dni);

    long countByRol(Usuario.Rol rol);
}
