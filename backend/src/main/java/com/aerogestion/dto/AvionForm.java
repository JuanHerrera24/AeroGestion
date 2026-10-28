package com.aerogestion.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AvionForm {

    @NotBlank(message = "La matrícula es obligatoria")
    private String matricula;

    @NotBlank(message = "El modelo es obligatorio")
    private String modelo;

    @NotBlank(message = "El fabricante es obligatorio")
    private String fabricante;

    @Min(value = 1970, message = "Año inválido")
    @Max(value = 2100, message = "Año inválido")
    private int anioFabricacion = 2020;

    /** Distribución inicial de asientos. */
    @Min(0) @Max(10)
    private int filasPrimera = 2;

    @Min(0) @Max(20)
    private int filasEjecutiva = 4;

    @Min(1) @Max(60)
    private int filasEconomica = 20;
}
