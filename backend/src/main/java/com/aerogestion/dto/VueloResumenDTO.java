package com.aerogestion.dto;

import java.time.Duration;

import com.aerogestion.model.Vuelo;

import lombok.Getter;

/** Datos de un vuelo para listar resultados de búsqueda. */
@Getter
public class VueloResumenDTO {

    private final Vuelo vuelo;
    private final double precioDesde;
    private final int asientosDisponibles;
    private final String duracionTexto;

    public VueloResumenDTO(Vuelo vuelo) {
        this.vuelo = vuelo;
        this.precioDesde = vuelo.getTarifaBase();
        this.asientosDisponibles = vuelo.getAvion().getCapacidadTotal() - vuelo.getAsientosOcupados().size();
        Duration d = vuelo.getDuracion();
        this.duracionTexto = d.toHours() + "h " + d.toMinutesPart() + "m";
    }
}
