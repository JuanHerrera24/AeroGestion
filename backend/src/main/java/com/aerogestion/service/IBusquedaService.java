package com.aerogestion.service;

import java.time.LocalDate;
import java.util.List;

import com.aerogestion.model.Vuelo;

public interface IBusquedaService {

    /**
     * @param origen  código IATA del aeropuerto de origen (ej. LIM)
     * @param destino código IATA del aeropuerto de destino (ej. CUZ)
     * @param orden   "precio" u "hora" (por defecto)
     */
    List<Vuelo> buscar(String origen, String destino, LocalDate fecha, String orden);

    List<Vuelo> proximosVuelos(int limite);
}
