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
import com.uade.tpo.supermercado.entity.*;
import java.net.URI;
import com.uade.tpo.supermercado.entity.Categoria;
import com.uade.tpo.supermercado.excepciones.CategoriaNoEncontrada;
import com.uade.tpo.supermercado.excepciones.NoCategoriesToDeleteException;

import java.util.Optional;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping ("Categoria")
public class CategoriaController {
    @Autowired
    private CategoriaService categoriaService;

    /**
 * Obtiene categorías paginadas
 * 
 * '@'param page Número de página (0-based). Opcional - si no se especifica, 
 *             retorna todas las categorías
 * '@'param size Tamaño de la página. Opcional - si no se especifica, 
 *             retorna todas las categorías
 * '@'return ResponseEntity con Page<Categoria> (lista paginada)
 * 
 * Ejemplos:
 * • GET /api/categorias → Todas las categorías
 * • GET /api/categorias?page=0&size=10 → Primera página (10 items)
 */


    @GetMapping
    public ResponseEntity<Page<Categoria>> getCategorias(@RequestParam(required = false) Integer page,
    @RequestParam(required = false) Integer size){
    if (page == null || size == null)
        return ResponseEntity.ok(categoriaService.getCategorias(PageRequest.of(0, Integer.MAX_VALUE)));
    return ResponseEntity.ok(categoriaService.getCategorias(PageRequest.of(page, size))
    );
}
/**
 * Obtiene una categoría específica por su ID
 * 
 * '@'param categoriaID El ID numérico de la categoría a buscar (ej: 5)
 * '@'return La categoría encontrada (200 OK) o vacío (204 No Content) si no existe
 * 
 * Ejemplo: GET /api/categorias/5 → Devuelve la categoría con ID=5
 */
    @GetMapping("/{categoriaID}")
    public ResponseEntity<Categoria> getCategoriaById(@PathVariable int categoriaID){
        Optional<Categoria> result = categoriaService.getCategoriaById(categoriaID);
        if (result.isPresent())
            return ResponseEntity.ok(result.get());

        return ResponseEntity.notFound().build();
    }

    
    
    //* Este método maneja la solicitud POST para crear una nueva categoría.
    @PostMapping
    public ResponseEntity<Object> createCategory(@RequestBody com.uade.tpo.supermercado.entity.dto.CategoryRequest categoryRequest)
            throws com.uade.tpo.supermercado.excepciones.CategoryDuplicateException {
        // Verificar si la categoría padre existe, si se proporciona
        if (categoryRequest.getParentId() != null) {
            // Lanzar excepción si la categoría padre no existe
            categoriaService.getCategoriaById(categoryRequest.getParentId())
                .orElseThrow(() -> new CategoriaNoEncontrada("La categoría padre con ID " + categoryRequest.getParentId() + " no existe."));
        }
    
        // Crear la categoría y obtener el resultado
        Categoria result = categoriaService.createCategory(categoryRequest);
    
        return ResponseEntity.created(URI.create("/categories/" + result.getId())).body(result);
    }

    //Elimina una categoría por su ID
    @DeleteMapping("/{categoriaID}")
    public ResponseEntity<Void> deleteCategory(@PathVariable int categoriaID) {
        Optional<Categoria> result = categoriaService.getCategoriaById(categoriaID);
        if (result.isPresent()) {
            categoriaService.deleteCategory(categoriaID);
            return ResponseEntity.noContent().build();
        }
       else {
        throw new CategoriaNoEncontrada("La categoría con ID " + categoriaID + " no se encuentra.");
        }
    }

    //Este método maneja la solicitud DELETE para eliminar todas las categorías.
    @DeleteMapping
    public ResponseEntity<Void> deleteAllCategories() {
        // Si no hay categorías en el sistema, lanzamos la excepción
        if (categoriaService.getCategorias(PageRequest.of(0, Integer.MAX_VALUE)).getTotalElements() == 0) {
            throw new NoCategoriesToDeleteException("No hay categorías para eliminar.");
        }
        else {
            // Si hay categorías, las eliminamos
            categoriaService.deleteAllCategories();
            return ResponseEntity.noContent().build();
        }
        

        
    }

    @PostMapping("/{id}")
    public ResponseEntity<Categoria> updateCategory(@PathVariable int id, @RequestBody com.uade.tpo.supermercado.entity.dto.CategoryRequest categoryRequest) 
            throws CategoriaNoEncontrada {
        // Verificar si la categoría existe
        Categoria existingCategory = categoriaService.getCategoriaById(id)
                .orElseThrow(() -> new CategoriaNoEncontrada("La categoría con ID " + id + " no existe."));
        
        // Actualizar la categoría
        Categoria updatedCategory = categoriaService.updateCategory(id, categoryRequest);
        
        return ResponseEntity.ok(updatedCategory);
    }
    






    







    

    

}
