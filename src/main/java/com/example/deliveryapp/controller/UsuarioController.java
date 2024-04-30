package com.example.deliveryapp.controller;

import com.example.deliveryapp.model.Usuario;
import com.example.deliveryapp.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
//import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties.Web;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
//import java.util.Optional;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Endpoint para obtener todos los usuarios registrados.
    @GetMapping
    public CollectionModel<EntityModel<Usuario>> getAllUsuarios() {
        List<Usuario> usuarios = usuarioService.getAllUsuarios();
        List<EntityModel<Usuario>> usuariosResources = usuarios.stream()
                .map(usuario -> EntityModel.of(usuario,
                        WebMvcLinkBuilder
                                .linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getUsuarioById(usuario.getId()))
                                .withSelfRel()))
                .collect(Collectors.toList());

        WebMvcLinkBuilder linkTo = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getAllUsuarios());
        CollectionModel<EntityModel<Usuario>> resource = CollectionModel.of(usuariosResources,
                linkTo.withRel("usuarios"));
        return resource;

    }

    /*
     * public List<Usuario> getAllUsuarios() {
     * return usuarioService.getAllUsuarios();
     * }
     * 
     */
    // Endpoint para obtener un usuario específico por ID.
    @GetMapping("/{id}")
    public EntityModel<Usuario> getUsuarioById(@PathVariable @Min(1) Long id) {
        Usuario usuario = usuarioService.getUsuarioById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id : " + id));
        return EntityModel.of(usuario,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getUsuarioById(id)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getAllUsuarios())
                        .withRel("all-usuarios"));
    }

    /*
     * public ResponseEntity<Usuario> getUsuarioById(@PathVariable @Min(1) int id) {
     * Optional<Usuario> usuario = usuarioService.getUsuarioById(id);
     * return usuario.map(ResponseEntity::ok)
     * .orElseGet(() -> ResponseEntity.notFound().build());
     * }
     */
    // Endpoint para agregar un nuevo usuario a la lista.
    @PostMapping
    public EntityModel<Usuario> addUsuario(@Validated @RequestBody Usuario usuario) {
        Usuario savedUsuario = usuarioService.addUsuario(usuario);
        return EntityModel.of(savedUsuario,
                WebMvcLinkBuilder
                        .linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getUsuarioById(savedUsuario.getId()))
                        .withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getAllUsuarios())
                        .withRel("all-usuarios"));
    }

    /*
     * public ResponseEntity<Usuario> addUsuario(@Valid @RequestBody Usuario
     * usuario) {
     * Usuario savedUsuario = usuarioService.addUsuario(usuario);
     * return ResponseEntity.ok(savedUsuario);
     * }
     */

    // Endpoint para actualizar un usuario existente por ID.
    @PutMapping("/{id}")
    public EntityModel<Usuario> updateUsuario(@PathVariable @Min(1) Long id,
            @Valid @RequestBody Usuario usuarioActualizado) {
        Usuario updatedUser = usuarioService.updateUsuario(id, usuarioActualizado);
        return EntityModel.of(updatedUser,
                WebMvcLinkBuilder
                        .linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getUsuarioById(updatedUser.getId()))
                        .withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getAllUsuarios())
                        .withRel("all-usuarios"));
    }

    /*
     * public ResponseEntity<?> updateUsuario(@PathVariable @Min(1) int
     * id, @Valid @RequestBody Usuario usuarioActualizado) {
     * try {
     * Usuario updatedUser = usuarioService.updateUsuario(id, usuarioActualizado);
     * return ResponseEntity.ok(updatedUser);
     * } catch (RuntimeException ex) {
     * return ResponseEntity.badRequest().body("Error al actualizar el usuario: " +
     * ex.getMessage());
     * }
     * }
     */

    // Endpoint para eliminar un usuario existente por ID.
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuario(@PathVariable @Min(1) int id) {
        try {
            usuarioService.deleteUsuario(id);
            return ResponseEntity.ok().body("Usuario eliminado exitosamente.");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body("Error al eliminar el usuario: " + ex.getMessage());
        }
    }

    // Endpoint para login.

}