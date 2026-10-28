package com.aerogestion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.aerogestion.model.Avion;
import com.aerogestion.model.EstadoAvion;

public interface AvionRepository extends MongoRepository<Avion, String> {

    Optional<Avion> findByMatricula(String matricula);

    List<Avion> findByEstado(EstadoAvion estado);

    boolean existsByMatricula(String matricula);
}
