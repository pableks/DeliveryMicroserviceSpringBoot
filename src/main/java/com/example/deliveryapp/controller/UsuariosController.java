package com.example.DeliveryApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import com.example.DeliveryApp.model.Despacho;
import com.example.DeliveryApp.model.Rol;
import com.example.DeliveryApp.model.Usuario;
import com.example.DeliveryApp.service.UsuariosService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/usuarios")
public class UsuariosController {

    private static final Logger log = LoggerFactory.getLogger(UsuariosController.class);

    @Autowired
    private UsuariosService regUsuService;

    @GetMapping
    public CollectionModel<EntityModel<Usuario>> getAllUsuarios() {
        List<Usuario> usuarios = regUsuService.getAllUsuarios();
        log.info("GET /usuarios");
        log.info("Retornando todos los usuarios");
        List<EntityModel<Usuario>> usuariosResources = usuarios.stream()
                .map(usuario -> EntityModel.of(usuario,
                        WebMvcLinkBuilder
                                .linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getUsuarioById(usuario.getId()))
                                .withSelfRel()))
                .collect(Collectors.toList());

        WebMvcLinkBuilder linkTo = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getAllUsuarios());
        CollectionModel<EntityModel<Usuario>> resources = CollectionModel.of(usuariosResources,
                linkTo.withRel("usuarios"));

        return resources;
    }

    @GetMapping("/{id}")
    public EntityModel<Usuario> getUsuarioById(@Validated @PathVariable Long id) {
        Optional<Usuario> usuario = regUsuService.getUsuarioById(id);

        if (usuario.isPresent()) {
            return EntityModel.of(usuario.get(),
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getUsuarioById(id))
                            .withSelfRel(),
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getAllUsuarios())
                            .withRel("all-usuarios"));
        } else {
            throw new RegUsuNotFoundException("No se encontró el usuario con ID " + id);
        }
    }

    @PostMapping
    public EntityModel<Usuario> createUsuario(@Valid @RequestBody Usuario usuario) {
        Usuario nuevoUsuario = regUsuService.createUsuario(usuario);
        return EntityModel.of(nuevoUsuario,
                WebMvcLinkBuilder
                        .linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getUsuarioById(nuevoUsuario.getId()))
                        .withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getAllUsuarios())
                        .withRel("all-usuarios"));
    }

    @PutMapping("/{id}")
    public EntityModel<Usuario> updateUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        Usuario usuarioActualizado = regUsuService.updateUsuario(id, usuario);
        return EntityModel.of(usuarioActualizado,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getUsuarioById(id)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getAllUsuarios())
                        .withRel("all-usuarios"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuario(@Validated @PathVariable Long id) {
        try {
            regUsuService.deleteUsuario(id);
            return ResponseEntity.ok(new ErrorResponse(true, "Usuario eliminado correctamente."));
        } catch (Exception e) {
            log.error("Error al eliminar el usuario con ID {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(false, "Ocurrió un error al eliminar el usuario."));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> autenticarUsuario(@RequestBody(required = false) Map<String, String> credentials) {
        // validar que vengan datos (json) en el post. ya que si se deba vacio da un
        // error muy grande de la excepcion
        if (credentials == null || credentials.isEmpty()) {
            log.info("Debe ingresar el usuario y la contrasena.");
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(false, "Debe ingresar el usuario y la contraseña."));
        }

        String username = credentials.get("username");
        String password = credentials.get("password");

        // validar que se ingrese el usuario y contraseña
        if (username == null || password == null) {
            log.info("Debe ingresar el usuario y la contrasena.");
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(false, "Debe ingresar el usuario y la contraseña."));
        }

        // buscar el usuario por nomber de usuario
        Usuario usuario = regUsuService.findByUsername(username);

        // validar si el usuario existe
        if (usuario == null) {
            log.info("El usuario no existe.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(false, "El usuario no existe."));
        }

        // validar si la contraseña es correcta
        if (!password.equals(usuario.getPassword())) {
            log.info("Contrasena incorrecta.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(false, "Contraseña incorrecta."));
        }

        // si esta todo correcto lanzamos el true
        log.info("Autenticacion exitosa.");
        return ResponseEntity.ok(new ErrorResponse(true, "Autenticación exitosa."));
    }

    @GetMapping("/{userId}/despachos")
    public ResponseEntity<?> getDespachosForUser(@PathVariable Long userId) {
        try {
            Usuario usuario = regUsuService.getUsuarioById(userId)
                    .orElseThrow(() -> new RegUsuNotFoundException("User not found with ID " + userId));

            List<Despacho> despachos = usuario.getDespachos();
            if (despachos.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(despachos);
        } catch (RegUsuNotFoundException e) {
            log.error("Error fetching despachos for user with ID {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(false, e.getMessage()));
        } catch (Exception e) {
            log.error("Error fetching despachos for user with ID {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(false, "Failed to retrieve despachos."));
        }
    }

    @PostMapping("/{userId}/despachos")
    public ResponseEntity<?> addDespachoToUser(@PathVariable Long userId, @RequestBody Despacho despacho) {
        try {
            Usuario updatedUser = regUsuService.addDespachoToUser(userId, despacho);
            return ResponseEntity.ok(updatedUser);
        } catch (RegUsuNotFoundException e) {
            log.error("User not found with ID {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(false, "User not found"));
        } catch (Exception e) {
            log.error("Failed to add despacho to user with ID {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(false, "Failed to add despacho"));
        }
    }

    @GetMapping("/{userId}/roles")
    public ResponseEntity<?> getRolesForUser(@PathVariable Long userId) {
        try {
            Usuario usuario = regUsuService.getUsuarioById(userId)
                    .orElseThrow(() -> new RegUsuNotFoundException("User not found with ID " + userId));

            List<Rol> roles = usuario.getRoles();
            if (roles.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(roles);
        } catch (RegUsuNotFoundException e) {
            log.error("Error fetching roles for user with ID {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(false, e.getMessage()));
        } catch (Exception e) {
            log.error("Error fetching roles for user with ID {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(false, "Failed to retrieve roles."));
        }
    }

    @PostMapping("/{userId}/roles")
    public ResponseEntity<?> addRoleToUser(@PathVariable Long userId, @RequestBody Rol role) {
        try {
            Usuario updatedUser = regUsuService.addRoleToUser(userId, role);
            return ResponseEntity.ok(updatedUser);
        } catch (RegUsuNotFoundException e) {
            log.error("User not found with ID {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(false, "User not found"));
        } catch (Exception e) {
            log.error("Failed to add role to user with ID {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(false, "Failed to add role"));
        }
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<?> removeRoleFromUser(@PathVariable Long userId, @PathVariable Long roleId) {
        try {
            regUsuService.removeRoleFromUser(userId, roleId);
            return ResponseEntity.ok(new ErrorResponse(true, "Role removed successfully."));
        } catch (Exception e) {
            log.error("Error removing role from user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(false, "Failed to remove role."));
        }
    }

    @DeleteMapping("/{userId}/despachos/{despachoId}")
    public ResponseEntity<?> removeDespachoFromUser(@PathVariable Long userId, @PathVariable Long despachoId) {
        try {
            regUsuService.removeDespachoFromUser(userId, despachoId);
            return ResponseEntity.ok(new ErrorResponse(true, "Despacho removed successfully."));
        } catch (Exception e) {
            log.error("Error removing despacho from user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(false, "Failed to remove despacho."));
        }
    }

    static class ErrorResponse {
        private final boolean respuesta;
        private final String message;

        public ErrorResponse(boolean respuesta, String message) {
            this.respuesta = respuesta;
            this.message = message;
        }

        public boolean isRespuesta() {
            return respuesta;
        }

        public String getMessage() {
            return message;
        }
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ErrorResponse errorResponse = new ErrorResponse(false, "Sin datos ingresados.");
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ErrorResponse(false, "El ID no puede ser nulo o texto.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    static class RegUsuNotFoundException extends RuntimeException {
        public RegUsuNotFoundException(String message) {
            super(message);
        }
    }
}
