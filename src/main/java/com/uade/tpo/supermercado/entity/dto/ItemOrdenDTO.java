package com.uade.tpo.supermercado.entity.dto;

import lombok.Data;

@Data
public class ItemOrdenDTO {
    private int productoId;
    private String nombreProducto;
    private int cantidad;
    private double precioUnitario;

    public ItemOrdenDTO(int productoId, String nombreProducto, int cantidad, double precioUnitario) {
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

}
