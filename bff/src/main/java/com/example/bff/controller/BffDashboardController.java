package com.example.bff.controller;

import com.example.bff.client.PacienteClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bff/api/v1/dashboard")
@CrossOrigin(origins = "http://localhost:4200") // Permite la conexión desde Angular
public class BffDashboardController {

    private final PacienteClient pacienteClient;

    @Autowired
    public BffDashboardController(PacienteClient pacienteClient) {
        this.pacienteClient = pacienteClient;
    }

    @GetMapping("/paciente-resumen/{id}")
    public ResponseEntity<Object> obtenerResumenPaciente(@PathVariable Long id) {
        // El BFF llama al ms-pacientes a través de Feign
        Object datosPaciente = pacienteClient.obtenerPacientePorId(id);

        // Aquí podrías agregar más llamadas (ej. a ms-lista-espera)
        // y mezclar los datos antes de devolverlos.

        return ResponseEntity.ok(datosPaciente);
    }
}
