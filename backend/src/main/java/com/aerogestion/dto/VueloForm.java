package com.aerogestion.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class VueloForm {

    @NotBlank(message = "El código es obligatorio")
    private String codigoVuelo;

    @NotBlank(message = "Elige el origen")
    private String origenId;

    @NotBlank(message = "Elige el destino")
    private String destinoId;

    @NotNull(message = "Indica la salida")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime fechaHoraSalida;

    @NotNull(message = "Indica la llegada")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime fechaHoraLlegada;

    @NotBlank(message = "Elige el avión")
    private String avionId;

    @Positive(message = "La tarifa debe ser mayor a 0")
    private double tarifaBase;
}
