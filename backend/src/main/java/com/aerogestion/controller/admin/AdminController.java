package com.aerogestion.controller.admin;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.aerogestion.model.Vuelo;
import com.aerogestion.service.IAvionService;
import com.aerogestion.service.IReservaService;
import com.aerogestion.service.IVueloService;

import lombok.RequiredArgsConstructor;

/** Panel principal del administrador con indicadores (HU-08). */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final IAvionService avionService;
    private final IVueloService vueloService;
    private final IReservaService reservaService;

    @GetMapping
    public String dashboard(Model model) {
        List<Vuelo> proximos = vueloService.listar(null, LocalDate.now(), LocalDate.now().plusDays(7)).stream()
                .filter(Vuelo::estaAbiertoParaReservas)
                .toList();
        double ocupacionMedia = proximos.stream().mapToDouble(Vuelo::getPorcentajeOcupacion).average().orElse(0);
        model.addAttribute("totalAviones", avionService.listar().size());
        model.addAttribute("avionesActivos", avionService.listarDisponibles().size());
        model.addAttribute("vuelosSemana", proximos.size());
        model.addAttribute("reservasConfirmadas", reservaService.contarConfirmadas());
        model.addAttribute("ocupacionMedia", ocupacionMedia);
        model.addAttribute("proximos", proximos.stream().limit(8).toList());
        return "admin/dashboard";
    }
}
