package com.uade.tpo.supermercado.service;

import java.util.List;
import com.uade.tpo.supermercado.entity.*;
import com.uade.tpo.supermercado.entity.dto.OrdenResponseDTO;

public interface OrdenService {
    Orden finalizarCompra(int usuarioId); // Método para finalizar la compra y crear una orden

    Orden obtenerOrden(int usuarioId, int ordenId); // Método para obtener una orden específica de un usuario

    List<Orden> obtenerOrdenes(int usuarioId); // Método para obtener todas las órdenes de un usuario

    public OrdenResponseDTO convertirAOrdenResponse(Orden orden); // Método para convertir una orden a su representación
                                                                  // DTO (OrdenResponse)

}
