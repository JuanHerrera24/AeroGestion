package com.aerogestion.model;

import lombok.NoArgsConstructor;

@NoArgsConstructor
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
