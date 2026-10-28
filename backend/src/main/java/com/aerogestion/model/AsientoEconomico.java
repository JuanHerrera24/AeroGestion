package com.aerogestion.model;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AsientoEconomico extends Asiento {

    public AsientoEconomico(int fila, String letra) {
        super(fila, letra);
    }

    @Override
    public double getFactorPrecio() {
        return 1.0;
    }

    @Override
    public String getClase() {
        return "ECONOMICA";
    }
}
