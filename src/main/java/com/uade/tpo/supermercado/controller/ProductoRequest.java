package com.uade.tpo.supermercado.controller;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductoRequest {
    private String nombre;
    private BigDecimal precio;
    private String marca;
    private int categoria_id;
}
