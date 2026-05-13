package com.example.pacientes.controller;

import com.example.pacientes.dto.PacienteResponseDto;
import com.example.pacientes.dto.PacienteRequestDto;
import com.example.pacientes.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public ResponseEntity<List<PacienteResponseDto>> obtenerTodos() {
        return ResponseEntity.ok(pacienteService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pacienteService.obtenerPorId(id));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PacienteResponseDto>> obtenerPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(pacienteService.obtenerPorEstado(estado));
    }

    // UPDATE FULL (Reemplazar todo el registro)
    @PutMapping("/{id}")
    public ResponseEntity<PacienteResponseDto> actualizarCompleto(
            @PathVariable Long id,
            @Valid @RequestBody PacienteRequestDto dto) {
        return ResponseEntity.ok(pacienteService.actualizarCompleto(id, dto));
    }

    // UPDATE PARTIAL (Actualizar solo algunos campos)
    @PatchMapping("/{id}")
    public ResponseEntity<PacienteResponseDto> actualizarParcial(
            @PathVariable Long id,
            @RequestBody Map<String, Object> campos) {
        return ResponseEntity.ok(pacienteService.actualizarParcial(id, campos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pacienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}