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
    public ResponseEntity<?> getPatients() {
        try {
            // Intenta buscar los pacientes en el microservicio real
            return pacienteClient.getPacientes();
        } catch (FeignException.NotFound e) {
            // 🔴 Si ms-pacientes devuelve 404, lo atrapamos aquí
            return ResponseEntity.status(404).body("{\"error\": \"El microservicio ms-pacientes no tiene la ruta /api/pacientes o no devolvió nada\"}");
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).body(e.contentUTF8());
        }
    }
}