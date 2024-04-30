package com.example.deliveryapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.deliveryapp.model.Usuario;
import java.util.Optional;


public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Método para buscar un usuario por email
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByNombre(String nombre);
    Optional<Usuario> findById(Long id);
}