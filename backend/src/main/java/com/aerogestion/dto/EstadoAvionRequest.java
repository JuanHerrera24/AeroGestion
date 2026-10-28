package com.aerogestion.dto;

import com.aerogestion.model.EstadoAvion;

import jakarta.validation.constraints.NotNull;

public record EstadoAvionRequest(@NotNull(message = "Indica el nuevo estado") EstadoAvion estado) {
}
