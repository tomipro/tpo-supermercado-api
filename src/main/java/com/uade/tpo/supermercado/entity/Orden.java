package com.uade.tpo.supermercado.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Orden {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int usuario_id;//despues se agrega relacion

    @Column(nullable = false,precision = 10,scale = 2 )
    private BigDecimal total;

    @Column(nullable = false,columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fecha;

    @Column(length = 20)
    private String estado;

    @Column(length = 50)
    private String direccion;

    @Column(nullable = false,precision = 10, scale = 2,columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private BigDecimal descuentoTotal;

    public Orden(){

    }

    public Orden(int usuario_id, BigDecimal total, LocalDateTime fecha, String estado, String direccion,
            BigDecimal descuentoTotal) {
        this.usuario_id = usuario_id;
        this.total = total;
        this.fecha = fecha;
        this.estado = estado;
        this.direccion = direccion;
        this.descuentoTotal = descuentoTotal;
    }

    


}
