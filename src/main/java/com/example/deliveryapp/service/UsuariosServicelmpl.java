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
    private UsuariosRepository regUsuRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private DespachoRepository despachoRepository;

    @Override
    public List<Usuario> getAllUsuarios() {
        return regUsuRepository.findAll();
    }

    @Override
    public Optional<Usuario> getUsuarioById(Long id) {
        return regUsuRepository.findById(id);
    }

    @Override
    public Usuario createUsuario(Usuario usuario) {
        return regUsuRepository.save(usuario);
    }

    @Override
    public Usuario updateUsuario(Long id, Usuario usuario) {
        if (regUsuRepository.existsById(id)) {
            usuario.setId(id);
            return regUsuRepository.save(usuario);
        } else {
            return null;
        }
    }

    @Override
    public void deleteUsuario(Long id) {
        regUsuRepository.deleteById(id);
    }

    @Override
    public Usuario findByUsername(String username) {
        return regUsuRepository.findByUsername(username);
    }

    @Override
    public void removeRoleFromUser(Long userId, Long roleId) {
        Optional<Usuario> user = regUsuRepository.findById(userId);
        if (user.isPresent()) {
            Usuario usuario = user.get();
            usuario.getRoles().removeIf(rol -> rol.getId().equals(roleId));
            regUsuRepository.save(usuario); // Save the user after removing the role
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Override
    public void removeDespachoFromUser(Long userId, Long despachoId) {
        Optional<Usuario> user = regUsuRepository.findById(userId);
        if (user.isPresent()) {
            Usuario usuario = user.get();
            usuario.getDespachos().removeIf(despacho -> despacho.getId().equals(despachoId));
            regUsuRepository.save(usuario); // Save the user after removing the despacho
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Override
    public Usuario addRoleToUser(Long userId, Rol role) {
        Usuario usuario = regUsuRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        usuario.getRoles().add(role); // Add the new role to the user's collection of roles
        role.setUsuario(usuario); // Set the user as the owner of the role
        rolRepository.save(role); // Save the new role
        return regUsuRepository.save(usuario); // Save the user with the new role
    }

    @Override
    public Usuario addDespachoToUser(Long userId, Despacho despacho) {
        Usuario usuario = regUsuRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        usuario.getDespachos().add(despacho); // Add the new despacho to the user's collection of despachos
        despacho.setUsuario(usuario); // Set the user as the owner of the despacho
        despachoRepository.save(despacho); // Save the new despacho
        return regUsuRepository.save(usuario); // Save the user with the new despacho
    }

}
