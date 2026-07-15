package com.telecom.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa la unidad/forma de almacenamiento de un producto:
 * Bolsa, Unidad, Caja, Metro, Centimetro, Rollo, etc.
 */
@Entity
@Table(name = "tipo_almacen")
@Getter
@Setter
@NoArgsConstructor
public class TipoAlmacen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del tipo de almacen es obligatorio")
    @Column(nullable = false, unique = true, length = 60)
    private String nombre;
}
