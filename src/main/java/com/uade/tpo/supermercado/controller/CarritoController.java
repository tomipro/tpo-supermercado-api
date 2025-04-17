package com.uade.tpo.supermercado.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.supermercado.entity.Carrito;
import com.uade.tpo.supermercado.entity.Producto;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;;
/*
 * @RestController
 * 
 * @RequestMapping("carritos")
 * public class CarritoController {
 * 
 * @Autowired
 * private CarritoService carritoService;
 * 
 * // post para crear carrito
 * 
 * @PostMapping("/usuarios/{usuarioid}/crearCarrito")
 * public ResponseEntity<Carrito> crearCarrito(@PathVariable int id) {
 * Carrito nuevoCarrito = carritoService.crearCarrito(id);
 * return ResponseEntity.created(URI.create("/carritos/" +
 * nuevoCarrito.getId())).body(nuevoCarrito);
 * }
 * 
 * // get para obtener carrito por id
 * 
 * @GetMapping("/usuarios/{usuarioid}")
 * public ResponseEntity<Carrito> obtenerCarrito(@PathVariable int usuarioid) {
 * Carrito carrito = carritoService.obtenerCarrito(usuarioid);
 * return ResponseEntity.ok(carrito);
 * }
 * 
 * // agregar producto al carrito
 * 
 * @PostMapping("/usuario/{usuarioId}/productos/{productoId}")
 * public ResponseEntity<Carrito> agregarProducto(
 * 
 * @PathVariable int usuarioId,
 * 
 * @PathVariable int productoId,
 * 
 * @RequestParam(defaultValue = "1") int cantidad) {
 * Carrito carritoActualizado = carritoService.agregarProducto(usuarioId,
 * productoId, cantidad);
 * return ResponseEntity.ok(carritoActualizado);
 * }
 * 
 * // eliminar producto del carrito
 * 
 * @DeleteMapping("/usuarios/{id}/productos/{productoId}")
 * public ResponseEntity<Carrito> eliminarProducto(@PathVariable int
 * id, @PathVariable int productoId) {
 * Carrito carritoActualizado = carritoService.eliminarProducto(id, productoId);
 * return ResponseEntity.ok(carritoActualizado);
 * }
 * 
 * // finalizar compra
 * 
 * @PostMapping("/usuarios/{id}/finalizar")
 * public ResponseEntity<Carrito> finalizarCompra(@PathVariable int id) {
 * Carrito carritoFinalizado = carritoService.finalizarCompra(id);
 * return ResponseEntity.ok(carritoFinalizado);
 * }
 * 
 * // eliminar todo el carrito
 * 
 * @DeleteMapping("/usuarios/{id}/vaciar")
 * public ResponseEntity<Carrito> vaciarCarrito(@PathVariable int id) {
 * Carrito carritoVaciado = carritoService.vaciarCarrito(id);
 * return ResponseEntity.ok(carritoVaciado);
 * }
 * 
 * }
 */
