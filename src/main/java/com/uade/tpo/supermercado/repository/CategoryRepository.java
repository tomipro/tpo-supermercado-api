package com.uade.tpo.supermercado.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.uade.tpo.supermercado.entity.Categoria;
import com.uade.tpo.supermercado.entity.Producto;
import org.springframework.stereotype.Repository;

@Repository

public interface CategoryRepository extends JpaRepository<Categoria, Integer> {
    List<Categoria> findByParentCategoriaId(int parentId);

    boolean existsByNombreAndParentCategoriaId(String nombre, Integer parentId);

}
