package com.telecom.inventario.service;

import com.telecom.inventario.model.Movimiento;
import com.telecom.inventario.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;

    public List<Movimiento> listarTodos() {
        return movimientoRepository.findAllByOrderByFechaDesc();
    }

    public void registrar(Movimiento.TipoMovimiento tipo, String producto, BigDecimal cantidad, String detalle) {
        Movimiento m = new Movimiento();
        m.setTipo(tipo);
        m.setProducto(producto);
        m.setCantidad(cantidad);
        m.setDetalle(detalle);
        movimientoRepository.save(m);
    }
}
