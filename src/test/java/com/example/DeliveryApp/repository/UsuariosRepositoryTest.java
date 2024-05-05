package com.example.DeliveryApp.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.DeliveryApp.model.Usuario;
import com.example.DeliveryApp.repository.UsuariosRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UsuariosRepositoryTest {
    @Autowired
    private UsuariosRepository usuariosRepository;

    @Test
    public void guardarUsuarioTest() {
        Usuario usuario = new Usuario();
        usuario.setUsername("usuario_prueba");
        usuario.setPassword("password_prueba");

        Usuario usuarioGuardado = usuariosRepository.save(usuario);

        assertNotNull(usuarioGuardado.getId());
        assertEquals("usuario_prueba", usuarioGuardado.getUsername());
        assertEquals("password_prueba", usuarioGuardado.getPassword());
    }

    @Test
    public void buscarUsuarioPorUsernameTest() {
        Usuario usuario = new Usuario();
        usuario.setUsername("usuario_prueba");
        usuario.setPassword("password_prueba");

        usuariosRepository.save(usuario);

        Usuario usuarioEncontrado = usuariosRepository.findByUsername("usuario_prueba");

        assertNotNull(usuarioEncontrado);
        assertEquals("usuario_prueba", usuarioEncontrado.getUsername());
        assertEquals("password_prueba", usuarioEncontrado.getPassword());
    }
}
