package com.aerogestion.model;

import org.springframework.data.annotation.TypeAlias;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@TypeAlias("ECONOMICO")
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
