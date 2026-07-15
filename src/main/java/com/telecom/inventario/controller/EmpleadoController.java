package com.telecom.inventario.controller;

import com.telecom.inventario.dto.UsuarioEditRequest;
import com.telecom.inventario.model.Usuario;
import com.telecom.inventario.service.NoAutorizadoException;
import com.telecom.inventario.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints exclusivos del Admin para ver, editar y eliminar cuentas.
 * El front-end envia el rol y el id de quien esta conectado en cabeceras
 * (X-Rol, X-Usuario-Id); se valida aqui que solo un ADMIN pueda usarlos.
 */
@RestController
@RequestMapping("/api/empleados")
@RequiredArgsConstructor
public class EmpleadoController {

    private final UsuarioService usuarioService;

    @GetMapping
    public List<Usuario> listar(@RequestHeader(value = "X-Rol", required = false) String rol) {
        exigirAdmin(rol);
        return usuarioService.listarTodos();
    }

    @PutMapping("/{id}")
    public Usuario actualizar(@PathVariable Long id,
                               @Valid @RequestBody UsuarioEditRequest datos,
                               @RequestHeader(value = "X-Rol", required = false) String rol) {
        exigirAdmin(rol);
        return usuarioService.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id,
                          @RequestHeader(value = "X-Rol", required = false) String rol,
                          @RequestHeader(value = "X-Usuario-Id", required = false) Long idSolicitante) {
        exigirAdmin(rol);
        usuarioService.eliminar(id, idSolicitante);
    }

    private void exigirAdmin(String rol) {
        if (rol == null || !rol.equals("ADMIN")) {
            throw new NoAutorizadoException("Solo el Administrador puede acceder a esta seccion");
        }
    }
}
