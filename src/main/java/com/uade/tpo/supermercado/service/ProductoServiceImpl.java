package com.uade.tpo.supermercado.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.uade.tpo.supermercado.controller.ProductoRequest;
import com.uade.tpo.supermercado.entity.Categoria;
import com.uade.tpo.supermercado.entity.Imagen;
import com.uade.tpo.supermercado.entity.Producto;
import com.uade.tpo.supermercado.excepciones.ProductoDuplicateException;
import com.uade.tpo.supermercado.excepciones.ProductoNotFoundException;
import com.uade.tpo.supermercado.repository.ProductoRepository;

@Service
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
    public Optional<Producto> getProductoByCategory(int categoria_id) {
        return productoRepository.findByCategoria(categoria_id);
    }

    @Override
    public Optional<Producto> getProductoByMarca(String marca) {
        return productoRepository.findByMarca(marca);
    }

    @Override
    public Optional<Producto> getProductoByPrecio(BigDecimal precioMax, BigDecimal precioMin) {
        return productoRepository.findByPrecio(precioMax, precioMin);
    }

    @Override
    public Optional<Producto> getProductoByPrecioMaximo(BigDecimal precio) {
        return productoRepository.findByPrecioMaximo(precio);
    }

    @Override
    public Optional<Producto> getProductoByPrecioMinimo(BigDecimal precio) {
        return productoRepository.findByPrecioMinimo(precio);
    }

    @Override
    public Producto createProducto(ProductoRequest productoRequest)
            throws ProductoDuplicateException {
        // Verifica si el producto ya existe
        Optional<Producto> existingProduct = productoRepository.findAllProductos()
                .filter(producto -> producto.getNombre().equals(productoRequest.getNombre())
                        && producto.getMarca().equals(productoRequest.getMarca()));
        if (existingProduct.isPresent()) {
            throw new ProductoDuplicateException();
        }
        // Crea un nuevo producto y lo guarda en la base de datos
        Producto nuevoProducto = new Producto();
        nuevoProducto.setNombre(productoRequest.getNombre());
        nuevoProducto.setDescripcion(productoRequest.getDescripcion());
        nuevoProducto.setMarca(productoRequest.getMarca());
        nuevoProducto.setPrecio(productoRequest.getPrecio());
        nuevoProducto.setCategoria_id(productoRequest.getCategoria_id());
        // agregamos las imagenes
        List<Imagen> imagenes = new ArrayList<>();
        for (String imagenUrl : productoRequest.getImagenes()) {
            Imagen imagen = new Imagen();
            imagen.setImagen(imagenUrl);
            imagen.setProducto(nuevoProducto);
            imagenes.add(imagen);
        }
        return productoRepository.save(nuevoProducto);
    }

    @Override
    public Producto updateProducto(int id, ProductoRequest productoRequest)
            throws ProductoNotFoundException {
        Optional<Producto> existingProduct = productoRepository.findAllProductos()
                .filter(producto -> producto.getId() == id);
        // Verifica si el producto existe
        // Si existe, actualiza los campos del producto
        if (existingProduct.isPresent()) {
            Producto producto = existingProduct.get();
            producto.setNombre(productoRequest.getNombre());
            producto.setDescripcion(productoRequest.getDescripcion());
            producto.setMarca(productoRequest.getMarca());
            producto.setPrecio(productoRequest.getPrecio());
            producto.setCategoria_id(productoRequest.getCategoria_id());
            // Actualiza las imágenes
            List<Imagen> imagenes = new ArrayList<>();
            for (String imagenUrl : productoRequest.getImagenes()) {
                Imagen imagen = new Imagen();
                imagen.setImagen(imagenUrl);
                imagen.setProducto(producto);
                imagenes.add(imagen);
            }
            // Guarda el producto actualizado en la base de datos
            return productoRepository.save(producto);
        } else {
            throw new ProductoNotFoundException("Producto no encontrado con ID: " + id);
        }
    }

    @Override
    public void deleteProducto(int id) throws ProductoNotFoundException {
        Optional<Producto> existingProduct = productoRepository.findAllProductos()
                .filter(producto -> producto.getId() == id);

        if (existingProduct.isPresent()) {
            productoRepository.deleteProducto(id);
        } else {
            throw new ProductoNotFoundException("Producto no encontrado con ID: " + id);
        }
    }

    @Override
    public Optional<Producto> getProductoById(int id) {
        return productoRepository.findById(id);
    }

}
