package com.aerogestion.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "aviones")
public class Avion extends EntidadBase {

    @Indexed(unique = true)
    private String matricula;

    private String modelo;
    private String fabricante;
    private int anioFabricacion;
    private EstadoAvion estado = EstadoAvion.ACTIVO;

    /** Composición: los asientos se guardan embebidos en el documento del avión. */
    private List<Asiento> asientos = new ArrayList<>();

    public int getCapacidadTotal() {
        return asientos.size();
    }

    public boolean estaDisponible() {
        return estado == EstadoAvion.ACTIVO;
    }

    public void cambiarEstado(EstadoAvion nuevo) {
        this.estado = nuevo;
    }

    public Asiento buscarAsiento(String numero) {
        return asientos.stream()
                .filter(a -> a.getNumero().equalsIgnoreCase(numero))
                .findFirst()
                .orElse(null);
    }
}
