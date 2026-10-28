package com.aerogestion.model;

import java.time.LocalDate;
import java.time.Period;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pasajero extends Usuario {

    private String documentoIdentidad;
    private String telefono;
    private LocalDate fechaNacimiento;
    private String nacionalidad;

    public Pasajero() {
        setRol(Rol.PASAJERO);
    }

    @Override
    public boolean puedeGestionarVuelos() {
        return false;
    }

    public boolean esMayorDeEdad() {
        return fechaNacimiento != null
                && Period.between(fechaNacimiento, LocalDate.now()).getYears() >= 18;
    }
}
