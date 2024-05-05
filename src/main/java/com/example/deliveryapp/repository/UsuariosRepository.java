package com.example.DeliveryApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.DeliveryApp.model.Usuario;

public interface UsuariosRepository extends JpaRepository<Usuario, Long>{
    Usuario findByUsername(String username);
}
