package com.uade.tpo.supermercado.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.http.HttpMethod;

import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;
import java.util.List;

@Configuration

public class SecurityConfig {
    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // importante si usás cookies o headers personalizados
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/usuarios/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()
                        .requestMatchers("/usuarios/exists/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/usuarios/cambiar-password").permitAll()

                        // Endpoints publicos de Categoria
                        .requestMatchers(HttpMethod.GET, "/categorias").permitAll()
                        .requestMatchers(HttpMethod.GET, "/categorias/*").permitAll()
                        // Endpoints protegidos de categorías (solo ADMIN)
                        .requestMatchers(HttpMethod.POST, "/categorias").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/categorias").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/categorias/*").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/categorias/*").hasAuthority("ADMIN")

                        // Endpoints publicos de Productos
                        .requestMatchers(HttpMethod.GET, "/producto/catalogo").permitAll()
                        .requestMatchers(HttpMethod.GET, "/producto").permitAll()
                        .requestMatchers(HttpMethod.GET, "/producto/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/producto/id/*").permitAll()
                        // Endpoints protegidos de Productos (solo ADMIN)
                        .requestMatchers(HttpMethod.POST, "/productos").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/producto").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/producto/*").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/producto/*").hasAuthority("ADMIN")

                        // Endpoints protegidos de Carrito(Solo Usuario y ADMIN)
                        .requestMatchers(HttpMethod.POST, "/carritos/usuarios/{usuarioId}")
                        .hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/carritos/usuarios/{usuarioId}")
                        .hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/carritos/usuario/{usuarioId}/productos/{productoId}")
                        .hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/carritos/usuarios/{usuarioId}/productos/{productoId}")
                        .hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/carritos/usuarios/{usuarioId}/vaciar")
                        .hasAnyAuthority("USER", "ADMIN")

                        // Endpoints protegidos de Orden (solo Usuario y ADMIN)
                        .requestMatchers(HttpMethod.POST, "/ordenes")
                        .hasAnyAuthority("USER", "ADMIN")
                        // Endpoints protegidos de Orden (solo ADMIN)
                        .requestMatchers(HttpMethod.GET, "/ordenes/usuarios/*")
                        .hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/ordenes/usuarios/*/ordenes/*")
                        .hasAnyAuthority("USER", "ADMIN")

                        .requestMatchers("/admin/**").hasAuthority("ADMIN")
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(org.springframework.security.config.Customizer.withDefaults());
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        // CORS configuration can be added as a bean if needed.
        return http.build();
    }

    // solo para prueba/demo, no tiene password encoder. en prod se usaria
    // BCryptPasswordEncoder.
    @Bean
    public static NoOpPasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }
}
