package com.uade.tpo.supermercado.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.uade.tpo.supermercado.entity.Orden;
import com.uade.tpo.supermercado.entity.dto.OrdenResponseDTO;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import com.uade.tpo.supermercado.service.OrdenService;

@RestController
@RequestMapping("ordenes")
public class OrdenController {
    @Autowired
    private OrdenService ordenService;

    // finalizar compra
    @PostMapping("/usuarios/{id}/finalizar")
    public ResponseEntity<OrdenResponseDTO> finalizarCompra(@PathVariable int id) {
        Orden orden = ordenService.finalizarCompra(id);
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
