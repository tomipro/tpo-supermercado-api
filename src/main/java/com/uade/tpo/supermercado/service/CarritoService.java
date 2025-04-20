package com.uade.tpo.supermercado.service;

import com.uade.tpo.supermercado.entity.Carrito;
import com.uade.tpo.supermercado.entity.Usuario;
import com.uade.tpo.supermercado.entity.dto.CarritoResponse;

public interface CarritoService {
    Carrito crearCarrito(Usuario usuario);

    Carrito obtenerCarrito(Usuario usuario);

    Carrito agregarProducto(Usuario usuario, int productoId, int cantidad);

    Carrito eliminarProducto(Usuario usuario, int productoId, int cantidad);

    Carrito vaciarCarrito(Usuario usuario);

    CarritoResponse convertirACarritoResponse(Carrito carrito);

    public void vaciarCarritosAntiguos();

}
