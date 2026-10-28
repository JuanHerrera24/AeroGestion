package com.aerogestion.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Una fila del mapa de asientos. */
@Getter
@AllArgsConstructor
public class FilaAsientosDTO {

    private int fila;
    private String clase;
    private List<AsientoDTO> asientos;
}
