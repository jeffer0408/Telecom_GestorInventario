package com.telecom.inventario.controller;

import com.telecom.inventario.model.TipoAlmacen;
import com.telecom.inventario.service.TipoAlmacenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-almacen")
@RequiredArgsConstructor
public class TipoAlmacenController {

    private final TipoAlmacenService tipoAlmacenService;

    @GetMapping
    public List<TipoAlmacen> listar() {
        return tipoAlmacenService.listarTodos();
    }

    @PostMapping
    public TipoAlmacen crear(@Valid @RequestBody TipoAlmacen tipoAlmacen) {
        return tipoAlmacenService.guardar(tipoAlmacen);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        tipoAlmacenService.eliminar(id);
    }
}
