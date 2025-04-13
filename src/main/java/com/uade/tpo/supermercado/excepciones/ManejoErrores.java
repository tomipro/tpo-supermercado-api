package com.uade.tpo.supermercado.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ManejoErrores {
    @ExceptionHandler(ParametroFueraDeRangoException.class)
    public ResponseEntity<String> manejarParametroFueraDeRango(ParametroFueraDeRangoException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(NoEncontradoException.class)
    public ResponseEntity<String> manejarNoEncontrado(NoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(DatoDuplicadoException.class)
    public ResponseEntity<String> manejarDatoDuplicado(DatoDuplicadoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
    
}
