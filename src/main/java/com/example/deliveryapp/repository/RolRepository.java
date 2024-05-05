package com.example.DeliveryApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.DeliveryApp.model.Rol;

public interface RolRepository extends JpaRepository<Rol, Long> {
}