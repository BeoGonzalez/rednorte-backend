package com.example.bff.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "ms-pacientes")
public interface PacienteClient {
    @GetMapping("/api/pacientes")
    ResponseEntity<?> getPacientes();

    @GetMapping("/api/pacientes/{id}")
    Object obtenerPacientePorId(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String token // ⬅️ El BFF ahora enviará el token

    );
}
