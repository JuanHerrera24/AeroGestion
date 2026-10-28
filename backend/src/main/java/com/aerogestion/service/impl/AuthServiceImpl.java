package com.aerogestion.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aerogestion.dto.RegistroForm;
import com.aerogestion.exception.ReglaNegocioException;
import com.aerogestion.model.Pasajero;
import com.aerogestion.repository.UsuarioRepository;
import com.aerogestion.service.IAuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Pasajero registrar(RegistroForm form) {
        String email = form.getEmail().trim().toLowerCase();
        if (usuarioRepository.existsByEmail(email)) {
            throw new ReglaNegocioException("El email ya está en uso");
        }
        Pasajero pasajero = new Pasajero();
        pasajero.setNombre(form.getNombre().trim());
        pasajero.setApellido(form.getApellido().trim());
        pasajero.setEmail(email);
        pasajero.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        pasajero.setDocumentoIdentidad(form.getDocumentoIdentidad().trim());
        pasajero.setTelefono(form.getTelefono().trim());
        pasajero.setFechaNacimiento(form.getFechaNacimiento());
        pasajero.setNacionalidad(form.getNacionalidad().trim());
        return usuarioRepository.save(pasajero);
    }
}
