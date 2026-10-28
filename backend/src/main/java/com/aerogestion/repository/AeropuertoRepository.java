package com.aerogestion.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.aerogestion.model.Aeropuerto;

public interface AeropuertoRepository extends MongoRepository<Aeropuerto, String> {

    Optional<Aeropuerto> findByCodigoIata(String codigoIata);
}
