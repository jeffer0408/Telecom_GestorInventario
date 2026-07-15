package com.telecom.inventario.service;

import com.telecom.inventario.model.TipoAlmacen;
import com.telecom.inventario.repository.TipoAlmacenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoAlmacenService {

    private final TipoAlmacenRepository tipoAlmacenRepository;

    public List<TipoAlmacen> listarTodos() {
        return tipoAlmacenRepository.findAll();
    }

    public TipoAlmacen guardar(TipoAlmacen tipoAlmacen) {
        return tipoAlmacenRepository.save(tipoAlmacen);
    }

    public void eliminar(Long id) {
        tipoAlmacenRepository.deleteById(id);
    }
}
