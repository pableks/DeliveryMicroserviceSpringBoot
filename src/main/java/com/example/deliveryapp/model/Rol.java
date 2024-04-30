package com.example.deliveryapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rol")
public class Rol {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "nombre")
    private String nombre;


    public Rol() {
    }

    // Constructor con parámetros
    public Rol(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // Getters y setters

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}