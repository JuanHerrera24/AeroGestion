package com.aerogestion.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.aerogestion.exception.RecursoNoEncontradoException;
import com.aerogestion.exception.ReglaNegocioException;
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
    public List<Vuelo> buscar(String origen, String destino, LocalDate fecha, String orden) {
        if (origen.trim().equalsIgnoreCase(destino.trim())) {
            throw new ReglaNegocioException("El origen y el destino deben ser distintos");
        }
        Comparator<Vuelo> comparador = "precio".equals(orden)
                ? Comparator.comparingDouble(Vuelo::getTarifaBase)
                : Comparator.comparing(Vuelo::getFechaHoraSalida);
        return vueloRepository.findByOrigenAndDestinoAndFechaHoraSalidaBetween(
                        obtenerAeropuerto(origen), obtenerAeropuerto(destino),
                        fecha.atStartOfDay(), fecha.plusDays(1).atStartOfDay())
                .stream()
                .filter(Vuelo::estaAbiertoParaReservas)
                .sorted(comparador)
                .toList();
    }

    @Override
    public List<Vuelo> proximosVuelos(int limite) {
        return vueloRepository.findByFechaHoraSalidaAfterAndEstadoInOrderByFechaHoraSalida(
                        LocalDateTime.now(), List.of(EstadoVuelo.PROGRAMADO, EstadoVuelo.RETRASADO))
                .stream()
                .limit(limite)
                .toList();
    }

    private Aeropuerto obtenerAeropuerto(String codigoIata) {
        return aeropuertoRepository.findByCodigoIata(codigoIata.trim().toUpperCase())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el aeropuerto " + codigoIata));
    }
}
