package com.aerogestion.service;

import java.time.LocalDate;
import java.util.List;

import com.aerogestion.dto.ClaseResumenDTO;
import com.aerogestion.dto.FilaAsientosDTO;
import com.aerogestion.dto.VueloForm;
import com.aerogestion.model.Aeropuerto;
import com.aerogestion.model.Avion;
import com.aerogestion.model.EstadoVuelo;
import com.aerogestion.model.Vuelo;

public interface IVueloService {

    List<Vuelo> listar(EstadoVuelo estado, LocalDate desde, LocalDate hasta);

    Vuelo obtener(String codigoVuelo);

    Vuelo programar(VueloForm form);

    Vuelo actualizar(String codigoVuelo, VueloForm form);

    Vuelo cambiarEstado(String codigoVuelo, EstadoVuelo estado);

    void cancelar(String codigoVuelo);

    List<FilaAsientosDTO> obtenerMapaAsientos(String codigoVuelo);

    List<ClaseResumenDTO> resumenPorClase(String codigoVuelo);

    List<Vuelo> listarFuturosPorAvion(Avion avion);

    List<Aeropuerto> listarAeropuertos();
}
