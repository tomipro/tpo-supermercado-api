package com.uade.tpo.supermercado.controller;

import org.springframework.data.domain.Page;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.supermercado.entity.Categoria;
import com.uade.tpo.supermercado.entity.Producto;
import com.uade.tpo.supermercado.excepciones.*;
import com.uade.tpo.supermercado.service.ProductoService;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("Producto")
public class ProductoController {
    @Autowired
    private ProductoService productoService;

    @GetMapping
    public ResponseEntity<Page<Producto>> getProductos(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) throws ProductoNotFoundException {
        // Se puede obtener una lista paginada de productos
        // Si no se proporciona paginación, se devuelven todos los productos
        // Si no se encuentra ningún producto, se lanza una excepción
        if (page == null || size == null)
            return ResponseEntity.ok(productoService.getProductos(PageRequest.of(1, Integer.MAX_VALUE)));
        else if (productoService.getProductos(PageRequest.of(page, size)).isEmpty())
            throw new ProductoNotFoundException("No hay productos cargados en el sistema");
        else if (page < 1 || size < 1)
            throw new ParametroFueraDeRangoException("Los parámetros de paginación deben ser mayores a 0");
        else
            return ResponseEntity.ok(productoService.getProductos(PageRequest.of(page, size)));
    }

    @GetMapping("/{nombreProducto}")
    public ResponseEntity<Producto> getProductoByName(@RequestParam String nombreProducto)
            throws ProductoNotFoundException {
        // Se puede obtener el producto por nombre
        // Si el nombre es nulo o vacío, se lanza una excepción
        // Si no se encuentra el producto, se lanza una excepción
        if (nombreProducto == null || nombreProducto.isEmpty())
            throw new ParametroFueraDeRangoException("El nombre del producto no puede ser nulo o vacío");
        Optional<Producto> result = productoService.getProductoByName(nombreProducto);
        if (result.isPresent())
            return ResponseEntity.ok(result.get());
        throw new ProductoNotFoundException("No se encontró el producto con nombre: " + nombreProducto);
    }

    @GetMapping("/{categoria}")
    public ResponseEntity<Producto> getProductoByCategory(@RequestParam int categoria)
            throws ProductoNotFoundException {
        // Se puede obtener el producto por el id de la categoria
        // Si el id de la categoria es menor a 1, se lanza una excepción
        // Si no se encuentra el producto, se lanza una excepción
        if (categoria < 1)
            throw new ParametroFueraDeRangoException("El id de la categoría debe ser mayor a 0");
        Optional<Producto> result = productoService.getProductoByCategory(categoria);
        if (result.isPresent())
            return ResponseEntity.ok(result.get());

        throw new ProductoNotFoundException("No se encontró el producto con categoría: " + categoria);
    }

    @GetMapping("/{marca}")
    public ResponseEntity<Producto> getProductoByMarca(@RequestParam String marca) throws ProductoNotFoundException {
        // Se puede obtener el producto por marca
        // Si la marca es nula o vacía, se lanza una excepción
        // Si no se encuentra el producto, se lanza una excepción
        if (marca == null || marca.isEmpty())
            throw new ParametroFueraDeRangoException("La marca no puede ser nula o vacía");
        Optional<Producto> result = productoService.getProductoByMarca(marca);
        if (result.isPresent())
            return ResponseEntity.ok(result.get());

        throw new ProductoNotFoundException("No se encontró el producto con marca: " + marca);
    }

    @GetMapping("/{precio}")
    public ResponseEntity<Producto> getProductoByPrecioMaximo(@RequestParam BigDecimal precioMax,
            @RequestParam BigDecimal precioMin) throws ProductoNotFoundException {
        // Se puede obtener el producto por precio
        // Si ambos son nulos, se lanza una excepción
        // Si el precio máximo es menor al mínimo, se lanza una excepción
        // Si el precio máximo es nulo, se busca por precio mínimo
        // Si el precio mínimo es nulo, se busca por precio máximo
        // Si no se encuentra el producto, se lanza una excepción
        Optional<Producto> result = Optional.empty();
        if (precioMax == null && precioMin == null) {
            throw new ParametroFueraDeRangoException("Ambos precios no pueden ser nulos");
        } else if (precioMax != null && precioMin != null && precioMax.compareTo(precioMin) < 0) {
            throw new ParametroFueraDeRangoException("El precio máximo debe ser mayor al mínimo");
        } else if (precioMax == null) {
            result = productoService.getProductoByPrecioMinimo(precioMin);
        } else if (precioMin == null) {
            result = productoService.getProductoByPrecioMaximo(precioMax);
        } else {
            result = productoService.getProductoByPrecio(precioMax, precioMin);
        }
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        }

        throw new ProductoNotFoundException(
                "No se encontraron productos con precio máximo: " + precioMax + " y mínimo: " + precioMin);
    }

    @PostMapping
    public ResponseEntity<Object> createProducto(@RequestBody ProductoRequest producto)
            throws ProductoDuplicateException {
        // Se puede crear un producto
        if (producto.getNombre() == null || producto.getNombre().isEmpty()) {
            throw new ParametroFueraDeRangoException("El nombre del producto no puede ser nulo o vacío");
        }
        if (producto.getMarca() == null || producto.getMarca().isEmpty()) {
            throw new ParametroFueraDeRangoException("La marca no puede ser nula o vacía");
        }
        if (producto.getPrecio() == null || producto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ParametroFueraDeRangoException("El precio no puede ser nulo o menor a 0");
        }
        if (producto.getCategoria_id() < 1) {
            throw new ParametroFueraDeRangoException("El id de la categoría debe ser mayor a 0");
        }
        Producto result = productoService.createProducto(producto);
        return ResponseEntity.created(URI.create("/productos/" + result.getId())).body(result);
    }

    @PutMapping
    public ResponseEntity<Object> putProducto(@PathVariable int id, @RequestBody ProductoRequest productoRequest)
            throws ProductoNotFoundException {
        // Se puede actualizar un producto
        // Si el id es menor a 1, se lanza una excepción
        // Si el nombre es nulo o vacío, se lanza una excepción
        // Si la marca es nula o vacía, se lanza una excepción
        // Si el precio es nulo o menor a 0, se lanza una excepción
        // Si el id de la categoría es menor a 1, se lanza una excepción
        // Si no se encuentra el producto, se lanza una excepción

        if (id < 1) {
            throw new ParametroFueraDeRangoException("El id del producto debe ser mayor a 0");
        }
        if (productoRequest.getNombre() == null || productoRequest.getNombre().isEmpty()) {
            throw new ParametroFueraDeRangoException("El nombre del producto no puede ser nulo o vacío");
        }
        if (productoRequest.getMarca() == null || productoRequest.getMarca().isEmpty()) {
            throw new ParametroFueraDeRangoException("La marca no puede ser nula o vacía");
        }
        if (productoRequest.getPrecio() == null || productoRequest.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ParametroFueraDeRangoException("El precio no puede ser nulo o menor a 0");
        }
        if (productoRequest.getCategoria_id() < 1) {
            throw new ParametroFueraDeRangoException("El id de la categoría debe ser mayor a 0");
        }
        Optional<Producto> result = productoService.getProductoById(id);
        if (result.isPresent()) {
            productoService.updateProducto(id, productoRequest);
            return ResponseEntity.ok(result.get());
        }
        throw new ProductoNotFoundException("No se encontró el producto con id: " + id);
    }

    @DeleteMapping
    public ResponseEntity<Object> deleteProducto(@PathVariable int id) throws ProductoNotFoundException {
        productoService.deleteProducto(id);
        return ResponseEntity.noContent().build();
    }

}
