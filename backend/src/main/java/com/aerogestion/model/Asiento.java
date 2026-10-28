package com.aerogestion.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Asiento de un avión. Cada clase (económica, ejecutiva, primera) define su factor de precio. */
@Getter
@Setter
@NoArgsConstructor
public abstract class Asiento implements Tarificable {

    private String numero;
    private int fila;
    private String letra;

    protected Asiento(int fila, String letra) {
        this.fila = fila;
        this.letra = letra;
        this.numero = fila + letra;
    }

    public abstract double getFactorPrecio();

    public abstract String getClase();

    @Override
    public double calcularPrecio(double tarifaBase) {
        return Math.round(tarifaBase * getFactorPrecio() * 100) / 100.0;
    }
}
