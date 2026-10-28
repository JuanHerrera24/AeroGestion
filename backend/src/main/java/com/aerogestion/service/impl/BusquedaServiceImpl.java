package com.aerogestion.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.aerogestion.dto.VueloResumenDTO;
import com.aerogestion.exception.RecursoNoEncontradoException;
import com.aerogestion.model.Aeropuerto;
import com.aerogestion.model.EstadoVuelo;
import com.aerogestion.model.Vuelo;
import com.aerogestion.repository.AeropuertoRepository;
import com.aerogestion.repository.VueloRepository;
import com.aerogestion.service.IBusquedaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BusquedaServiceImpl implements IBusquedaService {

    private final VueloRepository vueloRepository;
    private final AeropuertoRepository aeropuertoRepository;

    @Override
    public List<VueloResumenDTO> buscar(String origenId, String destinoId, LocalDate fecha, String orden) {
        Aeropuerto origen = obtenerAeropuerto(origenId);
        Aeropuerto destino = obtenerAeropuerto(destinoId);
        Comparator<VueloResumenDTO> comparador = "precio".equals(orden)
                ? Comparator.comparingDouble(VueloResumenDTO::getPrecioDesde)
                : Comparator.comparing(r -> r.getVuelo().getFechaHoraSalida());
        return vueloRepository.findByOrigenAndDestinoAndFechaHoraSalidaBetween(
                        origen, destino, fecha.atStartOfDay(), fecha.plusDays(1).atStartOfDay())
                .stream()
                .filter(Vuelo::estaAbiertoParaReservas)
                .map(VueloResumenDTO::new)
                .sorted(comparador)
                .toList();
    }

    @Override
    public List<VueloResumenDTO> proximosVuelos(int limite) {
        return vueloRepository.findByFechaHoraSalidaAfterAndEstadoInOrderByFechaHoraSalida(
                        LocalDateTime.now(), List.of(EstadoVuelo.PROGRAMADO, EstadoVuelo.RETRASADO))
                .stream()
                .limit(limite)
                .map(VueloResumenDTO::new)
                .toList();
    }

    private Aeropuerto obtenerAeropuerto(String id) {
        return aeropuertoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Aeropuerto no encontrado"));
    }
}
