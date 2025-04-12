package com.uade.tpo.supermercado.entity.dto;

public class CategoryRequest {
    private String description;
    private Integer parentId; // puede ser null- Si es null es una Categoria padre

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    
}}
