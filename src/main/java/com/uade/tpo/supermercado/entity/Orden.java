package com.uade.tpo.supermercado.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public class Orden {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fecha;

    @Column(length = 20)
    private String estado;

    @Column(length = 50)
    private String direccion;

    @Column(nullable = false, precision = 10, scale = 2, columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private BigDecimal descuentoTotal;

    public Orden() {

    }

    public Orden(Usuario usuario, BigDecimal total, LocalDateTime fecha, String estado, String direccion,
            BigDecimal descuentoTotal) {
        this.usuario = usuario;
        this.total = total;
        this.fecha = fecha;
        this.estado = estado;
        this.direccion = direccion;
        this.descuentoTotal = descuentoTotal;
    }

}
