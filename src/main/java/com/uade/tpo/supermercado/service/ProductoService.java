package com.uade.tpo.supermercado.service;

import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.uade.tpo.supermercado.controller.ProductoRequest;
import com.uade.tpo.supermercado.entity.*;
import com.uade.tpo.supermercado.excepciones.ProductoDuplicateException;
import com.uade.tpo.supermercado.excepciones.ProductoNotFoundException;

public interface ProductoService {
    public Page<Producto> getProductos(Pageable pageable);
    public Optional<Producto> getProductoByName(String nombreProducto);
    public Optional<Producto> getProductoByCategory(Categoria categoria);
    public Optional<Producto> getProductoByMarca(String marca);
    public Optional<Producto> getProductoByPrecio(BigDecimal precioMax, BigDecimal precioMin);
    public Optional<Producto> getProductoByPrecioMaximo(BigDecimal precio);
    public Optional<Producto> getProductoByPrecioMinimo(BigDecimal precio);
    public Producto createProducto(ProductoRequest productoRequest) throws ProductoDuplicateException;
    public Producto updateProducto(int id, ProductoRequest productoRequest) throws ProductoNotFoundException;
    public void deleteProducto(int id) throws ProductoNotFoundException;
    public Optional<Producto> getProductoById(int id);
}
