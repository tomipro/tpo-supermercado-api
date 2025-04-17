package com.uade.tpo.supermercado.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class ProductoRequest {

    private String nombre;
    private String descripcion;
    private List<String> imagenes;//puede ser null
    private BigDecimal precio;
    private String marca;//puede ser null
    private int categoria_id;
    private LocalDate fechaVencimiento;
    private int stock=0;
    private int stockMinimo=0;
    private String unidadMedida;//puede ser null
    private String estado = "activo";
    private int ventasTotales = 0;

}
