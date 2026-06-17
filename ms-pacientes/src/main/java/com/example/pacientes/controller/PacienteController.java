package com.example.pacientes.controller;

import com.example.pacientes.dto.NotificacionDto;
import com.example.pacientes.dto.PacienteResponseDto;
import com.example.pacientes.dto.PacienteRequestDto;
import com.example.pacientes.service.PacienteService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pacientes")
@SecurityRequirement(name = "BearerAuth")
public class PacienteController {

    private final PacienteService pacienteService;

    @Autowired
    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_MEDICO', 'ROLE_PACIENTE')")
    public ResponseEntity<List<PacienteResponseDto>> obtenerTodos() {
        return ResponseEntity.ok(pacienteService.obtenerTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MEDICO', 'ROLE_PACIENTE')")
    public ResponseEntity<PacienteResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pacienteService.obtenerPorId(id));
    }

    // Onboarding profile check: lookup by ms-security authId
    @GetMapping("/auth/{authId}")
    @PreAuthorize("hasAnyAuthority('ROLE_MEDICO', 'ROLE_PACIENTE')")
    public ResponseEntity<PacienteResponseDto> obtenerPorAuthId(@PathVariable Long authId) {
        return ResponseEntity.ok(pacienteService.obtenerPorAuthId(authId));
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
    public ResponseEntity<List<PacienteResponseDto>> obtenerPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(pacienteService.obtenerPorEstado(estado));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_MEDICO', 'ROLE_PACIENTE')")
    public ResponseEntity<PacienteResponseDto> crearPaciente(@Valid @RequestBody PacienteRequestDto dto) {
        return ResponseEntity.status(201).body(pacienteService.crear(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
    public ResponseEntity<PacienteResponseDto> actualizarCompleto(
            @PathVariable Long id,
            @Valid @RequestBody PacienteRequestDto dto) {
        return ResponseEntity.ok(pacienteService.actualizarCompleto(id, dto));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
    public ResponseEntity<PacienteResponseDto> actualizarParcial(
            @PathVariable Long id,
            @RequestBody Map<String, Object> campos) {
        return ResponseEntity.ok(pacienteService.actualizarParcial(id, campos));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pacienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{pacienteId}/notificaciones")
    @PreAuthorize("hasAnyAuthority('ROLE_MEDICO', 'ROLE_PACIENTE')")
    public ResponseEntity<List<NotificacionDto>> obtenerNotificaciones(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(pacienteService.obtenerNotificaciones(pacienteId));
    }
}
