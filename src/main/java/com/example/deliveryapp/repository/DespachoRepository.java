package com.example.DeliveryApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.DeliveryApp.model.Despacho;

public interface DespachoRepository extends JpaRepository<Despacho, Long> {
}