package com.uade.tpo.supermercado.excepciones;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class CategoriaNoEncontrada extends RuntimeException {
    public CategoriaNoEncontrada(String message) {
        super(message);
    }
}