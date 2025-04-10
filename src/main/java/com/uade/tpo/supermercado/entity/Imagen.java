package com.uade.tpo.supermercado.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Imagen {

    @Id
    private int id;
    
    @Column
    private String imagen;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    
}
