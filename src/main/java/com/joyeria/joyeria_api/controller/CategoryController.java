package com.joyeria.joyeria_api.controller;

import com.joyeria.joyeria_api.model.Category;
import com.joyeria.joyeria_api.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

// @RestController combina @Controller + @ResponseBody
// todas las respuestas se convierten automáticamente a JSON
@RestController
@RequestMapping("/api/categories")
// @CrossOrigin permite peticiones desde otros dominios (frontend en localhost:3000)
@CrossOrigin(origins = "${cors.allowed.origins}")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/active")
    public ResponseEntity<List<Category>> getActiveCategories() {
        List<Category> categories = categoryService.getAllActiveCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        Category created = categoryService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody Category category
    ) {
        Category updated = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Category> partialUpdateCategory(
            @PathVariable Long id,
            @RequestBody Category category
    ) {
        Category updated = categoryService.partialUpdateCategory(id, category);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        // 204 NO CONTENT indica que se procesó pero no hay contenido en la respuesta
        return ResponseEntity.noContent().build();
    }
}