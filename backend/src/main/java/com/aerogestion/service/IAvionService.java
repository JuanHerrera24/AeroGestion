package com.aerogestion.service;

import java.util.List;

import com.aerogestion.dto.AvionForm;
import com.aerogestion.dto.ConfigAsientosForm;
import com.aerogestion.model.Avion;
import com.aerogestion.model.EstadoAvion;

public interface IAvionService {

    List<Avion> listar();

    List<Avion> listarDisponibles();

    Avion obtener(String matricula);

    Avion crear(AvionForm form);

    Avion cambiarEstado(String matricula, EstadoAvion estado);

    Avion configurarAsientos(String matricula, ConfigAsientosForm form);
}
