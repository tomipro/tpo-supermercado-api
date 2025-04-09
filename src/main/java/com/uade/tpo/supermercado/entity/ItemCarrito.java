package com.uade.tpo.supermercado.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
@Entity
@Data
public class ItemCarrito {
    //Es la tabla carrito_producto en sql

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int cantidad;

    @Column(nullable = false,precision = 10,scale = 2)
    private BigDecimal precio_unitario;

    //relacion con carrito,cada
    // item del carrito pertenece a un solo carrito
    @ManyToOne
    @JoinColumn(name = "carrito_id", nullable = false)
    private Carrito carrito;

    // En la relacion con producto es lo mismo que carrito
    @ManyToOne
    @JoinColumn(name = "producto_id",nullable = false)
    private Producto producto;

    public ItemCarrito(){

    }

    public ItemCarrito(int cantidad, BigDecimal precio_unirario, Carrito carrito, Producto producto) {
        this.cantidad = cantidad;
        this.precio_unitario = precio_unirario;
        this.carrito = carrito;
        this.producto = producto;
    }

    






    
}
