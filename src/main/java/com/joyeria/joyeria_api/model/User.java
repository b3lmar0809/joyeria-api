package com.joyeria.joyeria_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// UserDetails es una interfaz de Spring Security que permite usar esta clase para autenticacion
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String phone;


    // @Enumerated le dice a JPA como guardar el enum en la base de datos
    // EnumType.STRING guarda el nombre ("CUSTOMER" o "ADMIN") en vez del número
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.CUSTOMER;

    @Column(nullable = false)
    private Boolean active = true;

    // updatable = false significa que una vez creado, este valor NO se puede modificar
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();// fecha de registr

    // metodos requeridos por UserDetails (Spring Security)

    // getAuthorities() devuelve los roles/permisos del usuario
    // Spring Security usa esto para saber que puede hacer el usuario
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        // se convirtio el rol a un formato que Spring Security entiende
        // "ROLE_CUSTOMER" o "ROLE_ADMIN"
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    // getUsername() devuelve el identificador unico del usuario
    // En este caso, se va a usar el email como username
    @Override
    public String getUsername() {
        return email;
    }

    // indica si la cuenta ha expirado (nosotros siempre retornamos true = no expira)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // indica si la cuenta está bloqueada (usamos el campo "active")
    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    // indica si las credenciales (contraseña) han expirado
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Indica si el usuario está habilitado para iniciar sesión
    @Override
    public boolean isEnabled() {
        return active;
    }
}
