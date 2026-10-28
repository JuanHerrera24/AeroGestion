package com.aerogestion.controller.admin;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aerogestion.dto.ResumenAdminDTO;
import com.aerogestion.dto.VueloDTO;
import com.aerogestion.model.Vuelo;
import com.aerogestion.service.IAvionService;
import com.aerogestion.service.IReservaService;
import com.aerogestion.service.IVueloService;

import lombok.RequiredArgsConstructor;

/** Indicadores del panel de operaciones (HU-08). Solo administradores. */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final IAvionService avionService;
    private final IVueloService vueloService;
    private final IReservaService reservaService;

    @GetMapping("/resumen")
    public ResumenAdminDTO resumen() {
        List<Vuelo> proximos = vueloService.listar(null, LocalDate.now(), LocalDate.now().plusDays(7)).stream()
                .filter(Vuelo::estaAbiertoParaReservas)
                .toList();
        double ocupacionMedia = proximos.stream().mapToDouble(Vuelo::getPorcentajeOcupacion).average().orElse(0);
        return new ResumenAdminDTO(
                avionService.listar().size(),
                avionService.listarDisponibles().size(),
                proximos.size(),
                reservaService.contarConfirmadas(),
                Math.round(ocupacionMedia * 100) / 100.0,
                proximos.stream().limit(5).map(VueloDTO::de).toList());
    }
}
