package com.uade.tpo.supermercado.service;

import java.math.BigDecimal;
import com.uade.tpo.supermercado.entity.*;
import com.uade.tpo.supermercado.entity.dto.ItemOrdenDTO;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.uade.tpo.supermercado.repository.OrdenRepository;
import com.uade.tpo.supermercado.repository.CarritoRepository;
import com.uade.tpo.supermercado.repository.ProductoRepository;
import com.uade.tpo.supermercado.repository.DetalleOrdenRepository;
import java.util.List;
import com.uade.tpo.supermercado.entity.dto.OrdenResponseDTO;
import com.uade.tpo.supermercado.excepciones.EstadoInvalidoException;
import com.uade.tpo.supermercado.excepciones.NoEncontradoException;
import com.uade.tpo.supermercado.excepciones.StockInsuficienteException;

@Service

public class OrdenServiceImpl implements OrdenService {
    @Autowired
    private OrdenRepository ordenRepository;
    @Autowired
    private CarritoRepository carritoRepository;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private DetalleOrdenRepository detalleOrdenRepository;

    @Override
    @Transactional
    public Orden finalizarCompra(Usuario usuario) {

        // 1. Obtener el carrito del usuario
        Carrito carrito = carritoRepository.findByUsuarioIdAndEstado(usuario.getId(), EstadoCarrito.ACTIVO)
                .orElseThrow(() -> new EstadoInvalidoException("El carrito esta vacio"));

        // 2. Verificar el stock de los productos en el carrito
        for (ItemCarrito item : carrito.getItemsCarrito()) {
            Producto producto = item.getProducto();
            if (producto.getStock() - producto.getStock_minimo() < item.getCantidad()) {
                throw new StockInsuficienteException(
                        "No hay suficiente stock para el producto: " + producto.getNombre());
            }
        }

        // 5. Calcular el total de la compra
        BigDecimal totalCompra = carrito.getItemsCarrito().stream()
                .map(item -> item.getProducto().getPrecio().multiply(new BigDecimal(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 6. Crear la orden
        Orden orden = new Orden(usuario, totalCompra, LocalDateTime.now(), "FINALIZADA", null, BigDecimal.ZERO);

        // 7. Guardar la orden
        ordenRepository.save(orden);

        // 8. Crear los detalles de la orden y actualizar el stock de los productos
        for (ItemCarrito item : carrito.getItemsCarrito()) {
            BigDecimal subtotal = item.getProducto().getPrecio().multiply(new BigDecimal(item.getCantidad()));

            // Crear el detalle de la orden
            DetalleOrden detalle = new DetalleOrden(item.getCantidad(), item.getProducto().getPrecio(), subtotal, orden,
                    item.getProducto());
            detalleOrdenRepository.save(detalle);
            orden.getItemsOrden().add(detalle);

            // Actualizar el stock del producto
            Producto producto = item.getProducto();
            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);
        }

        // 9. Vaciar el carrito
        carrito.getItemsCarrito().clear();
        carrito.setEstado(EstadoCarrito.VACIO);

        // 10. Guardar el carrito vacío
        carritoRepository.save(carrito);

        // 11. Devolver la orden
        return orden;

    }

    @Override
    public Orden obtenerOrden(int usuarioId, int ordenId) {
        // 1. Validar existencia del usuario para evitar errores
        if (usuarioService.getUsuarioById(usuarioId).isEmpty()) {
            throw new NoEncontradoException("El usuario no existe.");
        }
        // 2. Buscar la orden
        return ordenRepository.buscarOrdenDeUsuario(ordenId, usuarioId)
                .orElseThrow(() -> new NoEncontradoException("No se encontró la orden para este usuario"));

    }

    @Override
    public List<Orden> obtenerOrdenes(int usuarioId) {
        // 1. Validar existencia del usuario
        Usuario usuario = usuarioService.getUsuarioByIdOrThrow(usuarioId);
        // 2. Obtener las ordenes del usuario
        List<Orden> ordenes = ordenRepository.findByUsuario(usuario);
        // 3. Verificar que el usuario tenga ordenes
        if (ordenes.isEmpty()) {
            throw new NoEncontradoException("El usuario no tiene ordenes.");
        }
        // 4. Devolver las ordenes
        return ordenes;
    }

    @Override
    public OrdenResponseDTO convertirAOrdenResponse(Orden orden) {
        List<ItemOrdenDTO> items = orden.getItemsOrden().stream()
                .map(item -> new ItemOrdenDTO(
                        item.getProducto().getId(),
                        item.getProducto().getNombre(),
                        item.getCantidad(),
                        item.getProducto().getPrecio().doubleValue()))
                .toList();

        return new OrdenResponseDTO(
                orden.getId(),
                orden.getUsuario().getId(),
                orden.getFecha(),
                orden.getEstado(),
                orden.getTotal().doubleValue(),
                items);
    }
}
