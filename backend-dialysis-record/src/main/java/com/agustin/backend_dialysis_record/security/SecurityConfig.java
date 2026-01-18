package com.agustin.backend_dialysis_record.security;

import com.agustin.backend_dialysis_record.security.jwt.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration // <- clase de configuración de Spring
@EnableMethodSecurity // <- habilita @PreAuthorize, @PostAuthorize, etc.
public class SecurityConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "http://127.0.0.1:*",
                "http://10.0.2.2:8080"
        ));


        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));

        config.setExposedHeaders(List.of("Authorization"));

        config.setAllowCredentials(false);

        // Cache del preflight
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        //System.out.println(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("1234")); @TODO: gen encode

        return http
                .cors(withDefaults())
                // 1) CSRF: en APIs REST stateless normalmente se desactiva
                //    (CSRF protege formularios/sesiones de navegador)
                .csrf(csrf -> csrf.disable())

                // 2) SessionManagement: STATELESS = no se guarda sesión de usuario en servidor
                //    Cada request debe traer su JWT
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3) Reglas de autorización por rutas
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Endpoints públicos: login, register, etc.
                        .requestMatchers("/auth/**", "/error", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Endpoints que requieren permiso especifico
                        .requestMatchers("/api/doctors/**").hasRole("DOCTOR")
                        .requestMatchers("/api/patients/**").hasAnyRole("PATIENT", "DOCTOR", "ADMIN")
                        .requestMatchers("/api/sessions/**").hasAnyRole("DOCTOR", "PATIENT")

                        .requestMatchers("/auth/logout").permitAll()
                        .requestMatchers("/auth/logout/all").authenticated()

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
