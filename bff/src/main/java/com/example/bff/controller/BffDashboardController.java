package com.example.bff.controller;

import com.example.bff.client.PacienteClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bff/api/v1/dashboard")
@CrossOrigin(origins = "http://localhost:4200")
public class BffDashboardController {

    private final PacienteClient pacienteClient;

    @Autowired
    public BffDashboardController(PacienteClient pacienteClient) {
        this.pacienteClient = pacienteClient;
    }

    @GetMapping("/paciente-resumen/{id}")
    public ResponseEntity<Object> obtenerResumenPaciente(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) { // ⬅️ 1. Atrapamos el token de Angular

        // 2. Le pasamos el token al cliente Feign
        Object datosPaciente = pacienteClient.obtenerPacientePorId(id, token);

        return ResponseEntity.ok(datosPaciente);
    }
}
