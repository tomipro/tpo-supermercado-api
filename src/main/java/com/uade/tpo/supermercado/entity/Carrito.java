package com.uade.tpo.supermercado.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity
public class Carrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

 

    @Column(nullable = false,columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fecha_creacion;

    @OneToOne
    @JoinColumn(name="usuario_id",referencedColumnName = "id",nullable = false)
    private Usuario usuario;


    public Carrito(){

    }


    public Carrito(LocalDateTime fecha_creacion, Usuario usuario) {
        this.fecha_creacion = fecha_creacion;
        this.usuario = usuario;
    }

    


    
}
