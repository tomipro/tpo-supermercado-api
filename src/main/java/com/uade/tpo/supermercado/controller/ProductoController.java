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
import com.uade.tpo.supermercado.entity.Producto;
import com.uade.tpo.supermercado.excepciones.*;
import com.uade.tpo.supermercado.service.ProductoService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;




@RestController
@RequestMapping ("Producto")
public class ProductoController {
    @Autowired
    private ProductoService productoService;
    
    @GetMapping
    public ResponseEntity<Page<Producto>> getProductos(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page == null || size == null)
            return ResponseEntity.ok(productoService.getProductos(PageRequest.of(0, Integer.MAX_VALUE)));
        return ResponseEntity.ok(productoService.getProductos(PageRequest.of(page, size)));
    }

    @GetMapping("/{nombreProducto}")
    public ResponseEntity<Producto> getProductoByName(@RequestParam String nombreProducto) {
        Optional<Producto> result = productoService.getProductoByName(nombreProducto);
        if (result.isPresent())
            return ResponseEntity.ok(result.get());

        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{categoria}")
    public ResponseEntity<Producto> getProductoByCategory(@RequestParam String categoria) {
        Optional<Producto> result = productoService.getProductoByCategory(categoria);
        if (result.isPresent())
            return ResponseEntity.ok(result.get());

        return ResponseEntity.noContent().build();
    }  

    @GetMapping("/{marca}")
    public ResponseEntity<Producto> getProductoByMarca(@RequestParam String marca) {
        Optional<Producto> result = productoService.getProductoByMarca(marca);
        if (result.isPresent())
            return ResponseEntity.ok(result.get());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{precio}")
    public ResponseEntity<Producto> getProductoByPrecio(@RequestParam BigDecimal precio) {
        Optional<Producto> result = productoService.getProductoByPrecio(precio);
        if (result.isPresent())
            return ResponseEntity.ok(result.get());

        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Object> createProducto(@RequestBody ProductoRequest producto) throws ProductoDuplicateException {
        Producto result = productoService.createProducto(producto.getNombre(), producto.getMarca(), producto.getPrecio(), producto.getCategoria_id());
        return ResponseEntity.created(URI.create("/productos/" + result.getId())).body(result);
    }

    @PutMapping
    public ResponseEntity<Object> putProducto(@PathVariable int id, @RequestBody ProductoRequest productoRequest) throws ProductoNotFoundException {
        Producto entity = productoService.updateProducto(id, productoRequest.getNombre(), productoRequest.getMarca(), productoRequest.getPrecio(), productoRequest.getCategoria_id());
        if (entity == null) {
            
        }
        return ResponseEntity.ok(entity);
    }

    @DeleteMapping
    public ResponseEntity<Object> deleteProducto(@PathVariable int id) throws ProductoNotFoundException {
        productoService.deleteProducto(id);
        return ResponseEntity.noContent().build();

}
