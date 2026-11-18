package com.aerogestion.model;

import org.springframework.data.annotation.TypeAlias;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TypeAlias("ADMINISTRADOR")
public class Administrador extends Usuario {

    private String codigoEmpleado;
    private String cargo;

    public Administrador() {
        setRol(Rol.ADMIN);
    }

    @Override
    public boolean puedeGestionarVuelos() {
        return true;
    }
}
