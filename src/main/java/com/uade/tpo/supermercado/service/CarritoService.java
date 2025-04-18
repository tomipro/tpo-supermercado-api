package com.uade.tpo.supermercado.service;

import com.uade.tpo.supermercado.entity.Carrito;
import com.uade.tpo.supermercado.entity.dto.CarritoResponse;

public interface CarritoService {
    Carrito crearCarrito(int usuarioId);

    Carrito obtenerCarrito(int usuarioId);

    Carrito agregarProducto(int usuarioId, int productoId, int cantidad);

    Carrito eliminarProducto(int usuarioId, int productoId);

    Carrito vaciarCarrito(int usuarioId);

    CarritoResponse convertirACarritoResponse(Carrito carrito);

}
