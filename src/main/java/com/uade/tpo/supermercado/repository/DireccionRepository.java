package com.uade.tpo.supermercado.repository;

import com.uade.tpo.supermercado.entity.Direccion;
import com.uade.tpo.supermercado.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DireccionRepository extends JpaRepository<Direccion, Integer> {
    List<Direccion> findByUsuario(Usuario usuario);
}
