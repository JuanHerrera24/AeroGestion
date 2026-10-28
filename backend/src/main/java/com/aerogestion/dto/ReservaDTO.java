package com.aerogestion.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.aerogestion.model.EstadoReserva;
import com.aerogestion.model.Reserva;

/** Reserva tal como la devuelve la API. */
public record ReservaDTO(
        String codigo,
        String vuelo,
        String ruta,
        LocalDateTime salida,
        String pasajero,
        List<AsientoReservado> asientos,
        double total,
        EstadoReserva estado,
        LocalDateTime fechaReserva,
        boolean cancelable) {

    public record AsientoReservado(String numero, String clase, double precio) {
    }

    public static ReservaDTO de(Reserva r) {
        double tarifa = r.getVuelo().getTarifaBase();
        return new ReservaDTO(
                r.getCodigoReserva(),
                r.getVuelo().getCodigoVuelo(),
                r.getVuelo().getOrigen().getCodigoIata() + " → " + r.getVuelo().getDestino().getCodigoIata(),
                r.getVuelo().getFechaHoraSalida(),
                r.getPasajero().getNombreCompleto(),
                r.getAsientos().stream()
                        .map(a -> new AsientoReservado(a.getNumero(), a.getClase(), a.calcularPrecio(tarifa)))
                        .toList(),
                r.getTotal(),
                r.getEstado(),
                r.getFechaReserva(),
                r.puedeCancelarse());
    }
}
