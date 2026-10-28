package com.aerogestion.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

/** Usuario del sistema. Cada rol concreto define sus permisos. */
@Getter
@Setter
@Document(collection = "usuarios")
public abstract class Usuario extends EntidadBase {

    private String nombre;
    private String apellido;

    @Indexed(unique = true)
    private String email;

    private String passwordHash;
    private Rol rol;

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public abstract boolean puedeGestionarVuelos();
}
