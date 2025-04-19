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

import com.uade.tpo.supermercado.controller.dto.ProductoDTO;
import com.uade.tpo.supermercado.entity.Categoria;
import com.uade.tpo.supermercado.entity.Producto;
import com.uade.tpo.supermercado.excepciones.*;
import com.uade.tpo.supermercado.service.ProductoService;
import com.uade.tpo.supermercado.service.CategoriaService;
import com.uade.tpo.supermercado.service.CategoriaServiceImpl;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.time.LocalDate;

@RestController
@RequestMapping("producto")
public class ProductoController {
    
    @Autowired
    private ProductoService productoService;
    @Autowired
    private CategoriaService categorias;

    @GetMapping
    public ResponseEntity<Page<Producto>> getProductos(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) throws ProductoNotFoundException {
        // Se puede obtener una lista paginada de productos
        if (page == null || size == null)
            // Si no se proporciona paginación, se devuelven todos los productos
            return ResponseEntity.ok(productoService.getProductos(PageRequest.of(1, Integer.MAX_VALUE)));
        else if (productoService.getProductos(PageRequest.of(page, size)).isEmpty())
            // Si no se encuentra ningún producto, se lanza una excepción
            throw new ProductoNotFoundException("No hay productos cargados en el sistema");
        else if (page < 1 || size < 1)
            // Si la página o el tamaño son menores a 1, se lanza una excepción
            throw new ParametroFueraDeRangoException("Los parámetros de paginación deben ser mayores a 0");
        else
            return ResponseEntity.ok(productoService.getProductos(PageRequest.of(page, size)));
    }

    @GetMapping("/{nombreProducto}")
    public ResponseEntity<Producto> getProductoByName(@RequestParam String nombreProducto)
            throws ProductoNotFoundException {
        // Se puede obtener el producto por nombre
        if (nombreProducto == null || nombreProducto.isEmpty())
            // Si el nombre es nulo o vacío, se lanza una excepción
            throw new ParametroFueraDeRangoException("El nombre del producto no puede ser nulo o vacío");
        Optional<Producto> result = productoService.getProductoByName(nombreProducto);
        if (result.isPresent())
            return ResponseEntity.ok(result.get());
        // Si no se encuentra el producto, se lanza una excepción
        throw new ProductoNotFoundException("No se encontró el producto con nombre: " + nombreProducto);
    }

    @GetMapping("/{categoria}")
    public ResponseEntity<Producto> getProductoByCategory(@RequestParam int categoria_id)
            throws ProductoNotFoundException {
        // Se puede obtener el producto por el id de la categoria
        if (categoria_id < 1)
            // Si el id de la categoria es menor a 1, se lanza una excepción
            throw new ParametroFueraDeRangoException("El id de la categoría debe ser mayor a 0");
        Optional<Categoria> categoriaOptional = categorias.getCategoriaById(categoria_id);
        if (categoriaOptional.isPresent()) {
            Categoria categoria = categoriaOptional.get();
            Optional<Producto> producto = productoService.getProductoByCategory(categoria);
            // Si la categoria es nula, se lanza una excepción
            return ResponseEntity.ok(producto.orElseThrow(
                    () -> new ProductoNotFoundException("No se encontró el producto con categoría: " + categoria_id)));
            
        }
        // Si no se encuentra el producto, se lanza una excepción
        throw new ProductoNotFoundException("No se encontró el producto con categoría: " + categoria_id);
    }

    @GetMapping("/{marca}")
    public ResponseEntity<Producto> getProductoByMarca(@RequestParam String marca) throws ProductoNotFoundException {
        // Se puede obtener el producto por marca
        if (marca == null || marca.isEmpty())
            // Si la marca es nula o vacía, se lanza una excepción
            throw new ParametroFueraDeRangoException("La marca no puede ser nula o vacía");
        Optional<Producto> result = productoService.getProductoByMarca(marca);
        if (result.isPresent())
            return ResponseEntity.ok(result.get());
        // Si no se encuentra el producto, se lanza una excepción
        throw new ProductoNotFoundException("No se encontró el producto con marca: " + marca);
    }

