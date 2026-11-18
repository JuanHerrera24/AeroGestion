package com.aerogestion.model;

import org.springframework.data.annotation.TypeAlias;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@TypeAlias("PRIMERA_CLASE")
public class AsientoPrimeraClase extends Asiento {

    public AsientoPrimeraClase(int fila, String letra) {
        super(fila, letra);
    }

    @Override
    public double getFactorPrecio() {
        return 3.0;
    }

    @Override
    public String getClase() {
        return "PRIMERA";
    }
}
