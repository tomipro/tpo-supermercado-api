package com.uade.tpo.supermercado.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
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
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 100, nullable = false)
    private String nombre;

    @Column(columnDefinition = "TEXT",nullable =false)
    private String descripcion;

    @Column(nullable = false,precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private int stock;

    @Column(length = 50)
    private String marca;

    @Column(length = 20)
    private String unidad_medida;

    @Column(name = "Fecha_Vencimiento", nullable = false)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private int categoria_id;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int ventas_totales;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int stock_minimo;

    @Column(length = 20,nullable = false,columnDefinition = "VARCHAR(20) DEFAULT 'activo'")
    private String estado;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Imagen> imagenes = new ArrayList<>();

    public Producto(){

    }
        
}
