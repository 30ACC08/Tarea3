package com.example.tarea3.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.tarea3.DTO.RegistroDTO;

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
    
    // Muestra la página de registro, añadiendo el objeto RegistroDTO al modelo
    @GetMapping("/registro")
    public String registro(Model model) {
        model.addAttribute("usuario", new RegistroDTO());
        return "registro";
    }
    
    // Alias para /registro, también añadiendo el objeto RegistroDTO
    @GetMapping("/usuarios/registro")
    public String registroAlternativo(Model model) {
        model.addAttribute("usuario", new RegistroDTO());
        return "registro";
    }
    
    // Muestra la página principal después del login
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}