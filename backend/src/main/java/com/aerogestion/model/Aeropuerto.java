package com.aerogestion.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "aeropuertos")
public class Aeropuerto extends EntidadBase {

    /** Código IATA de 3 letras, ej. LIM, CUZ, BOG. */
    @Indexed(unique = true)
    private String codigoIata;

    private String nombre;
    private String ciudad;
    private String pais;
}
