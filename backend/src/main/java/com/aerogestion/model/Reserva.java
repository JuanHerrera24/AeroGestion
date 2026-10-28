package com.aerogestion.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "reservas")
public class Reserva extends EntidadBase implements Cancelable {

    /** Horas mínimas antes de la salida para poder cancelar. */
    private static final int HORAS_MINIMAS_CANCELACION = 24;

    @Indexed(unique = true)
    private String codigoReserva;

    @DBRef
    private Pasajero pasajero;

    @DBRef
    private Vuelo vuelo;

    /** Agregación: copia de los asientos elegidos (con su clase) al momento de reservar. */
    private List<Asiento> asientos = new ArrayList<>();

    private LocalDateTime fechaReserva = LocalDateTime.now();
    private EstadoReserva estado = EstadoReserva.PENDIENTE;
    private double total;

    public double calcularTotal() {
        double suma = asientos.stream()
                .mapToDouble(a -> a.calcularPrecio(vuelo.getTarifaBase()))
                .sum();
        total = Math.round(suma * 100) / 100.0;
        return total;
    }

    /** Los asientos se ocupan en el vuelo de forma atómica desde el servicio antes de confirmar. */
    public void confirmar() {
        calcularTotal();
        estado = EstadoReserva.CONFIRMADA;
    }

    @Override
    public void cancelar() {
        if (!puedeCancelarse()) {
            throw new IllegalStateException("Solo se puede cancelar hasta "
                    + HORAS_MINIMAS_CANCELACION + " horas antes del vuelo");
        }
        estado = EstadoReserva.CANCELADA;
    }

    /** Cancelación forzada cuando la aerolínea cancela el vuelo (no aplica la regla de 24 h). */
    public void anularPorCancelacionDeVuelo() {
        estado = EstadoReserva.CANCELADA;
    }

    public List<String> getNumerosAsiento() {
        return asientos.stream().map(Asiento::getNumero).toList();
    }

    @Override
    public boolean puedeCancelarse() {
        return estado != EstadoReserva.CANCELADA
                && LocalDateTime.now().plusHours(HORAS_MINIMAS_CANCELACION)
                        .isBefore(vuelo.getFechaHoraSalida());
    }
}
