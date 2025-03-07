package com.example.tarea3.Controllers;


import java.util.List;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.tarea3.model.Usuario;
import com.example.tarea3.repository.UsuarioRepository;
import com.example.tarea3.service.UsuarioService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    public AdminController(UsuarioService usuarioService, 
                          UsuarioRepository usuarioRepository, 
                          PasswordEncoder passwordEncoder,
                          UserDetailsService userDetailsService) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.obtenerTodos();
        model.addAttribute("usuarios", usuarios);
        return "usuarios";
    }

    // Cargar formulario de edición
    @GetMapping("/usuarios/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.obtenerPorId(id);
        if (usuario == null) {
            return "redirect:/admin/usuarios";
        }
        model.addAttribute("usuario", usuario);
        return "editar_usuario"; // Cambiar la vista
    }

    // Guardar cambios en usuario
    @PostMapping("/usuarios/editar/{id}")
    public String actualizarUsuario(
            @PathVariable Long id,
            @ModelAttribute Usuario usuarioForm,
            @RequestParam(required = false) String password,
            @AuthenticationPrincipal UserDetails currentUser) {

        Usuario usuario = usuarioService.obtenerPorId(id);
        if (usuario == null) {
            return "redirect:/admin/usuarios";
        }

        String nombreAnterior = usuario.getNombre();
        // Actualiza solo los campos necesarios
        usuario.setNombre(usuarioForm.getNombre());
        usuario.setEmail(usuarioForm.getEmail());

        if (password != null && !password.isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(password));
        }

        usuarioService.guardar(usuario);
        
        // Si estamos editando nuestro propio usuario y el nombre cambió, actualizar la autenticación
        if (currentUser.getUsername().equals(nombreAnterior) && !nombreAnterior.equals(usuarioForm.getNombre())) {
            // Cargar los detalles actualizados del usuario
            UserDetails nuevoUserDetails = userDetailsService.loadUserByUsername(usuarioForm.getNombre());
            
            // Crear una nueva autenticación con los detalles actualizados
            Authentication nuevaAuth = new UsernamePasswordAuthenticationToken(
                nuevoUserDetails, nuevoUserDetails.getPassword(), nuevoUserDetails.getAuthorities());
            
            // Actualizar el SecurityContext
            SecurityContextHolder.getContext().setAuthentication(nuevaAuth);
        }
        
        return "redirect:/admin/usuarios";
    }

    // Eliminar usuario
    @GetMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return "redirect:/admin/usuarios";
    }
    
    // Perfil del administrador
    @GetMapping("/perfil")
    public String mostrarPerfilAdmin(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioRepository.findByNombre(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        model.addAttribute("usuario", usuario);
        return "perfil"; // Usa la misma vista de perfil
    }
    
    // Editar perfil del administrador
    @GetMapping("/perfil/editar")
    public String mostrarFormularioEdicionAdmin(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioRepository.findByNombre(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        model.addAttribute("usuario", usuario);
        return "editar_perfil"; // Usa la misma vista de edición de perfil
    }
    
    // Actualizar perfil del administrador
    @PostMapping("/perfil/editar")
    public String actualizarPerfilAdmin(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam(required = false) String password,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioRepository.findByNombre(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String nombreAnterior = usuario.getNombre();
        
        usuario.setNombre(nombre);
        usuario.setEmail(email);

        if (password != null && !password.isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(password));
        }

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
        return "redirect:/admin/perfil";
    }
}