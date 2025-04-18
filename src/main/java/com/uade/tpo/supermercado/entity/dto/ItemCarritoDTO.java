package com.uade.tpo.supermercado.entity.dto;

import lombok.Data;

@Data
public class ItemCarritoDTO {
    private int productoId;
    private String nombreProducto;
    private int cantidad;
    private double precioUnitario;

    public ItemCarritoDTO(int productoId, String nombreProducto, int cantidad, double precioUnitario) {
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

}
