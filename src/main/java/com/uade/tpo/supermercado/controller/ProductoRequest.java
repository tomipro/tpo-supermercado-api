package com.uade.tpo.supermercado.controller;

import java.math.BigDecimal;
import java.util.List;
import com.uade.tpo.supermercado.entity.Imagen;

import lombok.Data;

@Data
public class ProductoRequest {
    private String nombre;
    private String descripcion;
    private List<String> imagenes;
    private BigDecimal precio;
    private String marca;
    private int categoria_id;
}
