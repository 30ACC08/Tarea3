package com.example.tarea3.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tarea3.model.SecurityUser;
import com.example.tarea3.model.Usuario;
import com.example.tarea3.repository.UsuarioRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // IMPORTANTE: El formulario de login debe enviar el nombre de usuario, no el email
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNombre(username);
        
        if (usuarioOpt.isEmpty()) {
            // Si no se encuentra por nombre, intentar por email
            usuarioOpt = usuarioRepository.findByEmail(username);
        }
        
        Usuario usuario = usuarioOpt.orElseThrow(() -> 
            new UsernameNotFoundException("Usuario no encontrado con nombre o email: " + username));
            
        return new SecurityUser(usuario);
    }
}