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
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

        @Query(value = "select p from Producto p where p.id = ?1")
        Optional<Producto> findById(int id);

        @Query("select p from Producto p where p.nombre = ?1")
        Optional<Producto> findByNombre(String nombre);

        @Query(value = "select p from Producto p where p.marca = ?1")
        Optional<Producto> findByMarca(String marca);

        @Query(value = "select p from Producto p where p.precio <= ?1")
        Optional<Producto> findByPrecioMaximo(BigDecimal precio);

        @Query(value = "select p from Producto p where p.precio >= ?1")
        Optional<Producto> findByPrecioMinimo(BigDecimal precio);

        @Query("select p from Producto p where p.precio >= ?2 and p.precio <= ?1")
        Optional<Producto> findByPrecio(BigDecimal precioMax, BigDecimal precioMin);

        @Query(value = "select p from Producto p where p.categoria = ?1")
        Optional<Producto> findByCategoria(int categoria_id);

        @Query(value = "Select p from Producto p")
        Optional<Producto> findAllProductos();

        @Query(value = "insert into Producto (nombreProducto, descripcion, marca, precio, categoria) values (?1, ?2, ?3, ?4, ?5)", nativeQuery = true)
        Producto createProducto(String nombreProducto, String descripcion, String marca, BigDecimal precio,
                        int categoria_id);

        @Query(value = "Delete from Producto where id = ?1", nativeQuery = true)
        void deleteProducto(int id);

        @Query(value = "update Producto set nombreProducto = ?1, descripcion = ?2, marca = ?3, precio = ?4, categoria_id = ?5 where id = ?6", nativeQuery = true)
        Producto updateProducto(String nombreProducto, String descripcion,
                        String marca, BigDecimal precio, int categoria_id, int id);

}
