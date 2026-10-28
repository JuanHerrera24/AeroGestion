package com.aerogestion.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Muestra una página de error amigable para las excepciones de la aplicación. */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String noEncontrado(RecursoNoEncontradoException e, Model model) {
        model.addAttribute("titulo", "No encontrado");
        model.addAttribute("mensaje", e.getMessage());
        return "error";
    }

    @ExceptionHandler({ReglaNegocioException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String reglaNegocio(RuntimeException e, Model model) {
        model.addAttribute("titulo", "Operación no permitida");
        model.addAttribute("mensaje", e.getMessage());
        return "error";
    }
}
