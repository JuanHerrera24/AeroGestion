package com.aerogestion.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

/** Petición para reservar uno o más asientos de un vuelo. */
public record ReservaRequest(
        @NotBlank(message = "Indica el código del vuelo") String codigoVuelo,
        @NotEmpty(message = "Selecciona al menos un asiento") List<String> asientos) {
}
