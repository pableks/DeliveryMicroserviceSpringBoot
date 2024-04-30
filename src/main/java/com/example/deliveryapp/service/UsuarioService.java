package com.example.deliveryapp.service;

import com.example.deliveryapp.model.Usuario;
import java.util.List;
import java.util.Optional;


public interface UsuarioService {
    Usuario addUsuario(Usuario usuario);
    Usuario updateUsuario(Long id, Usuario usuarioDetails);
    Usuario autenticarUsuario(String email, String password);
    void deleteUsuario(int id);
    List<Usuario> getAllUsuarios();
    Optional<Usuario> getUsuarioById(Long id);
    Object findByUsuarionombre(String nombre);
    Object findByEmail(String email);
}   
