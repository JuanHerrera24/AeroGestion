package com.aerogestion.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.aerogestion.dto.ReservaDTO;
import com.aerogestion.dto.ReservaRequest;
import com.aerogestion.service.IReservaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Reservas del pasajero autenticado (HU-12, HU-13, HU-14). */
@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final IReservaService reservaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservaDTO crear(@Valid @RequestBody ReservaRequest request, Principal principal) {
        return ReservaDTO.de(reservaService.crear(request.codigoVuelo(), request.asientos(), principal.getName()));
    }

    @GetMapping
    public List<ReservaDTO> misReservas(Principal principal) {
        return reservaService.listarPorPasajero(principal.getName()).stream().map(ReservaDTO::de).toList();
    }

    @GetMapping("/{codigo}")
    public ReservaDTO detalle(@PathVariable String codigo, Principal principal) {
        return ReservaDTO.de(reservaService.obtenerDelPasajero(codigo, principal.getName()));
    }

    @PatchMapping("/{codigo}/cancelar")
    public ReservaDTO cancelar(@PathVariable String codigo, Principal principal) {
        return ReservaDTO.de(reservaService.cancelar(codigo, principal.getName()));
    }
}
