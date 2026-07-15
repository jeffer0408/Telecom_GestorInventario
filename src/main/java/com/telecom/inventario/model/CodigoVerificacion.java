package com.telecom.inventario.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Codigo temporal enviado al correo del Admin para autorizar
 * el registro de una nueva cuenta de Encargado.
 */
@Entity
@Table(name = "codigo_verificacion")
@Getter
@Setter
@NoArgsConstructor
public class CodigoVerificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "correo_solicitante", nullable = false, length = 150)
    private String correoSolicitante;

    @Column(nullable = false, length = 6)
    private String codigo;

    @Column(nullable = false)
    private boolean usado = false;

    @Column(name = "fecha_generacion", nullable = false)
    private LocalDateTime fechaGeneracion;

    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime fechaExpiracion;
}
