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
import com.uade.tpo.supermercado.excepciones.CategoriaNoEncontrada;
import com.uade.tpo.supermercado.excepciones.DatoDuplicadoException;
import com.uade.tpo.supermercado.excepciones.NoEncontradoException;
import com.uade.tpo.supermercado.excepciones.ParametroFueraDeRangoException;
import java.util.Optional;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("categorias")
public class CategoriaController {
    @Autowired
    private CategoriaService categoriaService;

    /**
     * Endpoint para obtener una lista paginada de categorías.
     *
     * @param page Número de página (debe ser mayor o igual a 1). Opcional.
     * @param size Cantidad de elementos por página (debe ser mayor o igual a 1).
     *             Opcional.
     * @return Una respuesta HTTP con el contenido de la página solicitada o con
     *         todas las categorías si no se especifica paginación.
     * @throws ParametroFueraDeRangoException si los parámetros de paginación son
     *                                        inválidos.
     * @throws NoEncontradoException          si no existen categorías cargadas en
     *                                        el sistema.
     *
     *                                        Ejemplos de uso:
     *                                        • GET /Categorias → retorna todas las
     *                                        categorías
     *                                        • GET /Categorias?page=1&size=10 →
     *                                        retorna la página 1 con 10 categorías
     *                                        por página
     */
    @GetMapping
    public ResponseEntity<Page<Categoria>> getCategorias(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        // Si no se proporciona paginación, se devuelven todas las categorías
        if (page == null || size == null) {
            Page<Categoria> categorias = categoriaService.getCategorias(PageRequest.of(0, Integer.MAX_VALUE));

            if (categorias.getTotalElements() == 0) {
                throw new NoEncontradoException("No hay categorías cargadas.");
            }

            return ResponseEntity.ok(categorias);
        }

        // Validación de parámetros de entrada
        if (page < 1 || size < 1) {
            throw new ParametroFueraDeRangoException("Los parámetros 'page' y 'size' deben ser mayores o iguales a 1.");
        }

        // Obtención de categorías paginadas
        Page<Categoria> categorias = categoriaService.getCategorias(PageRequest.of(page, size));

        if (categorias.getTotalElements() == 0) {
            throw new NoEncontradoException("No hay categorías cargadas.");
        }

        return ResponseEntity.ok(categorias);
    }

    /**
     * Obtiene una categoría específica a partir de su identificador único.
     *
     * @param categoriaID El ID numérico de la categoría a buscar (por ejemplo: 5).
     * @return ResponseEntity con la categoría encontrada (código 200 OK) o lanza
     *         una excepción si no existe.
     * @throws NoEncontradoException si no se encuentra ninguna categoría con el ID
     *                               proporcionado.
     *
     *                               Ejemplo de uso:
     *                               • GET /Categoria/5 → retorna la categoría con
     *                               ID 5, si existe.
     */
    @GetMapping("/{categoriaID}")
    public ResponseEntity<Categoria> getCategoriaById(@PathVariable int categoriaID) {
        Optional<Categoria> result = categoriaService.getCategoriaById(categoriaID);

        // Validación del ID de la categoría
        if (categoriaID < 1) {
            throw new ParametroFueraDeRangoException("El ID de la categoría debe ser mayor o igual a 1.");
        }
        // Si no se encuentra la categoría, lanzamos una excepción

        if (!result.isPresent()) {
            throw new NoEncontradoException("La categoría con ID " + categoriaID + " no se encuentra.");
        }

        return ResponseEntity.ok(result.get());
    }

    /**
     * Crea una nueva categoría.
     * 
     * '@'param categoryRequest Objeto con los datos de la nueva categoría (nombre y
     * ID del padre opcional)
     * '@'return La categoría creada (201 Created) o error si hay datos inválidos
     * 
     * Validaciones:
     * • El nombre no puede estar vacío
     * • Si se proporciona un ID de padre, debe existir
     * • No puede haber otra categoría con el mismo nombre y padre
     * 
     * Ejemplo: POST /api/categorias
     */
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

        // Verificar si la categoría padre existe, si se proporciona
        if (categoryRequest.getParentId() != null) {
            Optional<Categoria> parentCategory = categoriaService.getCategoriaById(categoryRequest.getParentId());
            if (!parentCategory.isPresent()) {
                throw new CategoriaNoEncontrada(
                        "La categoría padre con ID " + categoryRequest.getParentId() + " no existe.");
            }
        }

        // Verificar si ya existe una categoría con ese nombre y el mismo padre
        if (categoriaService.existsByNombreAndPadre(categoryRequest.getNombre(), categoryRequest.getParentId())) {
            throw new DatoDuplicadoException(
                    "Ya existe una categoría con el nombre '" + categoryRequest.getNombre() + "' para ese padre.");
        }

        // Crear la nueva categoría
        Categoria newCategory = categoriaService.createCategory(categoryRequest);

        return ResponseEntity.created(URI.create("/categories/" + newCategory.getId())).body(newCategory);
    }

    // Este método maneja la solicitud DELETE para eliminar todas las categorías.
    @DeleteMapping
    public ResponseEntity<Void> deleteAllCategories() {
        // Comprobar si existen categorías
        if (categoriaService.countCategorias() == 0) {
            throw new NoEncontradoException("No hay categorías para eliminar.");
        }

        // Si hay categorías, las eliminamos
        categoriaService.deleteAllCategories();

        // Devolver una respuesta sin contenido, indicando que la operación fue exitosa
        return ResponseEntity.noContent().build();
    }

    // Este método maneja la solicitud DELETE para eliminar una categoría por su ID,
    // incluyendo la eliminación de todas sus subcategorías si es una categoría
    // padre.
    @DeleteMapping("/{categoriaID}")
    public ResponseEntity<Void> deleteCategoryById(@PathVariable int categoriaID) {
        // Verificar si la categoría con el ID proporcionado existe
        Optional<Categoria> categoria = categoriaService.getCategoriaById(categoriaID);
        if (!categoria.isPresent()) {
            throw new NoEncontradoException("La categoría con ID " + categoriaID + " no se encuentra.");
        }

        // Verificar si la categoría tiene subcategorías
        List<Categoria> subcategorias = categoriaService.getSubcategoriasByParentId(categoriaID);
        if (!subcategorias.isEmpty()) {
            // Si tiene subcategorías, eliminarlas primero
            categoriaService.deleteSubcategorias(subcategorias);
        }

        // Eliminar la categoría principal
        categoriaService.deleteCategory(categoriaID);

        // Devolver una respuesta indicando que la operación fue exitosa (204 No
        // Content)
        return ResponseEntity.noContent().build();
    }

    // ACTUALIZAR CATEGORIA

    // Este método maneja la solicitud PUT para actualizar una categoría por su ID.
    @PutMapping("/{categoriaID}")
    public ResponseEntity<Categoria> updateCategory(@PathVariable int categoriaID,
            @RequestBody com.uade.tpo.supermercado.entity.dto.CategoryRequest categoryRequest) {
        // Verificar si la categoría con el ID proporcionado existe
        Categoria updatedCategory = categoriaService.updateCategory(categoriaID, categoryRequest);

        // Devolver la categoría actualizada con un código de estado 200 OK
        return ResponseEntity.ok(updatedCategory);

    }
}
