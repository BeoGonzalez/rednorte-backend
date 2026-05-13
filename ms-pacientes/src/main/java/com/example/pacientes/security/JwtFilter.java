package com.example.pacientes.security;

import com.example.pacientes.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        String username = jwtService.extractUsername(jwt);

        // 🔴 Extraemos el rol del token (Asumiendo que lo guardaste como claim "rol" en el ms-security)
        // Si tu JwtService no tiene este método, necesitas agregarlo para extraer el "rol" del Payload del JWT.
        String rol = jwtService.extractClaim(jwt, claims -> claims.get("rol", String.class));

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtService.isTokenValid(jwt)) {

                // Si por alguna razón el token no trae rol, asumimos por seguridad que es un paciente básico
                String authority = (rol != null) ? rol : "ROLE_PACIENTE";

                // 🔴 Ahora SÍ le pasamos la autoridad a Spring Security
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username, null, Collections.singletonList(new SimpleGrantedAuthority(authority))
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}