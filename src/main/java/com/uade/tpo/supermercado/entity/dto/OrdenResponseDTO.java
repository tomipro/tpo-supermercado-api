package com.uade.tpo.supermercado.entity.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class OrdenResponseDTO {

    private int ordenId;
    private int usuarioId;
    private LocalDateTime fechaCreacion;
    private String estado;
    private double total;
    private List<ItemOrdenDTO> items;

}
