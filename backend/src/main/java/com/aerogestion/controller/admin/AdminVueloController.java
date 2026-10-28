package com.aerogestion.controller.admin;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.aerogestion.dto.EstadoVueloRequest;
import com.aerogestion.dto.VueloDTO;
import com.aerogestion.dto.VueloForm;
import com.aerogestion.model.EstadoVuelo;
import com.aerogestion.service.IVueloService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Gestión de vuelos (HU-06, HU-07, HU-08). Solo administradores. */
@RestController
@RequestMapping("/api/admin/vuelos")
@RequiredArgsConstructor
public class AdminVueloController {

    private final IVueloService vueloService;

    /** Filtros opcionales: ?estado=PROGRAMADO&desde=2026-11-01&hasta=2026-11-05 */
    @GetMapping
    public List<VueloDTO> listar(@RequestParam(required = false) EstadoVuelo estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return vueloService.listar(estado, desde, hasta).stream().map(VueloDTO::de).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VueloDTO programar(@Valid @RequestBody VueloForm form) {
        return VueloDTO.de(vueloService.programar(form));
    }

    @PutMapping("/{codigo}")
    public VueloDTO actualizar(@PathVariable String codigo, @Valid @RequestBody VueloForm form) {
        return VueloDTO.de(vueloService.actualizar(codigo, form));
    }

    /** Cambia el estado (RETRASADO, EMBARCANDO, EN_VUELO, ATERRIZADO...). CANCELADO cancela también las reservas. */
    @PatchMapping("/{codigo}/estado")
    public VueloDTO cambiarEstado(@PathVariable String codigo, @Valid @RequestBody EstadoVueloRequest request) {
        return VueloDTO.de(vueloService.cambiarEstado(codigo, request.estado()));
    }

    @PatchMapping("/{codigo}/cancelar")
    public VueloDTO cancelar(@PathVariable String codigo) {
        vueloService.cancelar(codigo);
        return VueloDTO.de(vueloService.obtener(codigo));
    }
}
