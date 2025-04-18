package com.uade.tpo.supermercado.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/usuarios/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()
                        .requestMatchers("/usuarios/exists/**").permitAll()

                        // Endpoints publicos de Categoria
                        .requestMatchers(HttpMethod.GET, "/categorias").permitAll()
                        .requestMatchers(HttpMethod.GET, "/categorias/*").permitAll()
                        // Endpoints protegidos de categorías (solo ADMIN)
                        .requestMatchers(HttpMethod.POST, "/categorias").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/categorias").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/categorias/*").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/categorias/*").hasAuthority("ADMIN")

                        // Endpoints protegidos de Carrito(Solo Usuario y ADMIN)
                        .requestMatchers(HttpMethod.POST, "/carritos/usuarios/*").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/carritos/usuarios/*").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/carritos/usuario/*/productos/*")
                        .hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/carritos/usuarios/*/productos/*")
                        .hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/carritos/usuarios/*/vaciar")
                        .hasAnyAuthority("USER", "ADMIN")

                        .requestMatchers("/admin/**").hasAuthority("ADMIN")
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // solo para prueba/demo, no tiene password encoder. en prod se usaria
    // BCryptPasswordEncoder.
    @Bean
    public static NoOpPasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }
}
