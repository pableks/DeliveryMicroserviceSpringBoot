package com.example.DeliveryApp.service;

import java.util.List;
import java.util.Optional;

import com.example.DeliveryApp.model.Despacho;
import com.example.DeliveryApp.model.Rol;
import com.example.DeliveryApp.model.Usuario;

public interface UsuariosService {
    List<Usuario> getAllUsuarios();
    Optional<Usuario> getUsuarioById(Long id);
    Usuario createUsuario(Usuario usuario);
    Usuario updateUsuario(Long id, Usuario usuario);
    void deleteUsuario(Long id);
    void removeRoleFromUser(Long userId, Long roleId);
    void removeDespachoFromUser(Long userId, Long despachoId);
    Usuario findByUsername(String username);
    Usuario addRoleToUser(Long userId, Rol role) throws Exception;
    Usuario addDespachoToUser(Long userId, Despacho despacho) throws Exception;
    
}
