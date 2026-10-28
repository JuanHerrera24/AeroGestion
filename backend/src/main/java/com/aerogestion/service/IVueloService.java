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

    Vuelo obtener(String id);

    Vuelo programar(VueloForm form);

    Vuelo actualizar(String id, VueloForm form);

    Vuelo cambiarEstado(String id, EstadoVuelo estado);

    void cancelar(String id);

    List<FilaAsientosDTO> obtenerMapaAsientos(String id);

    List<ClaseResumenDTO> resumenPorClase(String id);

    List<Vuelo> listarFuturosPorAvion(Avion avion);

    List<Aeropuerto> listarAeropuertos();

    VueloForm aFormulario(Vuelo vuelo);
}
