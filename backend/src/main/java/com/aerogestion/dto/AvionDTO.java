package com.aerogestion.dto;

import com.aerogestion.model.Avion;
import com.aerogestion.model.EstadoAvion;

/** Avión con el resumen de su distribución de asientos. */
public record AvionDTO(
        String matricula,
        String fabricante,
        String modelo,
        int anioFabricacion,
        EstadoAvion estado,
        int capacidad,
        long asientosPrimera,
        long asientosEjecutiva,
        long asientosEconomica) {

    public static AvionDTO de(Avion a) {
        return new AvionDTO(
                a.getMatricula(),
                a.getFabricante(),
                a.getModelo(),
                a.getAnioFabricacion(),
                a.getEstado(),
                a.getCapacidadTotal(),
                contar(a, "PRIMERA"),
                contar(a, "EJECUTIVA"),
                contar(a, "ECONOMICA"));
    }

    private static long contar(Avion a, String clase) {
        return a.getAsientos().stream().filter(s -> s.getClase().equals(clase)).count();
    }
}
