package com.aerogestion.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.aerogestion.dto.ErrorDTO;

/** Convierte las excepciones en respuestas JSON con el código HTTP adecuado. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO noEncontrado(RecursoNoEncontradoException e) {
        return new ErrorDTO(404, "No encontrado", e.getMessage());
    }

    @ExceptionHandler({ReglaNegocioException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO reglaNegocio(RuntimeException e) {
        return new ErrorDTO(400, "Operación no permitida", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO validacion(MethodArgumentNotValidException e) {
        Map<String, String> campos = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(f -> campos.putIfAbsent(f.getField(), f.getDefaultMessage()));
        return new ErrorDTO(400, "Datos inválidos", "Revisa los campos indicados", campos, LocalDateTime.now());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO peticionMalFormada(Exception e) {
        return new ErrorDTO(400, "Petición inválida",
                "Revisa el formato del JSON o de los parámetros (fechas en formato 2026-11-01, estados en MAYÚSCULAS)");
    }
}
