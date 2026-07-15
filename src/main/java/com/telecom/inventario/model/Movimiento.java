package com.telecom.inventario.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimiento")
@Getter
@Setter
@NoArgsConstructor
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TipoMovimiento tipo;

    @Column(nullable = false, length = 150)
    private String producto;

    @Column(precision = 12, scale = 2)
    private BigDecimal cantidad;

    @Column(length = 400)
    private String detalle;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @PrePersist
    private void asignarFecha() {
        this.fecha = LocalDateTime.now();
    }

    public enum TipoMovimiento {
        ALTA, EDICION, BAJA
    }
}
