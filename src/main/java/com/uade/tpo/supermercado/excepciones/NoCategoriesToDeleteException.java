package com.uade.tpo.supermercado.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "No hay categorías para eliminar")
public class NoCategoriesToDeleteException extends RuntimeException {
    public NoCategoriesToDeleteException(String message) {
        super(message);
        }}