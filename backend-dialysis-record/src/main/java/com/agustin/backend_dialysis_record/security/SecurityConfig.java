package com.agustin.backend_dialysis_record.security;

import com.agustin.backend_dialysis_record.security.jwt.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // <- clase de configuración de Spring
@EnableMethodSecurity // <- habilita @PreAuthorize, @PostAuthorize, etc.
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        //System.out.println(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("1234")); @TODO: gen encode

        return http
                // 1) CSRF: en APIs REST stateless normalmente se desactiva
                //    (CSRF protege formularios/sesiones de navegador)
                .csrf(csrf -> csrf.disable())

                // 2) SessionManagement: STATELESS = no se guarda sesión de usuario en servidor
                //    Cada request debe traer su JWT
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3) Reglas de autorización por rutas
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos: login, register, etc.
                        .requestMatchers("/auth/**", "/error", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Endpoints que requieren permiso especifico
                        .requestMatchers("/api/doctors/**").hasRole("DOCTOR")
                        .requestMatchers("/api/patients/**").hasAnyRole("DOCTOR", "ADMIN")
                        .requestMatchers("/api/sessions/**").hasAnyRole("DOCTOR", "PATIENT")

                        // Cualquier otra ruta requiere autenticación
                        .anyRequest().authenticated()
                )

                // 4) Insertamos nuestro filtro ANTES del filtro estándar de username/password
                //    Así, cuando llegue al controller, el SecurityContext ya está seteado
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                // 5) Construir la cadena final
                .build();
    }
}
