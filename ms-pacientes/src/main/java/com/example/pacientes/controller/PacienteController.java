package com.example.pacientes.controller;

import com.example.pacientes.dto.PacienteResponseDto;
import com.example.pacientes.dto.PacienteRequestDto;
import com.example.pacientes.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {
    private final PacienteService pacienteService;

    @Autowired
    public PacienteController(PacienteService pacienteService){
        this.pacienteService = pacienteService;
    }

    /**
     * MODIFICADO: Ahora permite que tanto médicos como pacientes accedan.
     * Esto soluciona el error 403 al entrar al Portal de Pacientes.
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_MEDICO', 'ROLE_PACIENTE')")
    public ResponseEntity<List<PacienteResponseDto>> obtenerTodos() {
        return ResponseEntity.ok(pacienteService.obtenerTodos());
    }

    // Médicos y Pacientes pueden consultar por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MEDICO', 'ROLE_PACIENTE')")
    public ResponseEntity<PacienteResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pacienteService.obtenerPorId(id));
    }

    // Solo los MÉDICOS pueden buscar por estados
    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
    public ResponseEntity<List<PacienteResponseDto>> obtenerPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(pacienteService.obtenerPorEstado(estado));
    }

    // Solo los MÉDICOS pueden crear nuevos pacientes
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
    public ResponseEntity<PacienteResponseDto> crearPaciente(@Valid @RequestBody PacienteRequestDto dto) {
        return ResponseEntity.status(201).body(pacienteService.crear(dto));
    }

    // Solo los MÉDICOS pueden hacer actualizaciones completas
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
    public ResponseEntity<PacienteResponseDto> actualizarCompleto(
            @PathVariable Long id,
            @Valid @RequestBody PacienteRequestDto dto) {
        return ResponseEntity.ok(pacienteService.actualizarCompleto(id, dto));
    }

    // Solo los MÉDICOS pueden actualizar campos específicos
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
    public ResponseEntity<PacienteResponseDto> actualizarParcial(
            @PathVariable Long id,
            @RequestBody Map<String, Object> campos) {
        return ResponseEntity.ok(pacienteService.actualizarParcial(id, campos));
    }

    // Solo los MÉDICOS pueden eliminar registros
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pacienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}