package com.uade.tpo.supermercado.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.uade.tpo.supermercado.entity.*;
import com.uade.tpo.supermercado.entity.dto.CarritoResponse;
import com.uade.tpo.supermercado.entity.dto.ItemCarritoDTO;
import com.uade.tpo.supermercado.excepciones.DatoDuplicadoException;
import com.uade.tpo.supermercado.excepciones.EstadoInvalidoException;
import com.uade.tpo.supermercado.excepciones.NoEncontradoException;
import com.uade.tpo.supermercado.excepciones.StockInsuficienteException;
import com.uade.tpo.supermercado.repository.CarritoRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.List;

@Service
public class CarritoServiceImpl implements CarritoService {
    @Autowired
    private CarritoRepository carritoRepository;
    @Autowired
    private ProductoService productoService;

    @Override
    public Carrito crearCarrito(Usuario usuario) {
        // Chequear si ya tiene un carrito ACTIVO o VACIO
        boolean yaTieneCarrito = carritoRepository
                .findByUsuarioIdAndEstadoConItems(usuario.getId(), EstadoCarrito.ACTIVO)
                .isPresent()
                || carritoRepository.findByUsuarioIdAndEstadoConItems(usuario.getId(), EstadoCarrito.VACIO).isPresent();

        if (yaTieneCarrito) {
            throw new DatoDuplicadoException("El usuario ya tiene un carrito.");
        }
        // Crear el carrito con estado "VACIO"
        Carrito nuevoCarrito = new Carrito(usuario, EstadoCarrito.VACIO, LocalDateTime.now());

        carritoRepository.save(nuevoCarrito); // Guardar el nuevo carrito en la base de datos

        return nuevoCarrito; // Retornar el carrito recién creado

    }

    @Override
    public Carrito obtenerCarrito(Usuario usuario) {

        // Buscar el carrito del usuario
        Optional<Carrito> carritoExistente = carritoRepository.findByUsuario(usuario);

        // Si no existe el carrito, crear uno vacío
        if (!carritoExistente.isPresent()) {
            return crearCarrito(usuario);
        }

        Carrito carrito = carritoExistente.get();

        // Si el carrito está vacío, asegurarse de que su estado sea "VACIO"
        if (carrito.getItemsCarrito().isEmpty()) {
            carrito.setEstado(EstadoCarrito.VACIO);
        } else {
            // Si hay productos, asegurarse de que el estado sea "ACTIVO"
            carrito.setEstado(EstadoCarrito.ACTIVO);
        }

        return carrito;

    }

    @Override
    @Transactional
    public Carrito agregarProducto(Usuario usuario, int productoId, int cantidad) {

        // Obtener el carrito (activo o vacio)
        Carrito carrito = carritoRepository.findByUsuarioIdAndEstadoConItems(usuario.getId(), EstadoCarrito.VACIO)
                .or(() -> carritoRepository.findByUsuarioIdAndEstadoConItems(usuario.getId(), EstadoCarrito.ACTIVO))
                .orElseGet(() -> crearCarrito(usuario));

        // Validar existencia del producto
        Producto producto = productoService.getProductoById(productoId)
                .orElseThrow(() -> new NoEncontradoException("Producto no encontrado con ID: " + productoId));

        // Validar stock disponible
        if (producto.getStock_minimo() < cantidad) {
            throw new StockInsuficienteException("No hay suficiente stock para el producto con ID: " + productoId);
        }

        // Verificar si el producto ya está en el carrito
        Optional<ItemCarrito> itemExistente = carrito.getItemsCarrito().stream()
                .filter(item -> item.getProducto().getId() == productoId)
                .findFirst();

        if (itemExistente.isPresent()) {
            // Si ya existe, sumar la cantidad
            ItemCarrito item = itemExistente.get();
            int nuevaCantidad = item.getCantidad() + cantidad;

            if (nuevaCantidad > producto.getStock()) {
                throw new StockInsuficienteException("No se puede agregar más productos que el stock disponible.");
            }

            item.setCantidad(nuevaCantidad); // actualizar cantidad
        } else {
            // Si no existe, crear nuevo ItemCarrito
            ItemCarrito nuevoItem = new ItemCarrito(cantidad, producto.getPrecio(), carrito, producto);
            carrito.getItemsCarrito().add(nuevoItem);
        }

        // Si el carrito estaba vacío, cambiar su estado a "ACTIVO"
        if (carrito.getEstado() == EstadoCarrito.VACIO) {
            carrito.setEstado(EstadoCarrito.ACTIVO);
        }

        // Guardar carrito y devolver
        return carritoRepository.save(carrito);

    }

    @Override
    public Carrito eliminarProducto(Usuario usuario, int productoId, int cantidad) {

        // 1. Obtener el carrito del usuario
        Carrito carrito = carritoRepository.findByUsuarioIdAndEstadoConItems(usuario.getId(), EstadoCarrito.ACTIVO)
                .orElseThrow(() -> new EstadoInvalidoException("El carrito esta vacio"));

        // 2. Verificar si el producto está en el carrito
        Optional<ItemCarrito> itemCarrito = carrito.getItemsCarrito().stream()
                .filter(item -> item.getProducto().getId() == productoId)
                .findFirst();

        // 3. Si el producto no está en el carrito, lanzar una excepción
        if (!itemCarrito.isPresent()) {
            throw new NoEncontradoException("El producto no está en el carrito");
        }

        // 4. Eliminar el producto del carrito
        ItemCarrito item = itemCarrito.get();
        if (item.getCantidad() > cantidad) {
            item.setCantidad(item.getCantidad() - cantidad);
        } else {
            carrito.getItemsCarrito().remove(item);
        }
        // 5. Verificar si el carrito está vacío después de la eliminación
        if (carrito.getItemsCarrito().isEmpty()) {
            carrito.setEstado(EstadoCarrito.VACIO);
        }

        // 6. Persistir los cambios
        carritoRepository.save(carrito); // Guardar los cambios en el carrito

        // 7. Retornar el carrito actualizado
        return carrito;

    }

    @Override
    public Carrito vaciarCarrito(Usuario usuario) {
        // 1. Obtener el carrito del usuario
        Carrito carrito = carritoRepository.findByUsuarioIdAndEstadoConItems(usuario.getId(), EstadoCarrito.ACTIVO)
                .orElseThrow(() -> new EstadoInvalidoException("El carrito esta vacio"));

        // 2. Vaciar el carrito eliminando todos los productos del carrito
        carrito.getItemsCarrito().clear(); // Elimina todos los productos del carrito

        // 3. Cambiar el estado del carrito a "VACIO"
        carrito.setEstado(EstadoCarrito.VACIO);

        // 4. Persistir los cambios
        carritoRepository.save(carrito); // Guardar el carrito vacío en la base de datos

        // 5. Retornar el carrito actualizado
        return carrito;

    }

    public CarritoResponse convertirACarritoResponse(Carrito carrito) {
        List<ItemCarritoDTO> items = carrito.getItemsCarrito().stream()
                .map(item -> new ItemCarritoDTO(
                        item.getProducto().getId(),
                        item.getProducto().getNombre(),
                        item.getCantidad(),
                        item.getProducto().getPrecio().doubleValue()))
                .collect(Collectors.toList());

        return new CarritoResponse(
                carrito.getId(),
                carrito.getUsuario().getId(),
                carrito.getEstado().toString(),
                items);

    }

}
