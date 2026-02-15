package com.joyeria.joyeria_api.repository;

import com.joyeria.joyeria_api.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

    Optional<Material> findByName(String name);

    Optional<Material> findByNameIgnoreCase(String name);

    Boolean existsByNameIgnoreCase(String name);

    List<Material> findByActiveTrue();

    List<Material> findByActiveTrueOrderByNameAsc();
}
