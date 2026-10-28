package com.aerogestion.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.aerogestion.model.Avion;
import com.aerogestion.model.EstadoAvion;

public interface AvionRepository extends MongoRepository<Avion, String> {

    List<Avion> findByEstado(EstadoAvion estado);

    boolean existsByMatricula(String matricula);
}
