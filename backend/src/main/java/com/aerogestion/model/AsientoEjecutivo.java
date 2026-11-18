package com.aerogestion.model;

import org.springframework.data.annotation.TypeAlias;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@TypeAlias("EJECUTIVO")
public class AsientoEjecutivo extends Asiento {

    public AsientoEjecutivo(int fila, String letra) {
        super(fila, letra);
    }

    @Override
    public double getFactorPrecio() {
        return 1.8;
    }

    @Override
    public String getClase() {
        return "EJECUTIVA";
    }
}
