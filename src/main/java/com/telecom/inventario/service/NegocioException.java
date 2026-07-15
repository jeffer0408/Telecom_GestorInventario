package com.telecom.inventario.service;

/** Error de validacion de negocio (datos duplicados, codigo invalido, credenciales incorrectas, etc). */
public class NegocioException extends RuntimeException {
    public NegocioException(String mensaje) {
        super(mensaje);
    }
}
