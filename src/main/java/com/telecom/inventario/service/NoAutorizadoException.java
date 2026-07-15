package com.telecom.inventario.service;

/** El usuario que hace la peticion no tiene permisos para esta accion (por ejemplo, no es Admin). */
public class NoAutorizadoException extends RuntimeException {
    public NoAutorizadoException(String mensaje) {
        super(mensaje);
    }
}
