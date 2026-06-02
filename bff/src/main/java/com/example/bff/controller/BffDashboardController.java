package com.example.bff.controller;

import com.example.bff.client.PacienteClient;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bff/dashboard")
public class BffDashboardController {

    @Autowired
    private PacienteClient pacienteClient;

    @GetMapping("/patients")
    public ResponseEntity<?> getPatients(@RequestHeader("Authorization") String token) { // 🔴 Capturamos el token del frontend
        try {
            // 🔴 Le pasamos el token al Feign Client
            return pacienteClient.getPacientes(token);
        } catch (FeignException.NotFound e) {
            return ResponseEntity.status(404).body("{\"error\": \"El microservicio ms-pacientes no tiene la ruta /api/pacientes o no devolvió nada\"}");
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).body(e.contentUTF8());
        }
    }
}