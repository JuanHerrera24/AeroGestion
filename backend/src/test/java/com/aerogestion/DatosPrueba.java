package com.aerogestion;

import java.time.LocalDateTime;

import com.aerogestion.model.Aeropuerto;
import com.aerogestion.model.AsientoEconomico;
import com.aerogestion.model.AsientoEjecutivo;
import com.aerogestion.model.Avion;
import com.aerogestion.model.Pasajero;
import com.aerogestion.model.Reserva;
import com.aerogestion.model.Vuelo;

/** Objetos del dominio listos para usar en las pruebas (sin base de datos). */
public final class DatosPrueba {

    public static final String EMAIL_PASAJERO = "pasajero@aerogestion.com";

    private DatosPrueba() {
    }

    public static Aeropuerto aeropuerto(String iata, String ciudad) {
        Aeropuerto a = new Aeropuerto();
        a.setId("ap-" + iata);
        a.setCodigoIata(iata);
        a.setNombre("Aeropuerto de " + ciudad);
        a.setCiudad(ciudad);
        a.setPais("Perú");
        return a;
    }

    /** Avión con 2 asientos ejecutivos (1A, 1B) y 2 económicos (2A, 2B). */
    public static Avion avion() {
        Avion avion = new Avion();
        avion.setId("av-1");
        avion.setMatricula("OB-1234");
        avion.setModelo("A320");
        avion.setFabricante("Airbus");
        avion.setAnioFabricacion(2020);
        avion.getAsientos().add(new AsientoEjecutivo(1, "A"));
        avion.getAsientos().add(new AsientoEjecutivo(1, "B"));
        avion.getAsientos().add(new AsientoEconomico(2, "A"));
        avion.getAsientos().add(new AsientoEconomico(2, "B"));
        return avion;
    }

    /** Vuelo LIM → CUZ dentro de 10 días, abierto para reservas. */
    public static Vuelo vuelo() {
        Vuelo vuelo = new Vuelo();
        vuelo.setId("vu-1");
        vuelo.setCodigoVuelo("AG100");
        vuelo.setOrigen(aeropuerto("LIM", "Lima"));
        vuelo.setDestino(aeropuerto("CUZ", "Cusco"));
        vuelo.setFechaHoraSalida(LocalDateTime.now().plusDays(10).withNano(0));
        vuelo.setFechaHoraLlegada(LocalDateTime.now().plusDays(10).plusMinutes(80).withNano(0));
        vuelo.setTarifaBase(200);
        vuelo.setAvion(avion());
        return vuelo;
    }

    public static Pasajero pasajero() {
        Pasajero p = new Pasajero();
        p.setId("us-1");
        p.setNombre("Ana");
        p.setApellido("Gómez");
        p.setEmail(EMAIL_PASAJERO);
        return p;
    }

    /** Reserva confirmada del asiento 2A en el vuelo AG100. */
    public static Reserva reserva() {
        Vuelo vuelo = vuelo();
        Reserva r = new Reserva();
        r.setId("re-1");
        r.setCodigoReserva("AG-ABC234");
        r.setPasajero(pasajero());
        r.setVuelo(vuelo);
        r.getAsientos().add(vuelo.getAvion().buscarAsiento("2A"));
        r.confirmar();
        return r;
    }
}
