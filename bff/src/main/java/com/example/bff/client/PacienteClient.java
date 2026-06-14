package com.example.bff.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "ms-pacientes")
public interface PacienteClient {

    @GetMapping("/api/pacientes")
    ResponseEntity<?> getPacientes();

    @GetMapping("/api/pacientes/{id}")
    ResponseEntity<?> obtenerPacientePorId(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String token);

    @PostMapping("/api/pacientes")
    ResponseEntity<?> crearPaciente(@RequestBody Map<String, Object> request);
}
