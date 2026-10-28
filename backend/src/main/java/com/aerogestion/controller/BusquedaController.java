package com.aerogestion.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.aerogestion.service.IBusquedaService;
import com.aerogestion.service.IVueloService;

import lombok.RequiredArgsConstructor;

/** Búsqueda y detalle de vuelos: público, no requiere sesión (HU-09, HU-10). */
@Controller
@RequestMapping("/vuelos")
@RequiredArgsConstructor
public class BusquedaController {

    private final IBusquedaService busquedaService;
    private final IVueloService vueloService;

    @GetMapping("/buscar")
    public String buscar(@RequestParam String origen, @RequestParam String destino,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(defaultValue = "hora") String orden, Model model) {
        model.addAttribute("aeropuertos", vueloService.listarAeropuertos());
        model.addAttribute("origen", origen);
        model.addAttribute("destino", destino);
        model.addAttribute("fecha", fecha);
        model.addAttribute("orden", orden);
        if (origen.equals(destino)) {
            model.addAttribute("error", "El origen y el destino deben ser distintos.");
        } else {
            model.addAttribute("resultados", busquedaService.buscar(origen, destino, fecha, orden));
        }
        return "vuelos/resultados";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable String id, Model model) {
        model.addAttribute("vuelo", vueloService.obtener(id));
        model.addAttribute("clases", vueloService.resumenPorClase(id));
        return "vuelos/detalle";
    }
}
