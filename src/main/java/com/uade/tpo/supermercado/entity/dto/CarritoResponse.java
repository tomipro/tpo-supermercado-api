package com.uade.tpo.supermercado.entity.dto;

import java.util.List;

import lombok.Data;

@Data
public class CarritoResponse {
        private int id;
        private int usuarioId;
        private String estado;
        private List<ItemCarritoDTO> items;

        public CarritoResponse(int id, int usuarioId, String estado, List<ItemCarritoDTO> items) {
                this.id = id;
                this.usuarioId = usuarioId;
                this.estado = estado;
                this.items = items;
        }

}
