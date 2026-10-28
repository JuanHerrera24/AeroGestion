package com.aerogestion.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Filas por clase. Primera y Ejecutiva usan letras A C D F; Económica usa A-F.
 * Las filas se numeran en orden: primero Primera, luego Ejecutiva y al final Económica.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigAsientosForm {

    @Min(0) @Max(10)
    private int filasPrimera;

    @Min(0) @Max(20)
    private int filasEjecutiva;

    @Min(1) @Max(60)
    private int filasEconomica;
}
