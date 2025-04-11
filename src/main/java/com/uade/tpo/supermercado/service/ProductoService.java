package com.uade.tpo.supermercado.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.uade.tpo.supermercado.entity.*;
import com.uade.tpo.supermercado.excepciones.ProductoDuplicateException;
import com.uade.tpo.supermercado.excepciones.ProductoNotFoundException;

public interface ProductoService {
    public Page<Producto> getProductos(PageRequest pageRequest);
    public Optional<Producto> getProductoByName(String nombreProducto);
    public Optional<Producto> getProductoByCategory(int categoria_id);
    public Optional<Producto> getProductoByMarca(String marca);
    public Optional<Producto> getProductoByPrecio(BigDecimal precio);
    public Producto createProducto(String nombreProducto, String descripcion, String marca, BigDecimal precio, String categoria) throws ProductoDuplicateException;
    public Producto updateProducto(int id, String nombreProducto, String descripcion, String marca, BigDecimal precio, Categoria categoria) throws ProductoNotFoundException;
    public void deleteProducto(int id) throws ProductoNotFoundException;
}
