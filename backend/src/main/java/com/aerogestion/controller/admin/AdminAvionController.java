package com.aerogestion.controller.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.aerogestion.dto.AvionDTO;
import com.aerogestion.dto.AvionForm;
import com.aerogestion.dto.CambioEstadoAvionDTO;
import com.aerogestion.dto.ConfigAsientosForm;
import com.aerogestion.dto.EstadoAvionRequest;
import com.aerogestion.model.Avion;
import com.aerogestion.model.EstadoAvion;
import com.aerogestion.model.Vuelo;
import com.aerogestion.service.IAvionService;
import com.aerogestion.service.IVueloService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Gestión de la flota (HU-03, HU-04, HU-05). Solo administradores. */
@RestController
@RequestMapping("/api/admin/aviones")
@RequiredArgsConstructor
public class AdminAvionController {

    private final IAvionService avionService;
    private final IVueloService vueloService;

    @GetMapping
    public List<AvionDTO> listar() {
        return avionService.listar().stream().map(AvionDTO::de).toList();
    }

    @GetMapping("/{matricula}")
    public AvionDTO detalle(@PathVariable String matricula) {
        return AvionDTO.de(avionService.obtener(matricula));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AvionDTO registrar(@Valid @RequestBody AvionForm form) {
        return AvionDTO.de(avionService.crear(form));
    }

    /** Cambia el estado y avisa qué vuelos futuros quedan afectados si el avión deja de estar activo. */
    @PatchMapping("/{matricula}/estado")
    public CambioEstadoAvionDTO cambiarEstado(@PathVariable String matricula,
            @Valid @RequestBody EstadoAvionRequest request) {
        Avion avion = avionService.cambiarEstado(matricula, request.estado());
        List<String> afectados = request.estado() == EstadoAvion.ACTIVO
                ? List.of()
                : vueloService.listarFuturosPorAvion(avion).stream().map(Vuelo::getCodigoVuelo).toList();
        String advertencia = afectados.isEmpty() ? null
                : "El avión tiene " + afectados.size() + " vuelos programados: reasígnalos o cancélalos";
        return new CambioEstadoAvionDTO(AvionDTO.de(avion), afectados, advertencia);
    }

    @PutMapping("/{matricula}/asientos")
    public AvionDTO configurarAsientos(@PathVariable String matricula, @Valid @RequestBody ConfigAsientosForm form) {
        return AvionDTO.de(avionService.configurarAsientos(matricula, form));
    }
}
