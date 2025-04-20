package com.uade.tpo.supermercado.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.uade.tpo.supermercado.entity.Orden;
import com.uade.tpo.supermercado.entity.Usuario;
import com.uade.tpo.supermercado.entity.dto.OrdenResponseDTO;
import com.uade.tpo.supermercado.excepciones.NoEncontradoException;

import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;
import com.uade.tpo.supermercado.service.OrdenService;
import com.uade.tpo.supermercado.service.UsuarioService;

@RestController
@RequestMapping("ordenes")
public class OrdenController {
    @Autowired
    private OrdenService ordenService;

    @Autowired
    private UsuarioService usuarioService;

    // Finalizar compra
    @PostMapping
    public ResponseEntity<OrdenResponseDTO> finalizarCompra(Principal principal) {
        // Obtener el usuario a partir del token JWT
        String username = principal.getName();
        Usuario usuario = usuarioService.getUsuarioByUsername(username)
                .orElseThrow(() -> new NoEncontradoException("Usuario no encontrado"));

        Orden orden = ordenService.finalizarCompra(usuario);
        OrdenResponseDTO dto = ordenService.convertirAOrdenResponse(orden);
        return ResponseEntity.ok(dto);
    }

    // obtener una orden de un usuario
    @GetMapping("/{ordenId}/usuarios/{id}")
    public ResponseEntity<OrdenResponseDTO> obtenerOrden(@PathVariable int id, @PathVariable int ordenId) {
        Orden orden = ordenService.obtenerOrden(id, ordenId);
        OrdenResponseDTO dto = ordenService.convertirAOrdenResponse(orden);
        return ResponseEntity.ok(dto);
    }

    // obtener todas las ordenes de un usuario por id
    @GetMapping("/usuarios/{id}")
    public ResponseEntity<List<OrdenResponseDTO>> obtenerOrdenes(@PathVariable int id) {
        List<Orden> ordenes = ordenService.obtenerOrdenes(id);
        List<OrdenResponseDTO> dtos = ordenes.stream()
                .map(ordenService::convertirAOrdenResponse)
                .toList();
        return ResponseEntity.ok(dtos);
    }

}
