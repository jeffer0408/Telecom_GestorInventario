package com.telecom.inventario.service;

import com.telecom.inventario.model.Movimiento;
import com.telecom.inventario.model.Producto;
import com.telecom.inventario.model.Proveedor;
import com.telecom.inventario.model.TipoAlmacen;
import com.telecom.inventario.repository.ProductoRepository;
import com.telecom.inventario.repository.ProveedorRepository;
import com.telecom.inventario.repository.TipoAlmacenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ProveedorRepository proveedorRepository;
    private final TipoAlmacenRepository tipoAlmacenRepository;
    private final MovimientoService movimientoService;

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public Producto obtener(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado (id " + id + ")"));
    }

    public List<Producto> buscar(String nombre, Long proveedorId, Long tipoAlmacenId) {
        String texto = (nombre == null || nombre.isBlank()) ? null : nombre.trim();
        return productoRepository.buscar(texto, proveedorId, tipoAlmacenId);
    }

    public Producto crear(Producto datos) {
        Producto producto = new Producto();
        aplicarDatos(producto, datos);
        Producto guardado = productoRepository.save(producto);

        movimientoService.registrar(Movimiento.TipoMovimiento.ALTA, guardado.getNombre(),
                guardado.getCantidad(), "Producto agregado al inventario");

        return guardado;
    }

    public Producto actualizar(Long id, Producto datos) {
        Producto producto = obtener(id);
        aplicarDatos(producto, datos);
        Producto actualizado = productoRepository.save(producto);

        movimientoService.registrar(Movimiento.TipoMovimiento.EDICION, actualizado.getNombre(),
                actualizado.getCantidad(), "Producto editado");

        return actualizado;
    }

    public void eliminar(Long id) {
        Producto producto = obtener(id);
        movimientoService.registrar(Movimiento.TipoMovimiento.BAJA, producto.getNombre(),
                producto.getCantidad(), "Producto eliminado del inventario");
        productoRepository.delete(producto);
    }

    private void aplicarDatos(Producto producto, Producto datos) {
        Proveedor proveedor = proveedorRepository.findById(datos.getProveedor().getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado"));
        TipoAlmacen tipoAlmacen = tipoAlmacenRepository.findById(datos.getTipoAlmacen().getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Tipo de almacen no encontrado"));

        producto.setNombre(datos.getNombre());
        producto.setCantidad(datos.getCantidad());
        producto.setPrecio(datos.getPrecio());
        producto.setProveedor(proveedor);
        producto.setTipoAlmacen(tipoAlmacen);
    }
}
