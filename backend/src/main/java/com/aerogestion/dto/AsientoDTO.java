package com.aerogestion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Un asiento tal como se muestra en el mapa del vuelo. */
@Getter
@AllArgsConstructor
public class AsientoDTO {

    private String numero;
    private String letra;
    private String clase;
    private double precio;
    private boolean ocupado;
    /** Hay pasillo a la derecha de este asiento. */
    private boolean pasillo;
}
