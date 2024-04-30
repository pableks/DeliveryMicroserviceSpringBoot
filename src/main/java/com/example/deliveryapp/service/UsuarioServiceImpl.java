package com.example.deliveryapp.service;

import com.example.deliveryapp.model.Usuario;
import com.example.deliveryapp.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }
    
    // Implementación de los métodos de la interfaz UsuarioService
    @Override
    public Usuario addUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario autenticarUsuario(String email, String password) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);
        if (usuarioOptional.isPresent() && usuarioOptional.get().getPassword().equals(password)) {
            return usuarioOptional.get();
        }
        return null;
    }

    @Override
    public Usuario updateUsuario(Long id, Usuario usuarioDetails) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            usuario.setNombre(usuarioDetails.getNombre());
            usuario.setEmail(usuarioDetails.getEmail());
            usuario.setPassword(usuarioDetails.getPassword());
            return usuarioRepository.save(usuario);
        } else {
            throw new RuntimeException("Usuario no encontrado con id : " + id);
        }
    }

    @Override
    public void deleteUsuario(int id) {
        usuarioRepository.deleteById((long) id);
    }

    @Override
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
public Optional<Usuario> getUsuarioById(Long id) {
    return usuarioRepository.findById(id);
}

    
    @Override
    public Optional<Usuario> findByUsuarionombre(String nombre) {
        // Implement your logic here
        return usuarioRepository.findByNombre(nombre);
    }
    
    @Override
    public Optional<Usuario> findByEmail(String email) {
        // Implement your logic here
        return usuarioRepository.findByEmail(email);
    }
} 