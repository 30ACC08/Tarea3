package com.example.tarea3.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.tarea3.model.Usuario;
import com.example.tarea3.repository.UsuarioRepository;

@Controller
public class PerfilController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    @Autowired
    public PerfilController(UsuarioRepository usuarioRepository, 
                           PasswordEncoder passwordEncoder,
                           UserDetailsService userDetailsService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/perfil")
    public String mostrarPerfil(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // Buscar el usuario por nombre
        Usuario usuario = usuarioRepository.findByNombre(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        model.addAttribute("usuario", usuario);
        return "perfil";
    }

    @GetMapping("/perfil/editar")
    public String mostrarFormularioEdicion(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // Buscar el usuario por nombre (porque usas 'nombre' en la BD, no 'username')
        Usuario usuario = usuarioRepository.findByNombre(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        model.addAttribute("usuario", usuario);
        return "editar_perfil"; // Thymeleaf usará el objeto 'usuario' en la vista
    }

    @PostMapping("/perfil/editar")
    public String actualizarPerfil(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam(required = false) String password,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioRepository.findByNombre(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String nombreAnterior = usuario.getNombre();
        
        // Actualizar los datos del usuario
        usuario.setNombre(nombre);
        usuario.setEmail(email);

        // Si el usuario ingresó una nueva contraseña, encriptarla y actualizarla
        if (password != null && !password.isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(password));
        }

        // Guardar el usuario actualizado
        usuarioRepository.save(usuario);
        
        // Si el nombre de usuario cambió, actualizar la autenticación
        if (!nombreAnterior.equals(nombre)) {
            // Cargar los detalles actualizados del usuario
            UserDetails nuevoUserDetails = userDetailsService.loadUserByUsername(nombre);
            
            // Crear una nueva autenticación con los detalles actualizados
            Authentication nuevaAuth = new UsernamePasswordAuthenticationToken(
                nuevoUserDetails, nuevoUserDetails.getPassword(), nuevoUserDetails.getAuthorities());
            
            // Actualizar el SecurityContext
            SecurityContextHolder.getContext().setAuthentication(nuevaAuth);
        }

        redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente");
        return "redirect:/perfil";
    }
}