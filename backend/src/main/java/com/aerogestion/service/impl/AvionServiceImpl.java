package com.aerogestion.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import org.springframework.stereotype.Service;

import com.aerogestion.dto.AvionForm;
import com.aerogestion.dto.ConfigAsientosForm;
import com.aerogestion.exception.RecursoNoEncontradoException;
import com.aerogestion.exception.ReglaNegocioException;
import com.aerogestion.model.Asiento;
import com.aerogestion.model.AsientoEconomico;
import com.aerogestion.model.AsientoEjecutivo;
import com.aerogestion.model.AsientoPrimeraClase;
import com.aerogestion.model.Avion;
import com.aerogestion.model.EstadoAvion;
import com.aerogestion.repository.AvionRepository;
import com.aerogestion.repository.VueloRepository;
import com.aerogestion.service.IAvionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AvionServiceImpl implements IAvionService {

    private static final String[] LETRAS_PREMIUM = {"A", "C", "D", "F"};
    private static final String[] LETRAS_ECONOMICA = {"A", "B", "C", "D", "E", "F"};

    private final AvionRepository avionRepository;
    private final VueloRepository vueloRepository;

    @Override
    public List<Avion> listar() {
        return avionRepository.findAll();
    }

    @Override
    public List<Avion> listarDisponibles() {
        return avionRepository.findByEstado(EstadoAvion.ACTIVO);
    }

    @Override
    public Avion obtener(String matricula) {
        return avionRepository.findByMatricula(matricula.trim().toUpperCase())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un avión con matrícula " + matricula));
    }

    @Override
    public Avion crear(AvionForm form) {
        String matricula = form.getMatricula().trim().toUpperCase();
        if (avionRepository.existsByMatricula(matricula)) {
            throw new ReglaNegocioException("Ya existe un avión con la matrícula " + matricula);
        }
        Avion avion = new Avion();
        avion.setMatricula(matricula);
        avion.setModelo(form.getModelo().trim());
        avion.setFabricante(form.getFabricante().trim());
        avion.setAnioFabricacion(form.getAnioFabricacion());
        avion.setAsientos(generarAsientos(new ConfigAsientosForm(
                form.getFilasPrimera(), form.getFilasEjecutiva(), form.getFilasEconomica())));
        return avionRepository.save(avion);
    }

    @Override
    public Avion cambiarEstado(String matricula, EstadoAvion estado) {
        Avion avion = obtener(matricula);
        avion.cambiarEstado(estado);
        return avionRepository.save(avion);
    }

    @Override
    public Avion configurarAsientos(String matricula, ConfigAsientosForm form) {
        Avion avion = obtener(matricula);
        boolean tieneReservas = vueloRepository.findByAvion(avion).stream()
                .anyMatch(v -> v.estaAbiertoParaReservas() && !v.getAsientosOcupados().isEmpty());
        if (tieneReservas) {
            throw new ReglaNegocioException(
                    "No se puede cambiar la distribución: el avión tiene vuelos futuros con asientos reservados");
        }
        avion.setAsientos(generarAsientos(form));
        return avionRepository.save(avion);
    }

    /** Genera los asientos numerando filas de forma continua: Primera, Ejecutiva, Económica. */
    private List<Asiento> generarAsientos(ConfigAsientosForm form) {
        List<Asiento> asientos = new ArrayList<>();
        int fila = 1;
        fila = agregarFilas(asientos, fila, form.getFilasPrimera(), LETRAS_PREMIUM, AsientoPrimeraClase::new);
        fila = agregarFilas(asientos, fila, form.getFilasEjecutiva(), LETRAS_PREMIUM, AsientoEjecutivo::new);
        agregarFilas(asientos, fila, form.getFilasEconomica(), LETRAS_ECONOMICA, AsientoEconomico::new);
        return asientos;
    }

    private int agregarFilas(List<Asiento> asientos, int filaInicial, int cantidad, String[] letras,
            BiFunction<Integer, String, Asiento> fabrica) {
        for (int f = filaInicial; f < filaInicial + cantidad; f++) {
            for (String letra : letras) {
                asientos.add(fabrica.apply(f, letra));
            }
        }
        return filaInicial + cantidad;
    }
}
