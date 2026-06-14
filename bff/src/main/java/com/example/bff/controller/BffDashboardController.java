package com.example.bff.controller;

import com.example.bff.client.DoctorClient;
import com.example.bff.client.PacienteClient;
import feign.FeignException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/bff/dashboard") // 🟢 ALINEADO EXACTAMENTE CON EL API GATEWAY
public class BffDashboardController {

    private final PacienteClient pacienteClient;
    private final DoctorClient doctorClient;

    // Inyección de dependencias por constructor
    public BffDashboardController(PacienteClient pacienteClient, DoctorClient doctorClient) {
        this.pacienteClient = pacienteClient;
        this.doctorClient = doctorClient;
    }

    // El Dashboard de Angular llamará a esta ruta
    @GetMapping("/{authId}")
    public ResponseEntity<Map<String, Object>> obtenerDashboardCompleto(@PathVariable Long authId) {
        Map<String, Object> dashboardResponse = new HashMap<>();

        // 1. Orquestación: Ir a buscar el Perfil del Doctor
        try {
            ResponseEntity<?> doctorResponse = doctorClient.obtenerPorAuthId(authId);
            dashboardResponse.put("perfilDoctor", doctorResponse.getBody());
        } catch (FeignException e) {
            // Tolerancia a fallos: Si el microservicio de doctores cae, el dashboard no explota
            dashboardResponse.put("perfilDoctor", null);
            dashboardResponse.put("alertaDoctor", "No se pudo cargar el perfil del médico");
        }

        // 2. Orquestación: Ir a buscar los Pacientes
        try {
            ResponseEntity<?> pacientesResponse = pacienteClient.getPacientes();
            dashboardResponse.put("listaPacientes", pacientesResponse.getBody());
        } catch (FeignException e) {
            dashboardResponse.put("listaPacientes", null);
            dashboardResponse.put("alertaPacientes", "Los pacientes no están disponibles en este momento");
        }

        // 3. Devolver el JSON perfectamente ensamblado
        return ResponseEntity.ok(dashboardResponse);
    }
}