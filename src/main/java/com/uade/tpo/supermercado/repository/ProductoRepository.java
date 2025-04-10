package com.uade.tpo.supermercado.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.uade.tpo.supermercado.entity.Categoria;
import com.uade.tpo.supermercado.entity.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    @Query(value = "select p from Producto p where p.nombreProducto = ?1")
    Optional<Producto> findByNombreProducto(String nombreProducto);

    @Query(value = "select p from Producto p where p.marca = ?1")
    Optional<Producto> findByMarca(String marca);

    @Query(value = "select p from Producto p where p.precio = ?1")
    Optional<Producto> findByPrecio(BigDecimal precio);

    @Query(value = "select p from Producto p where p.categoria = ?1")
    Optional<Producto> findByCategoria(Categoria categoria);

    @Query(value = "Select p from Producto")
    Optional<Producto> findAllProductos();

    @Query(value = "Insert into Producto (nombreProducto, marca, precio, categoria) values (?1, ?2, ?3, ?4)", nativeQuery = true)
    Producto createProducto(String nombreProducto, String marca, BigDecimal precio, Categoria categoria);

    @Query(value = "Update Producto set nombreProducto = ?1, marca = ?2, precio = ?3, categoria = ?4 where id = ?5", nativeQuery = true)
    void updateProducto(String nombreProducto, String marca, BigDecimal precio, Categoria categoria, int id);

    @Query(value = "Delete from Producto where id = ?1", nativeQuery = true)
    void deleteProducto(int id);
}
