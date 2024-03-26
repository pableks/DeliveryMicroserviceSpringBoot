package com.example.deliveryapp.controllers;

import com.example.deliveryapp.models.User;
import com.example.deliveryapp.models.Role;
import com.example.deliveryapp.models.Address;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private List<User> users = new ArrayList<>();

    public UserController() {
        Role customerRole = new Role("customer", "Cliente de la aplicación de envíos");
        Role driverRole = new Role("driver", "Conductor para la aplicación de envíos");

        Address address1 = new Address("123 Calle Principal", "Santiago", "Región Metropolitana", "8320000", "Chile");
        Address address2 = new Address("456 Avenida Los Robles", "Viña del Mar", "Valparaíso", "2520000", "Chile");
        Address address3 = new Address("789 Pasaje Los Aromas", "Concepción", "Biobío", "4030000", "Chile");
        Address address4 = new Address("321 Calle Larga", "Antofagasta", "Antofagasta", "1240000", "Chile");
        Address address5 = new Address("159 Avenida Costanera", "Puerto Montt", "Los Lagos", "5480000", "Chile");

        users.add(new User(1, "Juan Pérez", "juan.perez@example.com", "clave123", Arrays.asList(customerRole), Arrays.asList(address1)));
        users.add(new User(2, "María González", "maria.gonzalez@example.com", "clave456", Arrays.asList(driverRole), Arrays.asList(address2)));
        users.add(new User(3, "Carlos Rodríguez", "carlos.rodriguez@example.com", "clave789", Arrays.asList(customerRole, driverRole), Arrays.asList(address3)));
        users.add(new User(4, "Ana Martínez", "ana.martinez@example.com", "clave012", Arrays.asList(customerRole), Arrays.asList(address4)));
        users.add(new User(5, "Javier Hernández", "javier.hernandez@example.com", "clave345", Arrays.asList(driverRole), Arrays.asList(address5)));
    }

    @GetMapping
    public List<User> getUsers() {
        return users;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return users.stream()
                .filter(user -> user.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        users.add(user);
        return user;
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable int id) {
        User userToRemove = getUserById(id);
        if (userToRemove != null) {
            users.remove(userToRemove);
            return "Usuario con ID " + id + " eliminado.";
        } else {
            return "Usuario con ID " + id + " no encontrado.";
        }
    }
}