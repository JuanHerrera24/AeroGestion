package com.aerogestion.controller.admin;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aerogestion.dto.VueloForm;
import com.aerogestion.exception.ReglaNegocioException;
import com.aerogestion.model.EstadoVuelo;
import com.aerogestion.model.Vuelo;
import com.aerogestion.service.IAvionService;
import com.aerogestion.service.IVueloService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Gestión de vuelos (HU-06, HU-07, HU-08). */
@Controller
@RequestMapping("/admin/vuelos")
@RequiredArgsConstructor
public class AdminVueloController {

    private final IVueloService vueloService;
    private final IAvionService avionService;

    @GetMapping
    public String listar(@RequestParam(required = false) EstadoVuelo estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Model model) {
        model.addAttribute("vuelos", vueloService.listar(estado, desde, hasta));
        model.addAttribute("estados", EstadoVuelo.values());
        model.addAttribute("estado", estado);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        return "admin/vuelos";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        prepararFormulario(model, new VueloForm(), null);
        return "admin/vuelo-form";
    }

    @PostMapping
    public String programar(@Valid @ModelAttribute("form") VueloForm form, BindingResult result,
            Model model, RedirectAttributes flash) {
        if (!result.hasErrors()) {
            try {
                Vuelo vuelo = vueloService.programar(form);
                flash.addFlashAttribute("exito", "Vuelo " + vuelo.getCodigoVuelo() + " programado.");
                return "redirect:/admin/vuelos";
            } catch (ReglaNegocioException e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        prepararFormulario(model, form, null);
        return "admin/vuelo-form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable String id, Model model) {
        Vuelo vuelo = vueloService.obtener(id);
        prepararFormulario(model, vueloService.aFormulario(vuelo), vuelo);
        return "admin/vuelo-form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable String id, @Valid @ModelAttribute("form") VueloForm form,
            BindingResult result, Model model, RedirectAttributes flash) {
        Vuelo vuelo = vueloService.obtener(id);
        if (!result.hasErrors()) {
            try {
                vueloService.actualizar(id, form);
                flash.addFlashAttribute("exito", "Vuelo " + vuelo.getCodigoVuelo() + " actualizado.");
                return "redirect:/admin/vuelos";
            } catch (ReglaNegocioException e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        prepararFormulario(model, form, vuelo);
        return "admin/vuelo-form";
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable String id, @RequestParam EstadoVuelo estado,
            RedirectAttributes flash) {
        try {
            Vuelo vuelo = vueloService.cambiarEstado(id, estado);
            flash.addFlashAttribute("exito", "Vuelo " + vuelo.getCodigoVuelo() + " ahora está " + estado + ".");
        } catch (ReglaNegocioException | IllegalStateException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/vuelos";
    }

    private void prepararFormulario(Model model, VueloForm form, Vuelo vuelo) {
        model.addAttribute("form", form);
        model.addAttribute("vuelo", vuelo);
        model.addAttribute("aeropuertos", vueloService.listarAeropuertos());
        model.addAttribute("aviones", avionService.listarDisponibles());
    }
}
