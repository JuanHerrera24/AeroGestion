package com.aerogestion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.aerogestion.model.EstadoReserva;
import com.aerogestion.model.Pasajero;
import com.aerogestion.model.Reserva;
import com.aerogestion.model.Vuelo;

public interface ReservaRepository extends MongoRepository<Reserva, String> {

    Optional<Reserva> findByCodigoReserva(String codigoReserva);

    List<Reserva> findByPasajeroOrderByFechaReservaDesc(Pasajero pasajero);

    List<Reserva> findByVuelo(Vuelo vuelo);

    boolean existsByCodigoReserva(String codigoReserva);

    long countByEstado(EstadoReserva estado);
}
