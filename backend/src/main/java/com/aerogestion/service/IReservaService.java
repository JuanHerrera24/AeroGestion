package com.aerogestion.service;

import java.util.List;

import com.aerogestion.model.Reserva;

public interface IReservaService {

    Reserva crear(String codigoVuelo, List<String> numerosAsiento, String emailPasajero);

    List<Reserva> listarPorPasajero(String emailPasajero);

    Reserva obtenerDelPasajero(String codigoReserva, String emailPasajero);

    Reserva cancelar(String codigoReserva, String emailPasajero);

    long contarConfirmadas();
}
