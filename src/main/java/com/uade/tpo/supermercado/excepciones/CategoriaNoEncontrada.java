package com.uade.tpo.supermercado.excepciones;

public class CategoriaNoEncontrada extends RuntimeException {
    public CategoriaNoEncontrada(String message) {
        super(message);
    }
}