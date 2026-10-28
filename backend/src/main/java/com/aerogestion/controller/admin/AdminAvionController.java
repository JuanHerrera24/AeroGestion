package com.aerogestion.controller.admin;

import java.util.List;

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

import com.aerogestion.dto.AvionForm;
import com.aerogestion.dto.ConfigAsientosForm;
import com.aerogestion.exception.ReglaNegocioException;
import com.aerogestion.model.Avion;
import com.aerogestion.model.EstadoAvion;
import com.aerogestion.model.Vuelo;
import com.aerogestion.service.IAvionService;
import com.aerogestion.service.IVueloService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Gestión de flota (HU-03, HU-04, HU-05). */
@Controller
@RequestMapping("/admin/aviones")
@RequiredArgsConstructor
public class AdminAvionController {

    private final IAvionService avionService;
    private final IVueloService vueloService;

    @GetMapping
    public String listar(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new AvionForm());
        }
        model.addAttribute("aviones", avionService.listar());
        model.addAttribute("estados", EstadoAvion.values());
        return "admin/aviones";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("form") AvionForm form, BindingResult result,
            Model model, RedirectAttributes flash) {
        if (!result.hasErrors()) {
            try {
                Avion avion = avionService.crear(form);
                flash.addFlashAttribute("exito", "Avión " + avion.getMatricula() + " registrado con "
                        + avion.getCapacidadTotal() + " asientos.");
                return "redirect:/admin/aviones";
            } catch (ReglaNegocioException e) {
                result.rejectValue("matricula", "duplicada", e.getMessage());
            }
        }
        model.addAttribute("aviones", avionService.listar());
        model.addAttribute("estados", EstadoAvion.values());
        return "admin/aviones";
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable String id, @RequestParam EstadoAvion estado,
            RedirectAttributes flash) {
        Avion avion = avionService.cambiarEstado(id, estado);
        List<Vuelo> afectados = vueloService.listarFuturosPorAvion(avion);
        if (estado != EstadoAvion.ACTIVO && !afectados.isEmpty()) {
            flash.addFlashAttribute("advertencia", "Atención: " + avion.getMatricula() + " tiene "
                    + afectados.size() + " vuelos programados. Reasígnalos o cancélalos: "
                    + String.join(", ", afectados.stream().map(Vuelo::getCodigoVuelo).limit(10).toList())
                    + (afectados.size() > 10 ? "…" : ""));
        } else {
            flash.addFlashAttribute("exito", "Estado de " + avion.getMatricula() + " actualizado.");
        }
        return "redirect:/admin/aviones";
    }

    @GetMapping("/{id}/asientos")
    public String verAsientos(@PathVariable String id, Model model) {
        Avion avion = avionService.obtener(id);
        model.addAttribute("avion", avion);
        model.addAttribute("form", avionService.obtenerConfiguracion(avion));
        return "admin/avion-asientos";
    }

    @PostMapping("/{id}/asientos")
    public String configurarAsientos(@PathVariable String id, @Valid @ModelAttribute("form") ConfigAsientosForm form,
            BindingResult result, Model model, RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("avion", avionService.obtener(id));
            return "admin/avion-asientos";
        }
        try {
            Avion avion = avionService.configurarAsientos(id, form);
            flash.addFlashAttribute("exito", "Distribución actualizada: " + avion.getCapacidadTotal() + " asientos.");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/aviones/" + id + "/asientos";
    }
}
