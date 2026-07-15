package com.telecom.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "producto")
@Getter
@Setter
@NoArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Column(nullable = false, length = 150)
    private String nombre;

    @NotNull
    @DecimalMin(value = "0", message = "La cantidad no puede ser negativa")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal cantidad;

    @NotNull(message = "Selecciona un tipo de almacen")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_almacen_id", nullable = false)
    private TipoAlmacen tipoAlmacen;

    @NotNull
    @DecimalMin(value = "0", message = "El precio no puede ser negativo")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    @NotNull(message = "Selecciona un proveedor")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;

    /**
     * Total = cantidad * precio. Se recalcula automaticamente antes de guardar.
     */
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal total;

    @PrePersist
    @PreUpdate
    private void calcularTotal() {
        if (cantidad != null && precio != null) {
            this.total = cantidad.multiply(precio);
        }
    }
}
