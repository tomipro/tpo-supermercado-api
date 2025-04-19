package com.uade.tpo.supermercado.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.uade.tpo.supermercado.controller.ProductoRequest;
import com.uade.tpo.supermercado.entity.Producto;
import com.uade.tpo.supermercado.service.CategoriaServiceImpl;
import lombok.Data;

@Data
public class ProductoDTO {
    private String nombre;
    private String descripcion;
    private List<String> imagenes;//puede ser null
    private BigDecimal precio;
    private String marca;//puede ser null
    private String categoria;
    private LocalDate fechaVencimiento;
    private int stock;
    private String unidadMedida;//puede ser null

    // Constructors
    public ProductoDTO(Producto producto) {
        this.nombre = producto.getNombre();
        this.descripcion = producto.getDescripcion();
        this.imagenes = producto.getImagenes().stream()
                                 .map(imagen -> imagen.getImagen())
                                 .toList();
        this.precio = producto.getPrecio();
        this.marca = producto.getMarca();
        this.categoria = producto.getCategoria().getNombre();
        this.fechaVencimiento = producto.getDate();
        this.stock = producto.getStock();
        this.unidadMedida = producto.getUnidad_medida();
    }
}