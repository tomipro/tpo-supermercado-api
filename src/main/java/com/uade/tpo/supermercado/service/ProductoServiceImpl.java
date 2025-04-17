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

    @Autowired
    private CategoriaService categorias;

    @Override
    public Page<Producto> getProductos(PageRequest pageRequest) {
        return productoRepository.findAll(pageRequest);
    }

    @Override
    public Optional<Producto> getProductoByName(String nombre) {
        return productoRepository.findByNombre(nombre);
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
        
        // Crea un nuevo producto y lo guarda en la base de datos
        Producto nuevoProducto = new Producto();
        nuevoProducto.setNombre(productoRequest.getNombre());
        nuevoProducto.setDescripcion(productoRequest.getDescripcion());
        nuevoProducto.setMarca(productoRequest.getMarca());
        nuevoProducto.setPrecio(productoRequest.getPrecio());
        categorias.getCategoriaById(productoRequest.getCategoria_id())
            .ifPresent(nuevoProducto::setCategoria);
        nuevoProducto.setStock(productoRequest.getStock());
        nuevoProducto.setStock_minimo(productoRequest.getStockMinimo());
        nuevoProducto.setUnidad_medida(productoRequest.getUnidadMedida());
        nuevoProducto.setEstado(productoRequest.getEstado());
        nuevoProducto.setVentas_totales(productoRequest.getVentasTotales());
        nuevoProducto.setDate(productoRequest.getFechaVencimiento());
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
        Producto producto= productoRepository.findProductoById(id);
        producto.setNombre(productoRequest.getNombre());
        producto.setDescripcion(productoRequest.getDescripcion());
        producto.setMarca(productoRequest.getMarca());
        producto.setPrecio(productoRequest.getPrecio());
        categorias.getCategoriaById(productoRequest.getCategoria_id())
                .ifPresent(producto::setCategoria);
        producto.setStock(productoRequest.getStock());
        producto.setStock_minimo(productoRequest.getStockMinimo());
        producto.setUnidad_medida(productoRequest.getUnidadMedida());
        producto.setEstado(productoRequest.getEstado());
        producto.setVentas_totales(productoRequest.getVentasTotales());
        producto.setDate(productoRequest.getFechaVencimiento());

        //eliminar las imagenes viejas
        List<Imagen> imagenesViejas = producto.getImagenes();
        for (Imagen imagen : imagenesViejas) {
            imagen.setProducto(null); // Desvincula la imagen del producto
        }
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
        }

    @Override
    public void deleteProducto(int id) throws ProductoNotFoundException {
        productoRepository.deleteProducto(id);
    }

    @Override
    public Optional<Producto> getProductoById(int id) {
        return productoRepository.findById(id);
    }

}
