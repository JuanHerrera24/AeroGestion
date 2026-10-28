package com.aerogestion.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import com.aerogestion.dto.AsientoDTO;
import com.aerogestion.dto.ClaseResumenDTO;
import com.aerogestion.dto.FilaAsientosDTO;
import com.aerogestion.dto.VueloForm;
import com.aerogestion.exception.RecursoNoEncontradoException;
import com.aerogestion.exception.ReglaNegocioException;
import com.aerogestion.model.Aeropuerto;
import com.aerogestion.model.Asiento;
import com.aerogestion.model.Avion;
import com.aerogestion.model.EstadoReserva;
import com.aerogestion.model.EstadoVuelo;
import com.aerogestion.model.Reserva;
import com.aerogestion.model.Vuelo;
import com.aerogestion.notification.Notificador;
import com.aerogestion.repository.AeropuertoRepository;
import com.aerogestion.repository.ReservaRepository;
import com.aerogestion.repository.VueloRepository;
import com.aerogestion.service.IAvionService;
import com.aerogestion.service.IVueloService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VueloServiceImpl implements IVueloService {

    private final VueloRepository vueloRepository;
    private final AeropuertoRepository aeropuertoRepository;
    private final ReservaRepository reservaRepository;
    private final IAvionService avionService;
    private final Notificador notificador;

    @Override
    public List<Vuelo> listar(EstadoVuelo estado, LocalDate desde, LocalDate hasta) {
        return vueloRepository.findAllByOrderByFechaHoraSalidaAsc().stream()
                .filter(v -> estado == null || v.getEstado() == estado)
                .filter(v -> desde == null || !v.getFechaHoraSalida().toLocalDate().isBefore(desde))
                .filter(v -> hasta == null || !v.getFechaHoraSalida().toLocalDate().isAfter(hasta))
                .toList();
    }

    @Override
    public Vuelo obtener(String id) {
        return vueloRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Vuelo no encontrado"));
    }

    @Override
    public Vuelo programar(VueloForm form) {
        String codigo = form.getCodigoVuelo().trim().toUpperCase();
        if (vueloRepository.existsByCodigoVuelo(codigo)) {
            throw new ReglaNegocioException("Ya existe un vuelo con el código " + codigo);
        }
        if (!form.getFechaHoraSalida().isAfter(LocalDateTime.now())) {
            throw new ReglaNegocioException("La salida debe ser una fecha futura");
        }
        Vuelo vuelo = new Vuelo();
        vuelo.setCodigoVuelo(codigo);
        aplicarFormulario(vuelo, form);
        return vueloRepository.save(vuelo);
    }

    @Override
    public Vuelo actualizar(String id, VueloForm form) {
        Vuelo vuelo = obtener(id);
        if (!vuelo.puedeCancelarse()) {
            throw new ReglaNegocioException("Solo se pueden editar vuelos programados o retrasados");
        }
        boolean cambiaAvion = !vuelo.getAvion().getId().equals(form.getAvionId());
        if (cambiaAvion && !vuelo.getAsientosOcupados().isEmpty()) {
            throw new ReglaNegocioException("No se puede cambiar el avión de un vuelo con asientos reservados");
        }
        aplicarFormulario(vuelo, form);
        return vueloRepository.save(vuelo);
    }

    /** Valida las reglas de HU-06 y copia los datos del formulario al vuelo. */
    private void aplicarFormulario(Vuelo vuelo, VueloForm form) {
        if (form.getOrigenId().equals(form.getDestinoId())) {
            throw new ReglaNegocioException("El origen y el destino no pueden ser iguales");
        }
        if (!form.getFechaHoraLlegada().isAfter(form.getFechaHoraSalida())) {
            throw new ReglaNegocioException("La llegada debe ser posterior a la salida");
        }
        Avion avion = avionService.obtener(form.getAvionId());
        if (!avion.estaDisponible()) {
            throw new ReglaNegocioException("El avión " + avion.getMatricula() + " no está activo");
        }
        if (avion.getCapacidadTotal() == 0) {
            throw new ReglaNegocioException("El avión " + avion.getMatricula() + " no tiene asientos configurados");
        }
        boolean cruce = vueloRepository.findByAvion(avion).stream()
                .filter(otro -> !otro.getId().equals(vuelo.getId()))
                .filter(otro -> otro.getEstado() != EstadoVuelo.CANCELADO)
                .anyMatch(otro -> form.getFechaHoraSalida().isBefore(otro.getFechaHoraLlegada())
                        && otro.getFechaHoraSalida().isBefore(form.getFechaHoraLlegada()));
        if (cruce) {
            throw new ReglaNegocioException("El avión " + avion.getMatricula() + " ya tiene otro vuelo en ese horario");
        }
        vuelo.setOrigen(obtenerAeropuerto(form.getOrigenId()));
        vuelo.setDestino(obtenerAeropuerto(form.getDestinoId()));
        vuelo.setFechaHoraSalida(form.getFechaHoraSalida());
        vuelo.setFechaHoraLlegada(form.getFechaHoraLlegada());
        vuelo.setAvion(avion);
        vuelo.setTarifaBase(form.getTarifaBase());
    }

    private Aeropuerto obtenerAeropuerto(String id) {
        return aeropuertoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Aeropuerto no encontrado"));
    }

    @Override
    public Vuelo cambiarEstado(String id, EstadoVuelo estado) {
        if (estado == EstadoVuelo.CANCELADO) {
            cancelar(id);
            return obtener(id);
        }
        Vuelo vuelo = obtener(id);
        if (vuelo.getEstado() == EstadoVuelo.CANCELADO) {
            throw new ReglaNegocioException("El vuelo está cancelado");
        }
        vuelo.setEstado(estado);
        return vueloRepository.save(vuelo);
    }

    @Override
    public void cancelar(String id) {
        Vuelo vuelo = obtener(id);
        vuelo.cancelar();
        vueloRepository.save(vuelo);
        for (Reserva reserva : reservaRepository.findByVuelo(vuelo)) {
            if (reserva.getEstado() == EstadoReserva.CONFIRMADA) {
                reserva.anularPorCancelacionDeVuelo();
                reservaRepository.save(reserva);
                notificador.enviarCancelacion(reserva);
            }
        }
    }

    @Override
    public List<FilaAsientosDTO> obtenerMapaAsientos(String id) {
        Vuelo vuelo = obtener(id);
        Map<Integer, List<AsientoDTO>> filas = new TreeMap<>();
        Map<Integer, String> claseFila = new TreeMap<>();
        for (Asiento a : vuelo.getAvion().getAsientos()) {
            filas.computeIfAbsent(a.getFila(), f -> new ArrayList<>()).add(new AsientoDTO(
                    a.getNumero(),
                    a.getLetra(),
                    a.getClase(),
                    a.calcularPrecio(vuelo.getTarifaBase()),
                    vuelo.getAsientosOcupados().contains(a.getNumero()),
                    a.getLetra().equals("C")));
            claseFila.put(a.getFila(), a.getClase());
        }
        return filas.entrySet().stream()
                .map(e -> new FilaAsientosDTO(e.getKey(), claseFila.get(e.getKey()), e.getValue()))
                .toList();
    }

    @Override
    public List<ClaseResumenDTO> resumenPorClase(String id) {
        Map<String, List<AsientoDTO>> porClase = new LinkedHashMap<>();
        for (FilaAsientosDTO fila : obtenerMapaAsientos(id)) {
            porClase.computeIfAbsent(fila.getClase(), c -> new ArrayList<>()).addAll(fila.getAsientos());
        }
        return porClase.entrySet().stream()
                .map(e -> new ClaseResumenDTO(
                        e.getKey(),
                        e.getValue().get(0).getPrecio(),
                        e.getValue().size(),
                        (int) e.getValue().stream().filter(a -> !a.isOcupado()).count()))
                .toList();
    }

    @Override
    public List<Vuelo> listarFuturosPorAvion(Avion avion) {
        return vueloRepository.findByAvion(avion).stream()
                .filter(Vuelo::estaAbiertoParaReservas)
                .toList();
    }

    @Override
    public List<Aeropuerto> listarAeropuertos() {
        return aeropuertoRepository.findAll();
    }

    @Override
    public VueloForm aFormulario(Vuelo vuelo) {
        VueloForm form = new VueloForm();
        form.setCodigoVuelo(vuelo.getCodigoVuelo());
        form.setOrigenId(vuelo.getOrigen().getId());
        form.setDestinoId(vuelo.getDestino().getId());
        form.setFechaHoraSalida(vuelo.getFechaHoraSalida());
        form.setFechaHoraLlegada(vuelo.getFechaHoraLlegada());
        form.setAvionId(vuelo.getAvion().getId());
        form.setTarifaBase(vuelo.getTarifaBase());
        return form;
    }
}
