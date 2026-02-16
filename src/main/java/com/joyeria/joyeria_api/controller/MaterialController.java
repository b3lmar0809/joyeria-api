package com.joyeria.joyeria_api.controller;

import com.joyeria.joyeria_api.model.Material;
import com.joyeria.joyeria_api.service.MaterialService;
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

@RestController
@RequestMapping("/api/materials")
@CrossOrigin(origins = "${cors.allowed.origins}")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;

    @GetMapping("/active")
    public ResponseEntity<List<Material>> getActiveMaterials() {
        List<Material> materials = materialService.getAllActiveMaterials();
        return ResponseEntity.ok(materials);
    }

    @GetMapping
    public ResponseEntity<List<Material>> getAllMaterials() {
        List<Material> materials = materialService.getAllMaterials();
        return ResponseEntity.ok(materials);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Material> getMaterialById(@PathVariable Long id) {
        Material material = materialService.getMaterialById(id);
        return ResponseEntity.ok(material);
    }

    @PostMapping
    public ResponseEntity<Material> createMaterial(@Valid @RequestBody Material material) {
        Material created = materialService.createMaterial(material);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Material> updateMaterial(
            @PathVariable Long id,
            @Valid @RequestBody Material material
    ) {
        Material updated = materialService.updateMaterial(id, material);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Material> partialUpdateMaterial(
            @PathVariable Long id,
            @RequestBody Material material
    ) {
        Material updated = materialService.partialUpdateMaterial(id, material);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaterial(@PathVariable Long id) {
        materialService.deleteMaterial(id);
        return ResponseEntity.noContent().build();
    }
}