package com.uade.tpo.supermercado.controller.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.uade.tpo.supermercado.entity.Imagen;
import com.uade.tpo.supermercado.entity.Producto;

import lombok.Data;

@Data
public class CatalogoResponse {
    private BigDecimal precio;
    private List<String> imagenes;

    public CatalogoResponse(Producto producto) {
        this.precio = producto.getPrecio();
        this.imagenes = new ArrayList<>();
        cargarImagenes(producto);
    }

    private void cargarImagenes(Producto producto) {
        List<Imagen> imagenes = producto.getImagenes();
        for (Imagen imagen : imagenes) {
            this.imagenes.add(imagen.getImagen());
        }
    }
}
