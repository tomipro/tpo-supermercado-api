package com.uade.tpo.supermercado.controller;

import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.uade.tpo.supermercado.entity.Carrito;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.uade.tpo.supermercado.service.CarritoService;
import com.uade.tpo.supermercado.entity.dto.*;;;

@RestController
@RequestMapping("carritos")
public class CarritoController {
    @Autowired
    private CarritoService carritoService;

    // post para crear carrito
    @PostMapping("/usuarios/{usuarioid}/crearCarrito")
    public ResponseEntity<CarritoResponse> crearCarrito(@PathVariable int id) {
        Carrito nuevoCarrito = carritoService.crearCarrito(id);
        return ResponseEntity
                .created(URI.create("/carritos/" + nuevoCarrito.getId()))
                .body(carritoService.convertirACarritoResponse(nuevoCarrito));
    }

    // GET: obtener carrito por usuario
    @GetMapping("/usuarios/{usuarioid}")
    public ResponseEntity<CarritoResponse> obtenerCarrito(@PathVariable int usuarioid) {
        Carrito carrito = carritoService.obtenerCarrito(usuarioid);
        return ResponseEntity.ok(carritoService.convertirACarritoResponse(carrito));
    }

    // POST: agregar producto al carrito
    @PostMapping("/usuario/{usuarioId}/productos/{productoId}")
    public ResponseEntity<CarritoResponse> agregarProducto(
            @PathVariable int usuarioId,
            @PathVariable int productoId,
            @RequestParam(defaultValue = "1") int cantidad) {
        Carrito carritoActualizado = carritoService.agregarProducto(usuarioId, productoId, cantidad);
        return ResponseEntity.ok(carritoService.convertirACarritoResponse(carritoActualizado));
    }

    // DELETE: eliminar producto del carrito
    @DeleteMapping("/usuarios/{usuarioid}/productos/{productoId}")
    public ResponseEntity<CarritoResponse> eliminarProducto(@PathVariable int id, @PathVariable int productoId) {
        Carrito carritoActualizado = carritoService.eliminarProducto(id, productoId);
        return ResponseEntity.ok(carritoService.convertirACarritoResponse(carritoActualizado));
    }

    // DELETE: vaciar todo el carrito
    @DeleteMapping("/usuarios/{usuarioid}/vaciar")
    public ResponseEntity<CarritoResponse> vaciarCarrito(@PathVariable int id) {
        Carrito carritoVaciado = carritoService.vaciarCarrito(id);
        return ResponseEntity.ok(carritoService.convertirACarritoResponse(carritoVaciado));
    }
}
