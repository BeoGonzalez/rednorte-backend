package com.example.bff.controller;

import com.example.bff.client.DoctorClient;
import com.example.bff.client.PacienteClient;
import feign.FeignException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/bff/onboarding")
public class BffOnboardingController {

    private final PacienteClient pacienteClient;
    private final DoctorClient doctorClient;

    public BffOnboardingController(PacienteClient pacienteClient, DoctorClient doctorClient) {
        this.pacienteClient = pacienteClient;
        this.doctorClient = doctorClient;
    }

    @GetMapping("/perfil-paciente/{authId}")
    @PreAuthorize("hasAnyAuthority('ROLE_PACIENTE', 'ROLE_MEDICO')")
    public ResponseEntity<?> getPerfilPaciente(@PathVariable Long authId) {
        try {
            return pacienteClient.obtenerPorAuthId(authId);
        } catch (FeignException.NotFound e) {
            return ResponseEntity.notFound().build();
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).body(e.contentUTF8());
        }
    }

    @GetMapping("/perfil-medico/{authId}")
    @PreAuthorize("hasAnyAuthority('ROLE_MEDICO', 'ROLE_PACIENTE')")
    public ResponseEntity<?> getPerfilMedico(@PathVariable Long authId) {
        try {
            return doctorClient.obtenerPorAuthId(authId);
        } catch (FeignException.NotFound e) {
            return ResponseEntity.notFound().build();
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).body(e.contentUTF8());
        }
    }

    @PostMapping("/paciente")
    @PreAuthorize("hasAuthority('ROLE_PACIENTE')")
    public ResponseEntity<?> crearPerfilPaciente(@RequestBody Map<String, Object> payload) {
        try {
            return pacienteClient.crearPaciente(payload);
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).body(e.contentUTF8());
        }
    }

    @PostMapping("/medico")
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
    public ResponseEntity<?> crearPerfilMedico(@RequestBody Map<String, Object> payload) {
        try {
            return doctorClient.crearPerfil(payload);
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).body(e.contentUTF8());
        }
    }
}
