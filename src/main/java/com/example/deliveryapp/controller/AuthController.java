package com.example.deliveryapp.controller;

import com.example.deliveryapp.model.Rol;
import com.example.deliveryapp.model.Usuario;
import com.example.deliveryapp.service.UsuarioService;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/registro")
    public ResponseEntity<String> registrarUsuario(@RequestBody Usuario usuario, HttpSession session) {
        Usuario currentUsuario = (Usuario) session.getAttribute("usuario");
        if (currentUsuario != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("El usuario ya ha iniciado sesión. Por favor, cierre sesión para registrar un nuevo usuario.");
        }

        if (((Optional<?>) usuarioService.findByEmail(usuario.getEmail())).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El correo electrónico ya está registrado");
        }

        // Aquí validas que se proporcionen nombre, email y contraseña
        if (usuario.getNombre() == null || usuario.getNombre().isEmpty() ||
                usuario.getEmail() == null || usuario.getEmail().isEmpty() ||
                usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("El nombre, correo electrónico y la contraseña son obligatorios");
        }

        usuarioService.addUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado exitosamente");
    }


    @PostMapping("/inicio-sesion")
    public ResponseEntity<String> iniciarSesion(@RequestBody Usuario usuario, HttpSession session) {
        Usuario currentUsuario = (Usuario) session.getAttribute("usuario");
        if (currentUsuario != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("El usuario ya ha iniciado sesión. Por favor, cierre sesión primero.");
        }

        Usuario authenticatedUsuario = usuarioService.autenticarUsuario(usuario.getEmail(), usuario.getPassword());
        if (authenticatedUsuario != null) {
            session.setAttribute("usuario", authenticatedUsuario);
            return ResponseEntity.ok("Usuario inició sesión exitosamente");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }
    }

    @PostMapping("/cerrar-sesion")
    public ResponseEntity<String> cerrarSesion(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Usuario cerró sesión exitosamente");
    }

    @GetMapping("/usuario-actual")
    public ResponseEntity<CurrentUsuarioResponse> obtenerUsuarioActual(HttpSession session) {
        Usuario currentUsuario = (Usuario) session.getAttribute("usuario");
        if (currentUsuario != null) {
            Rol roleName = currentUsuario.getRol(); // Fix: Assign the role name directly to a single Rol object
            CurrentUsuarioResponse response = new CurrentUsuarioResponse(currentUsuario, roleName);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping("/actualizar-rol/{idUsuario}")
public ResponseEntity<String> actualizarRolUsuario(@PathVariable Long idUsuario, @RequestBody String nuevoRol,
        HttpSession session) {
    Usuario currentUsuario = (Usuario) session.getAttribute("usuario");
    if (currentUsuario != null && "admin".equals(currentUsuario.getRol().getNombre())) { // Ensure you compare string values correctly
        Optional<Usuario> optionalUsuarioActualizar = usuarioService.getUsuarioById(idUsuario);
        if (optionalUsuarioActualizar.isPresent()) {
            Usuario usuarioActualizar = optionalUsuarioActualizar.get();
            // Assume you have a method in the Rol class to create a role by name. Adjust as per your Rol class implementation
            Rol rol = new Rol(); // You would need to fetch or create a Rol object based on nuevoRol
            rol.setNombre(nuevoRol);
            usuarioActualizar.setRol(rol);
            usuarioService.updateUsuario(idUsuario, usuarioActualizar);
            return ResponseEntity.ok("Rol de usuario actualizado exitosamente");
        } else {
            return ResponseEntity.notFound().build();
        }
    } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Acceso denegado. Solo los administradores pueden actualizar los roles de usuario.");
    }
}


    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> obtenerTodosUsuarios(HttpSession session) {
        Usuario currentUsuario = (Usuario) session.getAttribute("usuario");
        if (currentUsuario != null && "admin".equals(currentUsuario.getRol().getNombre())) {
            List<Usuario> usuarios = usuarioService.getAllUsuarios();
            return ResponseEntity.ok(usuarios);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // Clase de respuesta personalizada
    static class CurrentUsuarioResponse {
        private Usuario usuario;
        private Rol roleName;

        public CurrentUsuarioResponse(Usuario usuario, Rol roleName) {
            this.usuario = usuario;
            this.roleName = roleName;
        }

        public Usuario getUsuario() {
            return usuario;
        }

        public Rol getRoleName() {
            return roleName;
        }
    }

    static class RegistrationRequest {
        private Usuario usuario;
        private String roleName;
    
        // Getters and setters
        public Usuario getUsuario() {
            return usuario;
        }
    
        public void setUsuario(Usuario usuario) {
            this.usuario = usuario;
        }
    
        public String getRoleName() {
            return roleName;
        }
    
        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }
    }
}
