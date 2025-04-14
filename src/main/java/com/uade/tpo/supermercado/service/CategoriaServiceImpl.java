package com.uade.tpo.supermercado.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import com.uade.tpo.supermercado.entity.Categoria;
import com.uade.tpo.supermercado.entity.dto.CategoryRequest;
import com.uade.tpo.supermercado.excepciones.CategoriaNoEncontrada;
import com.uade.tpo.supermercado.excepciones.DatoDuplicadoException;
import com.uade.tpo.supermercado.excepciones.NoEncontradoException;
import com.uade.tpo.supermercado.excepciones.ParametroFueraDeRangoException;
import com.uade.tpo.supermercado.repository.CategoryRepository;

@Service
public class CategoriaServiceImpl implements CategoriaService {
    @Autowired
    private CategoryRepository categoriaRepository;

    @Override
    public Categoria createCategory(CategoryRequest categoryRequest) {
        // Paso 1: Si es subcategoría, validar que el padre exista
        Optional<Categoria> parentCategory = validateParentCategoryExists(categoryRequest.getParentId());

        // Paso 2: Validar que no exista una categoría con el mismo nombre y mismo padre
        validateCategoryDuplicate(categoryRequest.getNombre(), categoryRequest.getParentId());

        // Paso 3: Crear la nueva categoría
        Categoria nuevaCategoria = new Categoria();
        nuevaCategoria.setNombre(categoryRequest.getNombre());

        // Paso 4: Asignar padre si corresponde
        parentCategory.ifPresent(nuevaCategoria::setParentCategoria);

        // Paso 5: Guardar la nueva categoría
        Categoria savedCategory = categoriaRepository.save(nuevaCategoria);

        // Paso 6: Si tiene un padre, agregar la subcategoría a la lista de
        // subcategorías del padre
        if (parentCategory.isPresent()) {
            Categoria parent = parentCategory.get();
            parent.getSubcategorias().add(savedCategory);
            categoriaRepository.save(parent); // Persistir el cambio en el padre
        }

        return savedCategory;

    }

    @Override
    public Categoria updateCategory(int id, CategoryRequest categoryRequest) {
        // 1. Verificar que la categoría a actualizar exista
        Optional<Categoria> categoriaExistente = categoriaRepository.findById(id);
        if (!categoriaExistente.isPresent()) {
            throw new NoEncontradoException("La categoría con ID " + id + " no existe.");
        }

        // 2. Evitar que se asigne como su propio padre
        if (categoryRequest.getParentId() != null && categoryRequest.getParentId().equals(id)) {
            throw new ParametroFueraDeRangoException("No se puede asignar la categoría como su propio padre.");
        }

        // 3. Verificar duplicado (otra categoría con el mismo nombre y mismo padre)
        validateCategoryDuplicate(categoryRequest.getNombre(), categoryRequest.getParentId());

        // 4. Validar que el padre exista (si se proporciona)
        Categoria parentCategoria = null;
        if (categoryRequest.getParentId() != null) {
            parentCategoria = validateParentCategoryExists(categoryRequest.getParentId()).orElse(null);
        }

        // 5. Actualizar los datos de la categoría
        Categoria categoria = categoriaExistente.get();
        categoria.setNombre(categoryRequest.getNombre());

        // Actualizar la relación padre-hijo
        if (parentCategoria != null) {
            categoria.setParentCategoria(parentCategoria);
            // Actualizar la relación bidireccional en el padre
            parentCategoria.getSubcategorias().add(categoria);
        } else {
            categoria.setParentCategoria(null); // Si es raíz, no tiene padre
        }

        // Guardar los cambios
        return categoriaRepository.save(categoria);

    }

    @Override
    public Page<Categoria> getCategorias(Pageable pageable) {
        return categoriaRepository.findAll(pageable);

    }

    @Override
    public Optional<Categoria> getCategoriaById(int id) {
        // Validación del ID de la categoría
        if (id < 1) {
            throw new ParametroFueraDeRangoException("El ID de la categoría debe ser mayor o igual a 1.");
        }

        // Obtener la categoría por ID
        Optional<Categoria> categoria = categoriaRepository.findById(id);

        // Si no se encuentra la categoría, lanzamos una excepción
        if (!categoria.isPresent()) {
            throw new CategoriaNoEncontrada("La categoría con ID " + id + " no se encuentra.");
        }

        return categoria;

    }

    @Override
    public void deleteAllCategories() {
        // Eliminar todas las categorías
        categoriaRepository.deleteAll();
    }

    @Override
    public void deleteCategory(int id) {
        // Validación del ID de la categoría
        if (id < 1) {
            throw new ParametroFueraDeRangoException("El ID de la categoría debe ser mayor o igual a 1.");
        }

        // Verificar si la categoría existe antes de eliminarla
        Optional<Categoria> categoria = categoriaRepository.findById(id);

        // Eliminar la categoría
        categoriaRepository.delete(categoria.get());

    }

    @Override
    public void deleteSubcategorias(List<Categoria> subcategorias) {
        for (Categoria subcategoria : subcategorias) {
            categoriaRepository.delete(subcategoria);
        }
    }

    @Override
    public boolean existsByNombreAndPadre(String nombre, Integer parentId) {
        return categoriaRepository.existsByNombreAndParentCategoriaId(nombre, parentId);
    }

    @Override
    public List<Categoria> getSubcategoriasByParentId(int parentId) {
        // Validación del ID de la categoría padre
        if (parentId < 1) {
            throw new ParametroFueraDeRangoException("El ID de la categoría padre debe ser mayor o igual a 1.");
        }

        // Obtener las subcategorías por ID del padre
        List<Categoria> subcategorias = categoriaRepository.findByParentCategoriaId(parentId);

        // Si no se encuentran subcategorías, lanzamos una excepción
        if (subcategorias.isEmpty()) {
            throw new CategoriaNoEncontrada(
                    "No se encontraron subcategorías para la categoría padre con ID " + parentId + ".");
        }

        return subcategorias;

    }

    @Override
    public long countCategorias() {
        return categoriaRepository.count();

    }

    // Método para validar si la categoría padre existe
    private Optional<Categoria> validateParentCategoryExists(Integer parentId) {
        if (parentId != null) {
            Optional<Categoria> parentCategory = categoriaRepository.findById(parentId);
            if (!parentCategory.isPresent()) {
                throw new CategoriaNoEncontrada("La categoría padre con ID " + parentId + " no existe.");
            }
            return parentCategory;
        }
        return Optional.empty();
    }

    // Método para validar si ya existe una categoría con el mismo nombre y padre
    private void validateCategoryDuplicate(String nombre, Integer parentId) {
        if (categoriaRepository.existsByNombreAndParentCategoriaId(nombre, parentId)) {
            throw new DatoDuplicadoException("Ya existe una categoría con el nombre '" + nombre + "' para ese padre.");
        }

    }

}
