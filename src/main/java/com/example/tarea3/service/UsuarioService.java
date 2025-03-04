package com.example.tarea3.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tarea3.DTO.RegistroDTO;
import com.example.tarea3.model.Rol;
import com.example.tarea3.model.Usuario;
import com.example.tarea3.repository.RolRepository;
import com.example.tarea3.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, 
                          RolRepository rolRepository, 
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registra un nuevo usuario con rol USER por defecto
     */
    @Transactional
    public Usuario registrarUsuario(RegistroDTO registroDto) {
        // Verificar si las contraseñas coinciden
        if (!registroDto.getPasswordHash().equals(registroDto.getConfirmPassword())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }
        
        // Verificar si el email ya está en uso
        if (usuarioRepository.existsByEmail(registroDto.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        
        // Crear nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(registroDto.getNombre());
        usuario.setEmail(registroDto.getEmail());
        usuario.setPassword(passwordEncoder.encode(registroDto.getPasswordHash()));
        
        // Asignar rol de usuario por defecto
        Rol rolUsuario = rolRepository.findByNombre("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Rol de usuario no encontrado"));
        usuario.addRol(rolUsuario);
        
        return usuarioRepository.save(usuario);
    }
    
    /**
     * Verifica si un email ya está registrado
     */
    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
    
    /**
     * Obtiene todos los usuarios
     */
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    /**
     * Obtiene un usuario por su id
     */
    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    /**
     * Obtiene un usuario por su nombre
     */
    public Optional<Usuario> obtenerPorNombre(String nombre) {
        return usuarioRepository.findByNombre(nombre);
    }

    /**
     * Guarda o actualiza un usuario
     */
    @Transactional
    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    /**
     * Elimina un usuario por su id
     */
    @Transactional
    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }
}