package com.telecom.inventario.controller;

import com.telecom.inventario.model.Movimiento;
import com.telecom.inventario.service.MovimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

    private final MovimientoService movimientoService;

    @GetMapping
    public List<Movimiento> listar() {
        return movimientoService.listarTodos();
    }
}
