package com.aerogestion.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aerogestion.dto.FilaAsientosDTO;
import com.aerogestion.dto.VueloDTO;
import com.aerogestion.dto.VueloDetalleDTO;
import com.aerogestion.model.Aeropuerto;
import com.aerogestion.service.IBusquedaService;
import com.aerogestion.service.IVueloService;

import lombok.RequiredArgsConstructor;

/** Consulta pública de aeropuertos y vuelos: no requiere iniciar sesión (HU-09, HU-10, HU-11). */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VueloController {

    private final IBusquedaService busquedaService;
    private final IVueloService vueloService;

    @GetMapping("/aeropuertos")
    public List<Aeropuerto> aeropuertos() {
        return vueloService.listarAeropuertos();
    }

    /** Ej: /api/vuelos/buscar?origen=LIM&destino=CUZ&fecha=2026-11-01&orden=precio */
    @GetMapping("/vuelos/buscar")
    public List<VueloDTO> buscar(@RequestParam String origen, @RequestParam String destino,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(defaultValue = "hora") String orden) {
        return busquedaService.buscar(origen, destino, fecha, orden).stream().map(VueloDTO::de).toList();
    }

    @GetMapping("/vuelos/proximos")
    public List<VueloDTO> proximos() {
        return busquedaService.proximosVuelos(10).stream().map(VueloDTO::de).toList();
    }

    @GetMapping("/vuelos/{codigo}")
    public VueloDetalleDTO detalle(@PathVariable String codigo) {
        return new VueloDetalleDTO(VueloDTO.de(vueloService.obtener(codigo)), vueloService.resumenPorClase(codigo));
    }

    /** Mapa de asientos por fila, con precio y si está ocupado. */
    @GetMapping("/vuelos/{codigo}/asientos")
    public List<FilaAsientosDTO> asientos(@PathVariable String codigo) {
        return vueloService.obtenerMapaAsientos(codigo);
    }
}
