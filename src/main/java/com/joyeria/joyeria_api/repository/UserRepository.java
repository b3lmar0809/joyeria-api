package com.joyeria.joyeria_api.repository;

import com.joyeria.joyeria_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    // optional significa que puede retornar un user o estar vacio (si no existe)

    Boolean existsByEmail(String email);

    java.util.List<User> findByActiveTrue();

    java.util.List<User> findByRole(com.joyeria.joyeria_api.model.Role role);
}
