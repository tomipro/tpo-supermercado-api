package com.uade.tpo.supermercado.controller;

import java.net.URI;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.uade.tpo.supermercado.entity.Carrito;
import com.uade.tpo.supermercado.entity.Usuario;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.uade.tpo.supermercado.service.CarritoService;
import com.uade.tpo.supermercado.entity.dto.*;
import com.uade.tpo.supermercado.excepciones.NoEncontradoException;
import com.uade.tpo.supermercado.service.UsuarioService;

@RestController
@RequestMapping("carritos")
public class CarritoController {
    @Autowired
    private CarritoService carritoService;
    @Autowired
    private UsuarioService usuarioService;

    // post para crear carrito
    @PostMapping
    public ResponseEntity<CarritoResponse> crearCarrito(Principal principal) {
        // Obtener el usuario a partir del token JWT
        Usuario usuario = getUsuarioIdDesdePrincipal(principal);
        // Crear el carrito para el usuario
        Carrito nuevoCarrito = carritoService.crearCarrito(usuario);
        return ResponseEntity
                .created(URI.create("/carritos/" + nuevoCarrito.getId()))
                .body(carritoService.convertirACarritoResponse(nuevoCarrito));
    }

    // GET: obtener carrito por usuario
    @GetMapping
    public ResponseEntity<CarritoResponse> obtenerCarrito(Principal principal) {

        // Obtener el usuario a partir del token JWT
        Usuario usuario = getUsuarioIdDesdePrincipal(principal);

        // Obtener el carrito del usuario
        Carrito carrito = carritoService.obtenerCarrito(usuario);

        return ResponseEntity.ok(carritoService.convertirACarritoResponse(carrito));
    }

    // UPDATE: agregar producto al carrito
    // Patch mapping porque no estás agregando/modificando un ítem
    // dentro del carrito (no reemplazando todo el carrito ni el producto)
    @PatchMapping("/{productoId}")
    public ResponseEntity<CarritoResponse> agregarProducto(
            Principal principal,
            @PathVariable int productoId,
            @RequestParam(defaultValue = "1") int cantidad) {

        // Obtener el usuario a partir del token JWT
        Usuario usuario = getUsuarioIdDesdePrincipal(principal);

        // Crear el carrito para el usuario
        Carrito carritoActualizado = carritoService.agregarProducto(usuario, productoId, cantidad);

        return ResponseEntity.ok(carritoService.convertirACarritoResponse(carritoActualizado));
    }

    @DeleteMapping("/{productoId}")
    public ResponseEntity<CarritoResponse> eliminarProducto(
            Principal principal,
            @PathVariable int productoId,
            @RequestParam(defaultValue = "1") int cantidad) {

        // Obtener el usuario a partir del token JWT
        Usuario usuario = getUsuarioIdDesdePrincipal(principal);

        // Eliminar la cantidad especificada del producto del carrito
        Carrito carritoActualizado = carritoService.eliminarProducto(usuario, productoId, cantidad);

        return ResponseEntity.ok(carritoService.convertirACarritoResponse(carritoActualizado));
    }

    // DELETE: vaciar todo el carrito
    @DeleteMapping
    public ResponseEntity<CarritoResponse> vaciarCarrito(Principal principal) {

        // Obtener el usuario a partir del token JWT
        Usuario usuario = getUsuarioIdDesdePrincipal(principal);

        // Eliminar el carrito del usuario
        Carrito carritoVaciado = carritoService.vaciarCarrito(usuario);

        return ResponseEntity.ok(carritoService.convertirACarritoResponse(carritoVaciado));
    }

    private Usuario getUsuarioIdDesdePrincipal(Principal principal) {
        String username = principal.getName();
        Usuario usuario = usuarioService.getUsuarioByUsername(username)
                .orElseThrow(() -> new NoEncontradoException("Usuario no encontrado"));
        return usuario;
    }
}
