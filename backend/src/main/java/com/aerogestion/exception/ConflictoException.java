package com.aerogestion.exception;

/**
 * La petición es válida, pero choca con el estado actual de los datos (HTTP 409 Conflict).
 * Ej.: el asiento ya está ocupado, la matrícula ya existe o el vuelo ya está cancelado.
 */
public class ConflictoException extends RuntimeException {

    public ConflictoException(String mensaje) {
        super(mensaje);
    }
}
