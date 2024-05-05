package com.example.DeliveryApp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.DeliveryApp.model.Rol;
import com.example.DeliveryApp.model.Usuario;
import com.example.DeliveryApp.repository.UsuariosRepository;
import com.example.DeliveryApp.repository.RolRepository;
import com.example.DeliveryApp.service.UsuariosServicelmpl;

@ExtendWith(MockitoExtension.class)
public class UsuariosServiceTest {
    @InjectMocks
    private UsuariosServicelmpl usuariosService;

    @Mock
    private UsuariosRepository usuariosRepositoryMock;

    @Mock
    private RolRepository rolRepository;

    @Test
    public void crearUsuarioTest() {
        Usuario usuario = new Usuario();
        usuario.setUsername("usuario_prueba");
        usuario.setPassword("password_prueba");

        when(usuariosRepositoryMock.save(any())).thenReturn(usuario);

        Usuario usuarioCreado = usuariosService.createUsuario(usuario);

        assertEquals("usuario_prueba", usuarioCreado.getUsername());
        assertEquals("password_prueba", usuarioCreado.getPassword());
    }

    @Test
    public void buscarUsuarioPorUsernameTest() {
        Usuario usuario = new Usuario();
        usuario.setUsername("usuario_prueba");
        usuario.setPassword("password_prueba");

        when(usuariosRepositoryMock.findByUsername("usuario_prueba")).thenReturn(usuario);

        Usuario usuarioEncontrado = usuariosService.findByUsername("usuario_prueba");

        assertEquals("usuario_prueba", usuarioEncontrado.getUsername());
        assertEquals("password_prueba", usuarioEncontrado.getPassword());
    }

    @Test
    public void actualizarUsuarioTest() {
        Long userId = 1L;
        Usuario existingUser = new Usuario();
        existingUser.setId(userId);
        existingUser.setUsername("existing_user");
        existingUser.setPassword("old_password");

        Usuario updatedUser = new Usuario();
        updatedUser.setUsername("updated_user");
        updatedUser.setPassword("new_password");

        when(usuariosRepositoryMock.existsById(userId)).thenReturn(true);
        when(usuariosRepositoryMock.save(any(Usuario.class))).thenReturn(updatedUser);

        Usuario result = usuariosService.updateUsuario(userId, updatedUser);

        assertEquals("updated_user", result.getUsername());
        assertEquals("new_password", result.getPassword());
        verify(usuariosRepositoryMock).save(updatedUser);
    }

    @Test
    public void anadirRolAUsuarioTest() {
        Long userId = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(userId);
        usuario.setUsername("test_user");
        usuario.setPassword("test_password");
        usuario.setRoles(new ArrayList<>()); // Start with no roles

        Rol newRole = new Rol();
        newRole.setId(2L);
        newRole.setRol("Admin");
        newRole.setActivo(true);

        // Mock to find the user
        when(usuariosRepositoryMock.findById(userId)).thenReturn(Optional.of(usuario));

        // Make sure to mock the save method properly to return the modified user
        when(usuariosRepositoryMock.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario updatedUser = usuariosService.addRoleToUser(userId, newRole);

        assertNotNull(updatedUser, "The returned user should not be null");
        assertEquals(1, updatedUser.getRoles().size(), "The role list size should be 1");
        assertTrue(updatedUser.getRoles().contains(newRole), "The new role should be in the user's role list");
    }

}
