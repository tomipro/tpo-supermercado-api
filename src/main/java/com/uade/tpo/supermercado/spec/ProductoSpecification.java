package com.uade.tpo.supermercado.spec;

import com.uade.tpo.supermercado.entity.Producto;
import com.uade.tpo.supermercado.entity.Categoria;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;

public class ProductoSpecification {
    public static Specification<Producto> nombreContains(String nombre) {
        return (root, query, cb) -> nombre == null ? null : cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%");
    }

    public static Specification<Producto> marcaEquals(String marca) {
        return (root, query, cb) -> marca == null ? null : cb.equal(cb.lower(root.get("marca")), marca.toLowerCase());
    }

    public static Specification<Producto> categoriaIdEquals(Integer categoriaId) {
        return (root, query, cb) -> categoriaId == null ? null : cb.equal(root.get("categoria").get("id"), categoriaId);
    }

    public static Specification<Producto> precioGreaterThanOrEqual(BigDecimal precioMin) {
        return (root, query, cb) -> precioMin == null ? null : cb.greaterThanOrEqualTo(root.get("precio"), precioMin);
    }

    public static Specification<Producto> precioLessThanOrEqual(BigDecimal precioMax) {
        return (root, query, cb) -> precioMax == null ? null : cb.lessThanOrEqualTo(root.get("precio"), precioMax);
    }
}
