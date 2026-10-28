package com.aerogestion.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.aerogestion.dto.RegistroForm;
import com.aerogestion.dto.UsuarioDTO;
import com.aerogestion.service.IAuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Registro de pasajeros y consulta del usuario autenticado (HU-01, HU-02). */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioDTO registrar(@Valid @RequestBody RegistroForm form) {
        return UsuarioDTO.de(authService.registrar(form));
    }

    /** Sirve para comprobar el inicio de sesión: devuelve quién eres y tu rol. */
    @GetMapping("/yo")
    public UsuarioDTO yo(Principal principal) {
        return UsuarioDTO.de(authService.obtenerPorEmail(principal.getName()));
    }
}
