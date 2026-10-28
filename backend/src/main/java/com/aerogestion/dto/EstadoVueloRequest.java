package com.aerogestion.dto;

import com.aerogestion.model.EstadoVuelo;

import jakarta.validation.constraints.NotNull;

public record EstadoVueloRequest(@NotNull(message = "Indica el nuevo estado") EstadoVuelo estado) {
}
