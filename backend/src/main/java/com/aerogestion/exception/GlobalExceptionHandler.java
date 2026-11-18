package com.aerogestion.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.aerogestion.dto.ErrorDTO;

/**
 * Convierte las excepciones en respuestas JSON con el código HTTP adecuado.
 *
 * <ul>
 *   <li>400 Bad Request: los datos enviados no son válidos.</li>
 *   <li>404 Not Found: el recurso (o la ruta) no existe.</li>
 *   <li>405 Method Not Allowed: la ruta existe, pero no con ese método HTTP.</li>
 *   <li>409 Conflict: la petición es válida, pero choca con el estado actual de los datos.</li>
 *   <li>500 Internal Server Error: error inesperado; nunca se expone el detalle técnico al cliente.</li>
 * </ul>
 * Los 401 y 403 los responde Spring Security (ver {@code SecurityConfig}).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO noEncontrado(RecursoNoEncontradoException e) {
        return new ErrorDTO(404, "No encontrado", e.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO rutaInexistente(NoResourceFoundException e) {
        return new ErrorDTO(404, "No encontrado", "La ruta /" + e.getResourcePath() + " no existe en la API");
    }

    @ExceptionHandler(ReglaNegocioException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO reglaNegocio(ReglaNegocioException e) {
        return new ErrorDTO(400, "Datos inválidos", e.getMessage());
    }

    @ExceptionHandler({ConflictoException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDTO conflicto(RuntimeException e) {
        return new ErrorDTO(409, "Conflicto", e.getMessage());
    }

    /** Respaldo de los índices únicos de MongoDB (email, matrícula, código de vuelo o de reserva). */
    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDTO duplicado(DuplicateKeyException e) {
        return new ErrorDTO(409, "Conflicto", "Ya existe un registro con ese identificador");
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

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorDTO metodoNoPermitido(HttpRequestMethodNotSupportedException e) {
        return new ErrorDTO(405, "Método no permitido",
                "La ruta no admite " + e.getMethod() + "; métodos válidos: " + e.getSupportedHttpMethods());
    }

    /** Deja que Spring Security responda 401/403 con su propio formato. */
    @ExceptionHandler({AccessDeniedException.class, AuthenticationException.class})
    public void seguridad(RuntimeException e) {
        throw e;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDTO errorInesperado(Exception e) {
        log.error("Error inesperado", e);
        return new ErrorDTO(500, "Error interno", "Ocurrió un error inesperado. Intenta de nuevo más tarde.");
    }
}
