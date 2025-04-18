package com.uade.tpo.supermercado.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.CascadeType;
import lombok.Data;

@Data
@Entity
public class Carrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private LocalDateTime fecha_creacion = LocalDateTime.now();

    @OneToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", nullable = false, unique = true)
    private Usuario usuario;

    @Column(nullable = false)
    private String estado; // Estado del carrito (Activo, Vacio)

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ItemCarrito> itemsCarrito = new ArrayList<>(); // Lista de los items del carrito

    public Carrito() {
    }

    public Carrito(Usuario usuario, String estado, LocalDateTime fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
        this.usuario = usuario;
        this.estado = estado;
        this.itemsCarrito = new ArrayList<>();// Inicializar la lista vacía
    }

}
