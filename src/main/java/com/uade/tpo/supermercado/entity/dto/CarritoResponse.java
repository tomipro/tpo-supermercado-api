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

        public int getId() {
                return id;
        }

        public void setId(int id) {
                this.id = id;
        }

        public int getUsuarioId() {
                return usuarioId;
        }

        public void setUsuarioId(int usuarioId) {
                this.usuarioId = usuarioId;
        }

        public String getEstado() {
                return estado;
        }

        public void setEstado(String estado) {
                this.estado = estado;
        }

        public List<ItemCarritoDTO> getItems() {
                return items;
        }

        public void setItems(List<ItemCarritoDTO> items) {
                this.items = items;
        }
}
