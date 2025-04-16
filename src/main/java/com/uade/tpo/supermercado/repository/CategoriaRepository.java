package com.uade.tpo.supermercado.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.uade.tpo.supermercado.entity.Categoria;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    List<Categoria> findByParentCategoriaId(int parentId);
    boolean existsByNombreAndParentCategoriaId(String nombre, Integer parentId);
}
