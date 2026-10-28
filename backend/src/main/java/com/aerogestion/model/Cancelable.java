package com.aerogestion.model;

/** Contrato para entidades que pueden cancelarse bajo ciertas reglas de negocio. */
public interface Cancelable {

    void cancelar();

    boolean puedeCancelarse();
}
