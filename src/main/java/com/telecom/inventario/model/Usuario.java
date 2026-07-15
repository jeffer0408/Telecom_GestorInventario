package com.telecom.inventario.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String nombre;

    @NotBlank
    @Column(nullable = false, unique = true, length = 150)
    private String correo;

    @NotBlank
    @Column(nullable = false, unique = true, length = 20)
    private String dni;

    @NotBlank
    @Column(nullable = false, length = 20)
    private String celular;

    @NotBlank
    @Column(nullable = false, unique = true, length = 60)
    private String username;

    @JsonIgnore
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Rol rol;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    private void asignarFecha() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public enum Rol {
        ADMIN, ENCARGADO
    }
}
