package com.joyeria.joyeria_api.service;

import com.joyeria.joyeria_api.model.Material;
import com.joyeria.joyeria_api.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;

    public Material createMaterial(Material material) {
        if (materialRepository.existsByName(material.getName())) {
            throw new RuntimeException("Ya existe un material con ese nombre");
        }
        return materialRepository.save(material);
    }

    @Transactional(readOnly = true)
    public List<Material> getAllActiveMaterials() {
        return materialRepository.findByActiveTrueOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public List<Material> getAllMaterials() {
        return materialRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Material getMaterialById(Long id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material no encontrado con ID: " + id));
    }

    public Material updateMaterial(Long id, Material materialDetails) {
        Material material = getMaterialById(id);

        material.setName(materialDetails.getName());
        material.setDescription(materialDetails.getDescription());
        material.setActive(materialDetails.getActive());

        return materialRepository.save(material);
    }

    public Material partialUpdateMaterial(Long id, Material materialDetails) {
        Material material = getMaterialById(id);

        if (materialDetails.getName() != null) {
            material.setName(materialDetails.getName());
        }
        if (materialDetails.getDescription() != null) {
            material.setDescription(materialDetails.getDescription());
        }
        if (materialDetails.getActive() != null) {
            material.setActive(materialDetails.getActive());
        }

        return materialRepository.save(material);
    }

    public void deleteMaterial(Long id) {
        Material material = getMaterialById(id);
        material.setActive(false);
        materialRepository.save(material);
    }
}