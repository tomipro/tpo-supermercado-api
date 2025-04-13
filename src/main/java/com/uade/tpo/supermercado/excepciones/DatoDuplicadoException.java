package com.uade.tpo.supermercado.excepciones;

public class DatoDuplicadoException extends RuntimeException {
    public DatoDuplicadoException(String message) {
        super(message);
    }
}