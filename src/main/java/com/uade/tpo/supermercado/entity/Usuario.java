package com.uade.tpo.supermercado.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
@Entity
@Data
public class Usuario  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 50,unique = true,nullable = false)
    private String username;
   
    @Column(length = 100,unique = true,nullable = false)
    private String email;

    @Column(length = 100,nullable = false)
    private String password;


    @Column(length = 50,nullable = false)
    private String nombre;

    @Column(length = 50,nullable = false)
    private String apellido;

    @Column(length = 20,nullable = false)
    private String rol;

    @Column(nullable = false,columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fecha_registro;

    public Usuario(){
        
    }

    public Usuario( String username, String email, String password, String nombre, String apellido, String rol,
            LocalDateTime fecha_registro) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rol = rol;
        this.fecha_registro = fecha_registro;
    }
    





    

    
}
