package com.aerogestion.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.Getter;
import lombok.Setter;

/** Clase base de todos los documentos: identificador y fechas de auditoría. */
@Getter
@Setter
public abstract class EntidadBase {

    @Id
    protected String id;

    @CreatedDate
    protected LocalDateTime fechaCreacion;

    @LastModifiedDate
    protected LocalDateTime fechaActualizacion;
}
