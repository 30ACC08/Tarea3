package com.example.tarea3.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    // Redirige la página principal al login
    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }
    
    // Muestra la página de login
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    // Muestra la página de registro
    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }
    
    // Alias para /registro (para mantener compatibilidad con el formulario)
    @GetMapping("/usuarios/registro")
    public String registroAlternativo() {
        return "registro";
    }
    
    // Alias para formulario de registro detallado
    @GetMapping("/usuarios/registro/formulario")
    public String registroFormulario() {
        return "registro";
    }
    
    // Muestra la página principal después del login
    @GetMapping("/home")
    public String home() {
        return "home";
    }
    
    // Alternativa para dashboard si es necesario
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}