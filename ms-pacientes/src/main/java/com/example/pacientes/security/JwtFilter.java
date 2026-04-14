package com.example.pacientes.security;

import com.example.pacientes.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    @Autowired
    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Buscamos la credencial en el encabezado de la petición
        String authHeader = request.getHeader("Authorization");

        // 2. Verificamos si existe y si tiene el formato correcto ("Bearer token...")
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Extraemos solo el código, quitando la palabra "Bearer "

            // 3. Pasamos el token por nuestro escáner matemático
            if (jwtService.validarToken(token)) {
                String email = jwtService.extraerEmail(token);

                // 4. ¡Es válido! Le avisamos al Guardia de Spring Security que lo deje pasar
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        email, null, new ArrayList<>() // Aquí irían los roles (PACIENTE, ADMIN), por ahora vacío
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 5. Dejamos que la petición siga su camino
        filterChain.doFilter(request, response);
    }


}
