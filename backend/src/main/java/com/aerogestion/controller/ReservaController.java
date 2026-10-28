package com.aerogestion.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aerogestion.exception.ReglaNegocioException;
import com.aerogestion.model.Reserva;
import com.aerogestion.model.Vuelo;
import com.aerogestion.service.IReservaService;
import com.aerogestion.service.IVueloService;

import lombok.RequiredArgsConstructor;

/** Selección de asientos y reservas del pasajero (HU-11 a HU-14). */
@Controller
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final IReservaService reservaService;
    private final IVueloService vueloService;

    @GetMapping("/vuelo/{vueloId}")
    public String mapaAsientos(@PathVariable String vueloId, Model model) {
        Vuelo vuelo = vueloService.obtener(vueloId);
        if (!vuelo.estaAbiertoParaReservas()) {
            throw new ReglaNegocioException("El vuelo " + vuelo.getCodigoVuelo() + " ya no admite reservas");
        }
        model.addAttribute("vuelo", vuelo);
        model.addAttribute("filas", vueloService.obtenerMapaAsientos(vueloId));
        model.addAttribute("clases", vueloService.resumenPorClase(vueloId));
        return "reservas/asientos";
    }

    @PostMapping
    public String crear(@RequestParam String vueloId,
            @RequestParam(name = "asientos", required = false) List<String> asientos,
            Principal principal, RedirectAttributes flash) {
        try {
            Reserva reserva = reservaService.crear(vueloId, asientos, principal.getName());
            flash.addFlashAttribute("exito", "¡Reserva confirmada! Te enviamos el detalle por correo.");
            return "redirect:/reservas/" + reserva.getId();
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/reservas/vuelo/" + vueloId;
        }
    }

    @GetMapping
    public String misReservas(Principal principal, Model model) {
        model.addAttribute("reservas", reservaService.listarPorPasajero(principal.getName()));
        return "reservas/mias";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable String id, Principal principal, Model model) {
        model.addAttribute("reserva", reservaService.obtenerDelPasajero(id, principal.getName()));
        return "reservas/detalle";
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable String id, Principal principal, RedirectAttributes flash) {
        try {
            reservaService.cancelar(id, principal.getName());
            flash.addFlashAttribute("exito", "Reserva cancelada. Los asientos quedaron libres.");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/reservas";
    }
}
