package com.uade.tpo.supermercado.service;

import com.uade.tpo.supermercado.entity.Usuario;
import com.uade.tpo.supermercado.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // obtener usuario
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    public Page<Usuario> getAllUsuarios(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    // buscar un usuario por ID
    public Optional<Usuario> getUsuarioById(int id) {
        return usuarioRepository.findById(id);
    }

    public Usuario getUsuarioByIdOrThrow(int id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    //  buscar un usuario por nombre de usuario
    public Optional<Usuario> getUsuarioByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    // buscar un usuario por email
    public Optional<Usuario> getUsuarioByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    // buscar usuarios por rol
    public List<Usuario> getUsuariosByRol(String rol) {
        return usuarioRepository.findByRol(rol);
    }

    // crear o actualizar un usuario
    public Usuario createOrUpdateUsuario(Usuario usuario) {
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso.");
        }
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso.");
        }
        return usuarioRepository.save(usuario);
    }

    // borrar un usuario por ID
    public void deleteUsuarioById(int id) {
        usuarioRepository.deleteById(id);
    }

    // validar si un usuario existe por nombre de usuario
    public boolean existsByUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    // verificar si un usuario existe por email
    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
}