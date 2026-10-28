package com.aerogestion.exception;

/** Se lanza cuando una operación viola una regla de negocio (ej. asiento ocupado). */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
