package com.uade.tpo.supermercado.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.uade.tpo.supermercado.entity.*;
import com.uade.tpo.supermercado.entity.dto.CarritoResponse;
import com.uade.tpo.supermercado.entity.dto.ItemCarritoDTO;
import com.uade.tpo.supermercado.repository.ProductoRepository;
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
    private UsuarioService usuarioService;
    @Autowired
    private ProductoService productoService;

    @Override
    public Carrito crearCarrito(int usuarioId) {
        Usuario usuario = usuarioService.getUsuarioByIdOrThrow(usuarioId); // Obtener el usuario por ID o lanzar
                                                                           // excepción si no exist

        // Verificar si el usuario ya tiene un carrito
        Optional<Carrito> carritoExistente = carritoRepository.findByUsuario(usuario);

        if (carritoExistente.isPresent()) {
            // Si ya tiene un carrito, no crear otro
            throw new IllegalArgumentException("El usuario ya tiene un carrito.");
        }

        // Crear el carrito con estado "VACIO"
        Carrito nuevoCarrito = new Carrito(usuario, "VACIO", LocalDateTime.now());

        carritoRepository.save(nuevoCarrito); // Guardar el nuevo carrito en la base de datos

        return nuevoCarrito; // Retornar el carrito recién creado

    }

    @Override
    public Carrito obtenerCarrito(int usuarioId) {
        // Obtener el usuario por ID o lanzar una excepción si no existe
        Usuario usuario = usuarioService.getUsuarioByIdOrThrow(usuarioId);

        // Buscar el carrito del usuario
        Optional<Carrito> carritoExistente = carritoRepository.findByUsuario(usuario);

        // Si no existe el carrito, crear uno vacío
        if (!carritoExistente.isPresent()) {
            return crearCarrito(usuarioId);
        }

        Carrito carrito = carritoExistente.get();

        // Si el carrito está vacío, asegurarse de que su estado sea "VACIO"
        if (carrito.getItemsCarrito().isEmpty()) {
            carrito.setEstado("VACIO");
        } else {
            // Si hay productos, asegurarse de que el estado sea "ACTIVO"
            carrito.setEstado("ACTIVO");
        }

        return carrito;

    }

    @Override
    @Transactional
    public Carrito agregarProducto(int usuarioId, int productoId, int cantidad) {
        Usuario usuario = usuarioService.getUsuarioByIdOrThrow(usuarioId); // Validar existencia del usuario

        // Obtener el carrito (activo o en proceso)
        Carrito carrito = carritoRepository.findByUsuarioAndEstado(usuario, "VACIO")
                .orElse(carritoRepository.findByUsuarioAndEstado(usuario, "ACTIVO")
                        .orElseGet(() -> crearCarrito(usuarioId)));

        // Validar existencia del producto
        Producto producto = productoService.getProductoById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + productoId));

        // Validar stock disponible
        if (producto.getStock() < cantidad) {
            throw new IllegalArgumentException("No hay suficiente stock para el producto con ID: " + productoId);
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
                throw new IllegalArgumentException("No se puede agregar más productos que el stock disponible.");
            }

            item.setCantidad(nuevaCantidad); // actualizar cantidad
        } else {
            // Si no existe, crear nuevo ItemCarrito
            ItemCarrito nuevoItem = new ItemCarrito(cantidad, producto.getPrecio(), carrito, producto);
            carrito.getItemsCarrito().add(nuevoItem);
        }

        // Si el carrito estaba vacío, cambiar su estado a "ACTIVO"
        if (carrito.getEstado().equals("VACIO")) {
            carrito.setEstado("ACTIVO");
        }

        // Guardar carrito y devolver
        return carritoRepository.save(carrito);

    }

    @Override
    public Carrito eliminarProducto(int usuarioId, int productoId) {
        // 1. Validar existencia del usuario
        Usuario usuario = usuarioService.getUsuarioByIdOrThrow(usuarioId);

        // 2. Obtener el carrito del usuario
        Carrito carrito = carritoRepository.findByUsuarioAndEstado(usuario, "ACTIVO")
                .orElseThrow(() -> new IllegalArgumentException("No existe un carrito activo para el usuario"));

        // 3. Verificar si el producto está en el carrito
        Optional<ItemCarrito> itemCarrito = carrito.getItemsCarrito().stream()
                .filter(item -> item.getProducto().getId() == productoId)
                .findFirst();

        // Si el producto no está en el carrito, lanzar una excepción
        if (!itemCarrito.isPresent()) {
            throw new IllegalArgumentException("El producto no está en el carrito");
        }

        // 4. Eliminar el producto del carrito
        ItemCarrito item = itemCarrito.get();
        carrito.getItemsCarrito().remove(item);

        // 5. Verificar si el carrito está vacío después de la eliminación
        if (carrito.getItemsCarrito().isEmpty()) {
            carrito.setEstado("VACIO");
        }

        // 6. Persistir los cambios
        carritoRepository.save(carrito); // Guardar los cambios en el carrito

        // 7. Retornar el carrito actualizado
        return carrito;

    }

    @Override
    public Carrito vaciarCarrito(int usuarioId) {
        // 1. Validar existencia del usuario
        Usuario usuario = usuarioService.getUsuarioByIdOrThrow(usuarioId);

        // 2. Obtener el carrito del usuario
        Carrito carrito = carritoRepository.findByUsuarioAndEstado(usuario, "ACTIVO")
                .orElseThrow(() -> new IllegalArgumentException("Este Carrito ya esta Vacio!"));

        // 3. Vaciar el carrito eliminando todos los productos (o poniéndolos en
        // cantidad 0 si prefieres esa opción)
        carrito.getItemsCarrito().clear(); // Elimina todos los productos del carrito

        // 4. Cambiar el estado del carrito a "VACIO"
        carrito.setEstado("VACIO");

        // 5. Persistir los cambios
        carritoRepository.save(carrito); // Guardar el carrito vacío en la base de datos

        // 6. Retornar el carrito actualizado
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
