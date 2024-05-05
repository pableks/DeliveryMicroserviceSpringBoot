package com.example.DeliveryApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.DeliveryApp.model.Despacho;
import com.example.DeliveryApp.model.Rol;
import com.example.DeliveryApp.model.Usuario;
import com.example.DeliveryApp.repository.DespachoRepository;
import com.example.DeliveryApp.repository.UsuariosRepository;
import com.example.DeliveryApp.repository.RolRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UsuariosServicelmpl implements UsuariosService {

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private DespachoRepository despachoRepository;

    @Override
    public List<Usuario> getAllUsuarios() {
        return usuariosRepository.findAll();
    }

    @Override
    public Optional<Usuario> getUsuarioById(Long id) {
        return usuariosRepository.findById(id);
    }

    @Override
    public Usuario createUsuario(Usuario usuario) {
        return usuariosRepository.save(usuario);
    }

    @Override
    public Usuario updateUsuario(Long id, Usuario usuario) {
        if (usuariosRepository.existsById(id)) {
            usuario.setId(id);
            return usuariosRepository.save(usuario);
        } else {
            return null;
        }
    }

    @Override
    public void deleteUsuario(Long id) {
        usuariosRepository.deleteById(id);
    }

    @Override
    public Usuario findByUsername(String username) {
        return usuariosRepository.findByUsername(username);
    }

    @Override
    public void removeRoleFromUser(Long userId, Long roleId) {
        Optional<Usuario> user = usuariosRepository.findById(userId);
        if (user.isPresent()) {
            Usuario usuario = user.get();
            usuario.getRoles().removeIf(rol -> rol.getId().equals(roleId));
            usuariosRepository.save(usuario); // Save the user after removing the role
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Override
    public void removeDespachoFromUser(Long userId, Long despachoId) {
        Optional<Usuario> user = usuariosRepository.findById(userId);
        if (user.isPresent()) {
            Usuario usuario = user.get();
            usuario.getDespachos().removeIf(despacho -> despacho.getId().equals(despachoId));
            usuariosRepository.save(usuario); // Save the user after removing the despacho
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Override
    public Usuario addRoleToUser(Long userId, Rol role) {
        Usuario usuario = usuariosRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        usuario.getRoles().add(role); // Add the new role to the user's collection of roles
        role.setUsuario(usuario); // Set the user as the owner of the role
        rolRepository.save(role); // Save the new role
        return usuariosRepository.save(usuario); // Save the user with the new role
    }

    @Override
    public Usuario addDespachoToUser(Long userId, Despacho despacho) {
        Usuario usuario = usuariosRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        usuario.getDespachos().add(despacho); // Add the new despacho to the user's collection of despachos
        despacho.setUsuario(usuario); // Set the user as the owner of the despacho
        despachoRepository.save(despacho); // Save the new despacho
        return usuariosRepository.save(usuario); // Save the user with the new despacho
    }

}
