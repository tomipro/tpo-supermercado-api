package com.uade.tpo.supermercado.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.uade.tpo.supermercado.entity.Categoria;
import com.uade.tpo.supermercado.entity.Producto;
import com.uade.tpo.supermercado.excepciones.ProductoDuplicateException;
import com.uade.tpo.supermercado.excepciones.ProductoNotFoundException;
import com.uade.tpo.supermercado.repository.ProductoRepository;

public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public Page<Producto> getProductos(PageRequest pageRequest) {
        return productoRepository.findAll(pageRequest);
    }

    @Override
    public Optional<Producto> getProductoByName(String nombreProducto) {
        return productoRepository.findByNombreProducto(nombreProducto);
    }

    @Override
    public Optional<Producto> getProductoByCategory(Categoria categoria) {
        return productoRepository.findByCategoria(categoria);
    }

    @Override
    public Optional<Producto> getProductoByMarca(String marca) {
        return productoRepository.findByMarca(marca);
    }

    @Override
    public Optional<Producto> getProductoByPrecio(BigDecimal precio) {
        return productoRepository.findByPrecio(precio);
    }

    @Override
    public Producto createProducto(String nombreProducto, String marca, BigDecimal precio, int categoria)
            throws ProductoDuplicateException {
        // Check if the product already exists
        Optional<Producto> existingProduct = productoRepository.findAllProductos()
                .filter(producto -> producto.getNombre().equals(nombreProducto) && producto.getMarca().equals(marca));
        if (existingProduct.isPresent()) {
            throw new ProductoDuplicateException();
        }
        return productoRepository.createProducto(nombreProducto, marca, precio, categoria);
    }

    @Override
    public Producto updateProducto(int id, String nombreProducto, String marca, BigDecimal precio, Categoria categoria)
            throws ProductoNotFoundException {
        // Check if the product exists
        Optional<Producto> existingProduct = productoRepository.findAllProductos()
                .filter(producto -> producto.getId() == id);

        if (existingProduct.isPresent()) {
            Producto producto = existingProduct.get();
            producto.setNombre(nombreProducto);
            producto.setMarca(marca);
            producto.setPrecio(precio);
            producto.setCategoria_id(categoria.getId());
            return productoRepository.save(producto);
        } else {
            throw new ProductoNotFoundException();
        }
    }

    @Override
    public void deleteProducto(int id) throws ProductoNotFoundException {
        // Check if the product exists
        Optional<Producto> existingProduct = productoRepository.findAllProductos()
                .filter(producto -> producto.getId() == id);

        if (existingProduct.isPresent()) {
            productoRepository.deleteProducto(id);
        } else {
            throw new ProductoNotFoundException();
        }
    }        

    
}
