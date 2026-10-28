package com.aerogestion.service;

import java.util.List;

import com.aerogestion.model.Reserva;

public interface IReservaService {

    Reserva crear(String vueloId, List<String> numerosAsiento, String emailPasajero);

    List<Reserva> listarPorPasajero(String emailPasajero);

    Reserva obtenerDelPasajero(String id, String emailPasajero);

    void cancelar(String id, String emailPasajero);

    long contarConfirmadas();
}
