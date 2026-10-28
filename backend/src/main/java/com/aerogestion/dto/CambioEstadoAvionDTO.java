package com.aerogestion.dto;

import java.util.List;

/** Resultado de cambiar el estado de un avión, con los vuelos futuros que quedan afectados. */
public record CambioEstadoAvionDTO(AvionDTO avion, List<String> vuelosAfectados, String advertencia) {
}