    @GetMapping("/{precio}")
    public ResponseEntity<Producto> getProductoByPrecioMaximo(@RequestParam BigDecimal precioMax,
            @RequestParam BigDecimal precioMin) throws ProductoNotFoundException {
        // Se puede obtener el producto por precio
        Optional<Producto> result = Optional.empty();
        if (precioMax == null && precioMin == null) {
            // Si ambos son nulos, se lanza una excepción
            throw new ParametroFueraDeRangoException("Ambos precios no pueden ser nulos");
        } else if (precioMax != null && precioMin != null && precioMax.compareTo(precioMin) < 0) {
            // Si el precio máximo es menor al mínimo, se lanza una excepción
            throw new ParametroFueraDeRangoException("El precio máximo debe ser mayor al mínimo");
        } else if (precioMax == null) {
            // Si el precio máximo es nulo, se busca por precio mínimo
            result = productoService.getProductoByPrecioMinimo(precioMin);
        } else if (precioMin == null) {
            // Si el precio mínimo es nulo, se busca por precio máximo
            result = productoService.getProductoByPrecioMaximo(precioMax);
        } else {
            result = productoService.getProductoByPrecio(precioMax, precioMin);
        }
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        }
        // Si no se encuentra el producto, se lanza una excepción
        throw new ProductoNotFoundException(
                "No se encontraron productos con precio máximo: " + precioMax + " y mínimo: " + precioMin);
    }

    @PostMapping
    public ResponseEntity<Object> createProducto(@RequestBody ProductoRequest producto) throws ProductoDuplicateException, ParametroFueraDeRangoException {
        // Se puede crear un producto
        if (producto.getCategoria_id() < 1) {
            //Si el id de la categoria es menor a 1, se lanza una excepción
            throw new ParametroFueraDeRangoException("El id del producto debe ser mayor a 0");
        }
        if (producto.getNombre() == null || producto.getNombre().isEmpty()) {
            // Si el nombre es nulo o vacío, se lanza una excepción
            throw new ParametroFueraDeRangoException("El nombre del producto no puede ser nulo o vacío");
        }
        if (producto.getPrecio() == null || producto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            // Si el precio es nulo o menor a 0, se lanza una excepción
            throw new ParametroFueraDeRangoException("El precio no puede ser nulo o menor a 0");
        }
        if (producto.getCategoria_id() < 1) {
            // Si el id de la categoria es menor a 1, se lanza una excepción
            throw new ParametroFueraDeRangoException("El id de la categoría debe ser mayor a 0");
        }
        if (producto.getDescripcion() == null || producto.getDescripcion().isEmpty()) {
            // Si la descripción es nula o vacía, se lanza una excepción
            throw new ParametroFueraDeRangoException("La descripción no puede ser nula o vacía");
        }
        if (producto.getStock() < 0) {
            // Si el stock es menor a 0, se lanza una excepción
            throw new ParametroFueraDeRangoException("El stock no puede ser menor a 0");
        }
        if (producto.getStockMinimo() < 0) {
            // Si el stock mínimo es menor a 0, se lanza una excepción
            throw new ParametroFueraDeRangoException("El stock mínimo no puede ser menor a 0");
        }
        if (producto.getFechaVencimiento() == null || producto.getFechaVencimiento().isBefore(LocalDate.now())) {
            // Si la fecha de vencimiento es nula o es anterior a la fecha actual, se lanza una excepción
            throw new ParametroFueraDeRangoException("La fecha de vencimiento no puede ser nula");
        }
        if (producto.getVentasTotales() < 0) {
            // Si las ventas totales son menores a 0, se lanza una excepción
            throw new ParametroFueraDeRangoException("Las ventas totales no pueden ser menores a 0");
        }
        if (producto.getImagenes().size() > 10) {
            // Si la lista de imagenes es mayor a 10, se lanza una excepción
            throw new ParametroFueraDeRangoException("No se pueden agregar más de 10 imagenes");
        }
        // Si la categoria no existe, se lanza una excepción
        categorias.getCategoriaById(producto.getCategoria_id())
                .orElseThrow(() -> new ParametroFueraDeRangoException("La categoría no existe"));
        Optional<Producto> existingProduct = productoService.getProductoByName(producto.getNombre());
        if (existingProduct.isPresent()) {
            if (producto.getNombre() == existingProduct.get().getNombre() && producto.getDescripcion() == existingProduct.get().getDescripcion() 
                && producto.getMarca() == existingProduct.get().getMarca()) {
                // Si el producto ya existe, se lanza una excepción
                throw new ProductoDuplicateException("El producto ya existe");
            }
        }
        Producto result = productoService.createProducto(producto);
        return ResponseEntity.created(URI.create("/productos/" + result.getId())).body(new ProductoDTO(result));
    }

    @PutMapping
    public ResponseEntity<Object> updateProducto(@PathVariable int id, @RequestBody ProductoRequest productoRequest)
            throws ProductoNotFoundException {
        // Se puede actualizar un producto
        if (productoRequest.getCategoria_id() < 1) {
            //Si el id de la categoria es menor a 1, se lanza una excepción
            throw new ParametroFueraDeRangoException("El id del producto debe ser mayor a 0");
        }
        if (productoRequest.getNombre() == null || productoRequest.getNombre().isEmpty()) {
            // Si el nombre es nulo o vacío, se lanza una excepción
            throw new ParametroFueraDeRangoException("El nombre del producto no puede ser nulo o vacío");
        }
        if (productoRequest.getPrecio() == null || productoRequest.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            // Si el precio es nulo o menor a 0, se lanza una excepción
            throw new ParametroFueraDeRangoException("El precio no puede ser nulo o menor a 0");
        }
        if (productoRequest.getCategoria_id() < 1) {
            // Si el id de la categoria es menor a 1, se lanza una excepción
            throw new ParametroFueraDeRangoException("El id de la categoría debe ser mayor a 0");
        }
        if (productoRequest.getDescripcion() == null || productoRequest.getDescripcion().isEmpty()) {
            // Si la descripción es nula o vacía, se lanza una excepción
            throw new ParametroFueraDeRangoException("La descripción no puede ser nula o vacía");
        }
        if (productoRequest.getStock() < 0) {
            // Si el stock es menor a 0, se lanza una excepción
            throw new ParametroFueraDeRangoException("El stock no puede ser menor a 0");
        }
        if (productoRequest.getStockMinimo() < 0) {
            // Si el stock mínimo es menor a 0, se lanza una excepción
            throw new ParametroFueraDeRangoException("El stock mínimo no puede ser menor a 0");
        }
        if (productoRequest.getFechaVencimiento() == null || productoRequest.getFechaVencimiento().isBefore(LocalDate.now())) {
            // Si la fecha de vencimiento es nula o es anterior a la fecha actual, se lanza una excepción
            throw new ParametroFueraDeRangoException("La fecha de vencimiento no puede ser nula");
        }
        if (productoRequest.getVentasTotales() < 0) {
            // Si las ventas totales son menores a 0, se lanza una excepción
            throw new ParametroFueraDeRangoException("Las ventas totales no pueden ser menores a 0");
        }
        if (productoRequest.getImagenes().size() > 10) {
            // Si la lista de imagenes es mayor a 10, se lanza una excepción
            throw new ParametroFueraDeRangoException("No se pueden agregar más de 10 imagenes");
        }
        // Si la categoria no existe, se lanza una excepción
        categorias.getCategoriaById(productoRequest.getCategoria_id())
                .orElseThrow(() -> new ParametroFueraDeRangoException("La categoría no existe"));     
        Optional<Producto> result = productoService.getProductoById(id);
        if (result.isPresent()) {
            productoService.updateProducto(id, productoRequest);
            Producto updatedProducto = productoService.getProductoById(id).get();
            return ResponseEntity.ok(new ProductoDTO(updatedProducto));
        }
        // Si no se encuentra el producto, se lanza una excepción
        throw new ProductoNotFoundException("No se encontró el producto con id: " + id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteProducto(@PathVariable int id) throws ProductoNotFoundException {
        // Se puede eliminar un producto
        if (id < 1) {
            // Si el id es menor a 1, se lanza una excepción
            throw new ParametroFueraDeRangoException("El id del producto debe ser mayor a 0");
        }
        // Si el producto no existe, se lanza una excepción
        productoService.getProductoById(id)
                .orElseThrow(() -> new ProductoNotFoundException("No se encontró el producto con id: " + id));
        productoService.deleteProducto(id);
        return ResponseEntity.noContent().build();
    }
}
