package com.example.bff.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest; // 🔴 Usa javax.servlet si estás en Spring Boot 2

@Configuration
public class FeignConfig implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // Captura la petición original que llegó desde Angular al BFF
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            // Extrae el token JWT
            String authHeader = request.getHeader("Authorization");

            // Si hay un token, se lo inyectamos a la llamada que va hacia los microservicios
            if (authHeader != null) {
                template.header("Authorization", authHeader);
            }
        }
    }
}
