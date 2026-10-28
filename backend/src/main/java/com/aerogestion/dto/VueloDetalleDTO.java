package com.aerogestion.dto;

import java.util.List;

/** Vuelo con el precio y la disponibilidad de cada clase. */
public record VueloDetalleDTO(VueloDTO vuelo, List<ClaseResumenDTO> clases) {
}
