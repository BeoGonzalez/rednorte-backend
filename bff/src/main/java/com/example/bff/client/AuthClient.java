package com.example.bff.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// 🔴 Volvemos a apuntar directamente al microservicio de seguridad
@FeignClient(name = "security")
public interface AuthClient {

    @PostMapping("/api/auth/register")
    ResponseEntity<?> register(@RequestBody Object request);

    @PostMapping("/api/auth/login")
    ResponseEntity<?> login(@RequestBody Object request);
}