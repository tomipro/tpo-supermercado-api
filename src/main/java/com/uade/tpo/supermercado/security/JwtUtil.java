package com.uade.tpo.supermercado.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.security.Key;

@Component
public class JwtUtil {
    // La clave debe tener al menos 32 bytes para HS256
    private final String SECRET_KEY = "supermercado_secret_key_supermercado_secret_key";
    private final Key key;

    public JwtUtil() {
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(String username, String rol) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", rol);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 horas
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRol(String token) {
        return (String) extractAllClaims(token).get("rol");
    }

    public boolean isTokenValid(String token, String username) {
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
