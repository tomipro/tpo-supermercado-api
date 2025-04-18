package com.uade.tpo.supermercado.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.tpo.supermercado.entity.Carrito;
import com.uade.tpo.supermercado.entity.Usuario;

import java.util.Optional;

public interface CarritoRepository extends JpaRepository<Carrito, Integer> {

    // Buscar carrito por usuario
    Optional<Carrito> findByUsuario(Usuario usuario);

    // Buscar carrito por usuario y estado ("ACTIVO", "VACIO")
    Optional<Carrito> findByUsuarioAndEstado(Usuario usuario, String estado);

}
