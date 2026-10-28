package com.aerogestion.dto;

import com.aerogestion.model.Rol;
import com.aerogestion.model.Usuario;

/** Datos públicos de un usuario (nunca incluye la contraseña). */
public record UsuarioDTO(String nombre, String apellido, String email, Rol rol) {

    public static UsuarioDTO de(Usuario u) {
        return new UsuarioDTO(u.getNombre(), u.getApellido(), u.getEmail(), u.getRol());
    }
}
