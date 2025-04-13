package com.uade.tpo.supermercado.entity.dto;

public class CategoryRequest {
    private String nombre; // nombre de la categoria
    private Integer parentId; // puede ser null- Si es null es una Categoria padre

    public String getNombre() {
        return nombre;
    } 
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    
}}
