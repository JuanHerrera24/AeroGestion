package com.aerogestion.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Respuesta JSON cuando una petición falla. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorDTO(int status, String error, String mensaje, Map<String, String> campos, LocalDateTime fecha) {

    public ErrorDTO(int status, String error, String mensaje) {
        this(status, error, mensaje, null, LocalDateTime.now());
    }
}
