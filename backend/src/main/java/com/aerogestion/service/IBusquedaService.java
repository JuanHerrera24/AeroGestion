package com.aerogestion.service;

import java.time.LocalDate;
import java.util.List;

import com.aerogestion.dto.VueloResumenDTO;

public interface IBusquedaService {

    /** @param orden "precio" u "hora" (por defecto). */
    List<VueloResumenDTO> buscar(String origenId, String destinoId, LocalDate fecha, String orden);

    List<VueloResumenDTO> proximosVuelos(int limite);
}
