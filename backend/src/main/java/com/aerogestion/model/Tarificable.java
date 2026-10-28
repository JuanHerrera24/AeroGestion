package com.aerogestion.model;

/** Contrato para todo elemento cuyo precio se calcula a partir de una tarifa base. */
public interface Tarificable {

    double calcularPrecio(double tarifaBase);
}
