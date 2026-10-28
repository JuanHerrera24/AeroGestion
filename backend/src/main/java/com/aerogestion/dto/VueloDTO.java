package com.aerogestion.dto;

import java.time.LocalDateTime;

import com.aerogestion.model.EstadoVuelo;
import com.aerogestion.model.Vuelo;

/** Vuelo tal como lo devuelve la API. */
public record VueloDTO(
        String codigo,
        String origen,
        String ciudadOrigen,
        String destino,
        String ciudadDestino,
        LocalDateTime salida,
        LocalDateTime llegada,
        String duracion,
        double tarifaBase,
        EstadoVuelo estado,
        String avion,
        String modeloAvion,
        int asientosDisponibles,
        double ocupacion) {

    public static VueloDTO de(Vuelo v) {
        return new VueloDTO(
                v.getCodigoVuelo(),
                v.getOrigen().getCodigoIata(),
                v.getOrigen().getCiudad(),
                v.getDestino().getCodigoIata(),
                v.getDestino().getCiudad(),
                v.getFechaHoraSalida(),
                v.getFechaHoraLlegada(),
                v.getDuracionTexto(),
                v.getTarifaBase(),
                v.getEstado(),
                v.getAvion().getMatricula(),
                v.getAvion().getFabricante() + " " + v.getAvion().getModelo(),
                v.getAvion().getCapacidadTotal() - v.getAsientosOcupados().size(),
                Math.round(v.getPorcentajeOcupacion() * 10) / 10.0);
    }
}
