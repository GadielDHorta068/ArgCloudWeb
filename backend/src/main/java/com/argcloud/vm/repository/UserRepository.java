package com.argcloud.vm.repository;

import com.argcloud.vm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad User.
 * Proporciona métodos para interactuar con la base de datos para los usuarios.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su dirección de correo electrónico.
     * @param email la dirección de correo electrónico a buscar.
     * @return un Optional que contiene al usuario si se encuentra, o vacío si no.
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Comprueba si un usuario existe por su dirección de correo electrónico.
     * @param email la dirección de correo electrónico a comprobar.
     * @return true si el usuario existe, false en caso contrario.
     */
    Boolean existsByEmail(String email);
    
    /**
     * Busca un usuario por su token de verificación de correo electrónico.
     * @param token el token de verificación a buscar.
     * @return un Optional que contiene al usuario si se encuentra, o vacío si no.
     */
    Optional<User> findByEmailVerificationToken(String token);
} 