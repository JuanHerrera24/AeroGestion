package com.aerogestion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Precio y disponibilidad de una clase (Primera, Ejecutiva, Económica) en un vuelo. */
@Getter
@AllArgsConstructor
public class ClaseResumenDTO {

    private String clase;
    private double precio;
    private int total;
    private int disponibles;
}
