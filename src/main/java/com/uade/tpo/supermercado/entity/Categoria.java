package com.uade.tpo.supermercado.entity;

import java.util.ArrayList;
import java.util.List;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 50,nullable = false )
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private Categoria parentCategoria;

    @OneToMany(mappedBy = "parentCategoria",cascade = CascadeType.ALL)
    private List<Categoria> subcategorias;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Producto> productos = new ArrayList<>();

    public Categoria(){

    }

    public Categoria(String nombre, Categoria parentCategoria, List<Categoria> subcategorias) {
        this.nombre = nombre;
        this.parentCategoria = parentCategoria;
        this.subcategorias = subcategorias;
    }
    







    
}
