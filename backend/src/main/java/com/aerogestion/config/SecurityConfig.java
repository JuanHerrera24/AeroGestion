package com.aerogestion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.aerogestion.repository.UsuarioRepository;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Seguridad de la API: autenticación HTTP Basic (email y contraseña en cada petición)
 * y permisos por rol. La búsqueda de vuelos y el registro son públicos.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/error").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/registro").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/vuelos/**", "/api/aeropuertos").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/reservas/**").hasRole("PASAJERO")
                .anyRequest().authenticated())
            .httpBasic(basic -> basic.authenticationEntryPoint((req, res, e) ->
                        escribirError(res, 401, "No autenticado", "Email o contraseña incorrectos")))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) ->
                        escribirError(res, 401, "No autenticado", "Envía tu email y contraseña (Basic Auth)"))
                .accessDeniedHandler((req, res, e) ->
                        escribirError(res, 403, "Acceso denegado", "Tu rol no tiene permiso para esta operación")));
        return http.build();
    }

    private static void escribirError(HttpServletResponse res, int status, String error, String mensaje)
            throws java.io.IOException {
        res.setStatus(status);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write("{\"status\":" + status + ",\"error\":\"" + error + "\",\"mensaje\":\"" + mensaje + "\"}");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
        return email -> usuarioRepository.findByEmail(email.trim().toLowerCase())
                .map(u -> User.withUsername(u.getEmail())
                        .password(u.getPasswordHash())
                        .roles(u.getRol().name())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Credenciales inválidas"));
    }
}
