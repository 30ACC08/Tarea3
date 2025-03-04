package com.example.tarea3.Controllers;

import com.example.tarea3.model.Usuario;
import com.example.tarea3.model.Rol;
import com.example.tarea3.repository.RolRepository;
import com.example.tarea3.repository.UsuarioRepository;
import com.example.tarea3.DTO.RegistroDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.HashSet;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/usuarios")
public class RegistroController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private RolRepository rolRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Modelo para el formulario de registro
    @GetMapping("/registro/formulario")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new RegistroDTO());
        return "registro";
    }
    
    // Alias para mantener compatibilidad
    @GetMapping("/registro")
    public String mostrarFormularioRegistroAlias(Model model) {
        return mostrarFormularioRegistro(model);
    }
    
    // Procesamiento del formulario de registro
    @PostMapping("/registrar")
    public String registrarUsuario(@ModelAttribute("usuario") @Valid RegistroDTO registroDTO,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Error en los datos del formulario.");
            return "redirect:/usuarios/registro/formulario";
        }
        
        // Verificar si el email ya existe
        if (usuarioRepository.existsByNombre(registroDTO.getEmail())) {
            redirectAttributes.addFlashAttribute("error", "El correo ya está registrado.");
            return "redirect:/usuarios/registro/formulario";
        }
        
        // Verificar si las contraseñas coinciden
        if (!registroDTO.getPasswordHash().equals(registroDTO.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden.");
            return "redirect:/usuarios/registro/formulario";
        }
        
        try {
            // Crear nuevo usuario
            Usuario usuario = new Usuario();
            usuario.setNombre(registroDTO.getNombre());
            usuario.setEmail(registroDTO.getEmail());
            usuario.setPassword(passwordEncoder.encode(registroDTO.getPasswordHash()));
            
            // Asignar rol de usuario por defecto
            Rol rolUsuario = rolRepository.findByNombre("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
            usuario.setRoles(new HashSet<>(Collections.singletonList(rolUsuario)));
            
            // Guardar usuario
            usuarioRepository.save(usuario);
            
            redirectAttributes.addFlashAttribute("success", "Registro exitoso. ¡Ya puedes iniciar sesión!");
            return "redirect:/login";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Hubo un error al registrar el usuario: " + e.getMessage());
            return "redirect:/usuarios/registro/formulario";
        }
    }
}