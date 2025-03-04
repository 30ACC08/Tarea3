package com.example.tarea3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Habilita @PreAuthorize en los controladores
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // Desactivar CSRF (para pruebas, activarlo en prod)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/registro", "/usuarios/registro", "/usuarios/registro/formulario", "/usuarios/registrar").permitAll()
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN") // Solo Admin accede a /admin
                        .requestMatchers("/perfil/**").authenticated() // Cualquier usuario autenticado
                        .anyRequest().authenticated() // Resto necesita autenticación
                )
                .formLogin(form -> form
                        .loginPage("/login") // Página de login
                        .permitAll()
                        .defaultSuccessUrl("/home", true) // Siempre redirige a /home tras login
                        .failureUrl("/login?error=true")
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}