package com.aerogestion.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "vuelos")
public class Vuelo extends EntidadBase implements Cancelable {

    @Indexed(unique = true)
    private String codigoVuelo;

    @DBRef
    private Aeropuerto origen;

    @DBRef
    private Aeropuerto destino;

    private LocalDateTime fechaHoraSalida;
    private LocalDateTime fechaHoraLlegada;
    private double tarifaBase;
    private EstadoVuelo estado = EstadoVuelo.PROGRAMADO;

    @DBRef
    private Avion avion;

    /** Números de asiento ya reservados en este vuelo (ej. "12A"). */
    private Set<String> asientosOcupados = new HashSet<>();

    public Duration getDuracion() {
        return Duration.between(fechaHoraSalida, fechaHoraLlegada);
    }

    public String getDuracionTexto() {
        Duration d = getDuracion();
        return d.toHours() + "h " + d.toMinutesPart() + "m";
    }

    public boolean asientoDisponible(String numero) {
        return avion.buscarAsiento(numero) != null && !asientosOcupados.contains(numero);
    }

    public void ocuparAsiento(String numero) {
        if (!asientoDisponible(numero)) {
            throw new IllegalStateException("El asiento " + numero + " no está disponible");
        }
        asientosOcupados.add(numero);
    }

    public void liberarAsiento(String numero) {
        asientosOcupados.remove(numero);
    }

    public boolean estaAbiertoParaReservas() {
        return (estado == EstadoVuelo.PROGRAMADO || estado == EstadoVuelo.RETRASADO)
                && fechaHoraSalida.isAfter(LocalDateTime.now());
    }

    public double getPorcentajeOcupacion() {
        int capacidad = avion.getCapacidadTotal();
        return capacidad == 0 ? 0 : asientosOcupados.size() * 100.0 / capacidad;
    }

    @Override
    public void cancelar() {
        if (!puedeCancelarse()) {
            throw new IllegalStateException("El vuelo " + codigoVuelo + " no puede cancelarse");
        }
        estado = EstadoVuelo.CANCELADO;
    }

    @Override
    public boolean puedeCancelarse() {
        return estado == EstadoVuelo.PROGRAMADO || estado == EstadoVuelo.RETRASADO;
    }
}
