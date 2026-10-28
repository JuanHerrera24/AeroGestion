package com.aerogestion.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.aerogestion.model.Aeropuerto;
import com.aerogestion.model.Avion;
import com.aerogestion.model.EstadoVuelo;
import com.aerogestion.model.Vuelo;

public interface VueloRepository extends MongoRepository<Vuelo, String> {

    Optional<Vuelo> findByCodigoVuelo(String codigoVuelo);

    List<Vuelo> findByOrigenAndDestinoAndFechaHoraSalidaBetween(
            Aeropuerto origen, Aeropuerto destino, LocalDateTime desde, LocalDateTime hasta);

    List<Vuelo> findByAvion(Avion avion);

    List<Vuelo> findByFechaHoraSalidaAfterAndEstadoInOrderByFechaHoraSalida(
            LocalDateTime desde, List<EstadoVuelo> estados);

    List<Vuelo> findAllByOrderByFechaHoraSalidaAsc();

    boolean existsByCodigoVuelo(String codigoVuelo);
}
