package com.uade.tpo.supermercado.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.uade.tpo.supermercado.service.CategoriaService;
import java.net.URI;
import com.uade.tpo.supermercado.entity.Categoria;
import com.uade.tpo.supermercado.excepciones.NoEncontradoException;
import com.uade.tpo.supermercado.excepciones.ParametroFueraDeRangoException;
import com.uade.tpo.supermercado.entity.dto.categoriaResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("categorias")
public class CategoriaController {
    @Autowired
    private CategoriaService categoriaService;

    // Este método maneja la solicitud GET para obtener una lista de categorías.
    // Ejemplos de uso:
    // • GET /Categorias → retorna todas las
    // • GET /Categorias?page=1&size=10 → retorna la página 1 con 10 categorías por
    // pagina

    @GetMapping
    public ResponseEntity<Page<categoriaResponse>> getCategorias(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        // Si no se proporciona paginación, se devuelven todas las categorías
        if (page == null || size == null) {
            Page<Categoria> allCategorias = categoriaService.getCategorias(PageRequest.of(0, Integer.MAX_VALUE));

            if (allCategorias.getTotalElements() == 0) {
                throw new NoEncontradoException("No hay categorías cargadas.");
            }
            Page<categoriaResponse> categoriasResponse = allCategorias.map(this::convertToCategoriaResponse);
            return ResponseEntity.ok(categoriasResponse);
        }

        // Validación de parámetros de entrada
        if (page < 0 || size < 1) {
            throw new ParametroFueraDeRangoException("Los parámetros 'page' y 'size' deben ser mayores o iguales a 1.");
        }

        // Obtención de categorías paginadas
        Page<Categoria> categorias = categoriaService.getCategorias(PageRequest.of(page, size));

        if (categorias.getTotalElements() == 0) {
            throw new NoEncontradoException("No hay categorías cargadas.");
        }
        // Convertir las categorías a la respuesta DTO
        Page<categoriaResponse> categoriasResponse = categorias.map(this::convertToCategoriaResponse);

        return ResponseEntity.ok(categoriasResponse);
    }

    // Este método maneja la solicitud GET para obtener una categoría por su ID.
    // Ejemplo de uso:
    // • GET /Categorias/5 → retorna la categoría con ID 5, si existe.
    @GetMapping("/{categoriaID}")
    public ResponseEntity<categoriaResponse> getCategoriaById(@PathVariable int categoriaID) {
        Optional<Categoria> result = categoriaService.getCategoriaById(categoriaID);

        // Verificar si la categoría existe
        if (!result.isPresent()) {
            throw new NoEncontradoException("La categoría con ID " + categoriaID + " no se encuentra.");
        }

        // convertir a CategoriaResponse
        categoriaResponse categoriaResponse = convertToCategoriaResponse(result.get());

        return ResponseEntity.ok(categoriaResponse);
    }

    // Este método maneja la solicitud POST para crear una nueva categoría.
    // Ejemplo de uso:
    // • POST /Categorias → crea una nueva categoría con los datos proporcionados en

    @PostMapping
    public ResponseEntity<Object> createCategory(
            @RequestBody com.uade.tpo.supermercado.entity.dto.CategoryRequest categoryRequest) {

        // Validar nombre de la categoría
        if (categoryRequest.getNombre() == null || categoryRequest.getNombre().trim().isEmpty()) {
            throw new ParametroFueraDeRangoException("El nombre de la categoría no puede estar vacío.");
        }

        // validar que el si el parent id no es null entonces tiene que ser mayor o
        // igual 1
        if (categoryRequest.getParentId() != null && categoryRequest.getParentId() < 1) {
            throw new ParametroFueraDeRangoException("El ID de la categoría padre debe ser mayor o igual a 1.");
        }

        // Crear la nueva categoría
        Categoria newCategory = categoriaService.createCategory(categoryRequest);

        return ResponseEntity.created(URI.create("/categories/" + newCategory.getId())).body(newCategory);
    }

    // Este método maneja la solicitud DELETE para eliminar todas las categorías.
    // Ejemplo de uso:
    // • DELETE /Categorias → elimina todas las categorías.

    @DeleteMapping
    public ResponseEntity<String> deleteAllCategories() {
        // Comprobar si existen categorías
        if (categoriaService.countCategorias() == 0) {
            throw new NoEncontradoException("No hay categorías para eliminar.");
        }

        // Si hay categorías, las eliminamos
        categoriaService.deleteAllCategories();

        // operación fue exitosa!
        return ResponseEntity.ok("Todas las categorías fueron eliminadas correctamente.");

    }

    // Este método maneja la solicitud DELETE para eliminar una categoría por su ID.
    // Ejemplo de uso:
    // • DELETE /Categorias/5 → elimina la categoría con ID 5, si existe.
    // Si la categoría tiene hijos, se eliminarán automáticamente

    @DeleteMapping("/{categoriaID}")
    public ResponseEntity<String> deleteCategoryById(@PathVariable int categoriaID) {
        Optional<Categoria> categoria = categoriaService.getCategoriaById(categoriaID);
        if (!categoria.isPresent()) {
            throw new NoEncontradoException("La categoría con ID " + categoriaID + " no se encuentra.");
        }

        // Borrá directamente y dejá que cascade se encargue
        categoriaService.deleteCategory(categoriaID);

        return ResponseEntity.ok("La categoría con ID " + categoriaID + " fue eliminada exitosamente.");

    }

    // Este método maneja la solicitud PUT para actualizar una categoría por su ID.
    // Ejemplo de uso:
    // • PUT /Categorias/5 → actualiza la categoría con ID 5, si existe.
    // Si la categoría tiene hijos, se eliminarán automáticamente
    @PutMapping("/{categoriaID}")
    public ResponseEntity<Categoria> updateCategory(@PathVariable int categoriaID,
            @RequestBody com.uade.tpo.supermercado.entity.dto.CategoryRequest categoryRequest) {

        Categoria updatedCategory = categoriaService.updateCategory(categoriaID, categoryRequest);

        // Devolver la categoría actualizada con un código de estado 200 OK
        return ResponseEntity.ok(updatedCategory);

    }

    // convertir Categoria a CategoriaResponse
    public categoriaResponse convertToCategoriaResponse(Categoria categoria) {
        if (categoria == null) {
            return null;
        }

        List<categoriaResponse> subcategoriasResponse = categoria.getSubcategorias().stream()
                .map(this::convertToCategoriaResponse)
                .toList();

        return new categoriaResponse(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getParentCategoria() != null ? categoria.getParentCategoria().getId() : null,
                categoria.getParentCategoria() != null ? categoria.getParentCategoria().getNombre() : null,
                subcategoriasResponse);
    }
}