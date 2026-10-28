package com.aerogestion.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/** Datos para programar o editar un vuelo. Aeropuertos por código IATA y avión por matrícula. */
@Data
public class VueloForm {

    @NotBlank(message = "El código es obligatorio")
    private String codigoVuelo;

    @NotBlank(message = "Indica el aeropuerto de origen (código IATA, ej. LIM)")
    private String origen;

    @NotBlank(message = "Indica el aeropuerto de destino (código IATA, ej. CUZ)")
    private String destino;

    @NotNull(message = "Indica la salida")
    private LocalDateTime fechaHoraSalida;

    @NotNull(message = "Indica la llegada")
    private LocalDateTime fechaHoraLlegada;

    @NotBlank(message = "Indica la matrícula del avión")
    private String matriculaAvion;

    @Positive(message = "La tarifa debe ser mayor a 0")
    private double tarifaBase;
}
