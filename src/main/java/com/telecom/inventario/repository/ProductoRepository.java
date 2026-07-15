package com.telecom.inventario.repository;

import com.telecom.inventario.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    @Query("select p from Producto p where " +
           "(:nombre is null or lower(p.nombre) like lower(concat('%', :nombre, '%'))) and " +
           "(:proveedorId is null or p.proveedor.id = :proveedorId) and " +
           "(:tipoAlmacenId is null or p.tipoAlmacen.id = :tipoAlmacenId)")
    List<Producto> buscar(@Param("nombre") String nombre,
                           @Param("proveedorId") Long proveedorId,
                           @Param("tipoAlmacenId") Long tipoAlmacenId);
}
