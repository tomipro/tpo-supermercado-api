package com.uade.tpo.supermercado.controller;

import com.uade.tpo.supermercado.controller.dto.LoginRequest;
import com.uade.tpo.supermercado.controller.dto.UsuarioLoginResponse;
import com.uade.tpo.supermercado.entity.Usuario;
import com.uade.tpo.supermercado.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        List<Usuario> usuarios = usuarioService.getAllUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    // Obtener un usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable int id) {
        Optional<Usuario> usuario = usuarioService.getUsuarioById(id);
        return usuario.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    // Crear un nuevo usuario
    @PostMapping
    public ResponseEntity<Usuario> createUsuario(@RequestBody Usuario usuario) {
        try {
            Usuario nuevoUsuario = usuarioService.createOrUpdateUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Actualizar un usuario existente (reemplazo total, PUT)
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable int id, @RequestBody Usuario usuario) {
        try {
            usuario.setId(id); // Asegurarse de que el ID sea el correcto
            Usuario usuarioActualizado = usuarioService.createOrUpdateUsuario(usuario);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Actualización parcial de usuario (PATCH)
    @PatchMapping("/{id}")
    public ResponseEntity<Usuario> patchUsuario(@PathVariable int id, @RequestBody Usuario usuarioPatch) {
        try {
            Optional<Usuario> usuarioOpt = usuarioService.getUsuarioById(id);
            if (!usuarioOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            Usuario usuarioExistente = usuarioOpt.get();
            // Solo actualiza los campos que vienen en el PATCH (no null)
            if (usuarioPatch.getUsername() != null) usuarioExistente.setUsername(usuarioPatch.getUsername());
            if (usuarioPatch.getEmail() != null) usuarioExistente.setEmail(usuarioPatch.getEmail());
            if (usuarioPatch.getPassword() != null) usuarioExistente.setPassword(usuarioPatch.getPassword());
            if (usuarioPatch.getNombre() != null) usuarioExistente.setNombre(usuarioPatch.getNombre());
            if (usuarioPatch.getApellido() != null) usuarioExistente.setApellido(usuarioPatch.getApellido());
            if (usuarioPatch.getRol() != null) usuarioExistente.setRol(usuarioPatch.getRol());
            Usuario usuarioActualizado = usuarioService.createOrUpdateUsuario(usuarioExistente);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Cambiar la contraseña de un usuario
    @PutMapping("/{id}/password")
    public ResponseEntity<String> cambiarPassword(
            @PathVariable int id,
            @RequestBody PasswordChangeRequest passwordChangeRequest) {
        Optional<Usuario> usuarioOpt = usuarioService.getUsuarioById(id);
        if (!usuarioOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
        }
        Usuario usuario = usuarioOpt.get();
        // Verifica la contraseña actual
        if (!usuario.getPassword().equals(passwordChangeRequest.getContrasenaActual())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La contraseña actual es incorrecta.");
        }
        usuario.setPassword(passwordChangeRequest.getNuevaContrasena());
        usuarioService.createOrUpdateUsuario(usuario);
        return ResponseEntity.ok("La contraseña fue actualizada correctamente.");
    }

    // Eliminar un usuario por ID (devuelve mensaje de confirmación)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUsuario(@PathVariable int id) {
        usuarioService.deleteUsuarioById(id);
        return ResponseEntity.ok("Usuario eliminado correctamente.");
    }

    // Verificar si un usuario existe por nombre de usuario
    @GetMapping("/exists/username/{username}")
    public ResponseEntity<Boolean> existsByUsername(@PathVariable String username) {
        boolean exists = usuarioService.existsByUsername(username);
        return ResponseEntity.ok(exists);
    }

    // Verificar si un usuario existe por email
    @GetMapping("/exists/email/{email}")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable String email) {
        boolean exists = usuarioService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    // Endpoint de login (autenticación de usuario)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<Usuario> usuarioOpt = loginRequest.getUsername() != null
                ? usuarioService.getUsuarioByUsername(loginRequest.getUsername())
                : usuarioService.getUsuarioByEmail(loginRequest.getEmail());
        if (!usuarioOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña incorrectos.");
        }
        Usuario usuario = usuarioOpt.get();
        if (!usuario.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña incorrectos.");
        }
        // Retornar solo datos públicos (sin contraseña)
        UsuarioLoginResponse response = new UsuarioLoginResponse(
            usuario.getId(),
            usuario.getUsername(),
            usuario.getEmail(),
            usuario.getNombre(),
            usuario.getApellido(),
            usuario.getRol()
        );
        return ResponseEntity.ok(response);
    }
}
