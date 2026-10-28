package com.aerogestion.notification;

import com.aerogestion.model.Reserva;

/** Contrato para enviar avisos al pasajero sobre su reserva. */
public interface Notificador {

    void enviarConfirmacion(Reserva reserva);

    void enviarCancelacion(Reserva reserva);
}
