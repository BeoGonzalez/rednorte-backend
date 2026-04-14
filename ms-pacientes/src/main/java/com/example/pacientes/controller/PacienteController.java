package com.example.pacientes.controller;

import com.example.pacientes.dto.LoginPacienteDto;
import com.example.pacientes.dto.PacienteResponseDto;
import com.example.pacientes.dto.RegistroPacienteDto;
import com.example.pacientes.dto.TokenResponseDto;
import com.example.pacientes.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    // 2. READ ALL (Obtener todos)
    @GetMapping
    public ResponseEntity<List<PacienteResponseDto>> obtenerTodos() {
        List<PacienteResponseDto> pacientes = pacienteService.obtenerTodos();
        return ResponseEntity.ok(pacientes);
    }

    // 3. READ BY ID (Obtener uno específico)
    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponseDto> obtenerPorId(@PathVariable Long id) {
        PacienteResponseDto paciente = pacienteService.obtenerPorId(id);
        return ResponseEntity.ok(paciente);
    }

    // 4. READ BY ESTADO (Filtrar por estado - Excelente práctica para Soft Deletes)
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PacienteResponseDto>> obtenerPorEstado(@PathVariable String estado) {
        List<PacienteResponseDto> pacientes = pacienteService.obtenerPorEstado(estado);
        return ResponseEntity.ok(pacientes);
    }

    @PostMapping("/registro")
    public ResponseEntity<PacienteResponseDto> registrar(@Valid @RequestBody RegistroPacienteDto dto) {
        PacienteResponseDto response = pacienteService.registrarPaciente(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginPacienteDto dto) {
        TokenResponseDto token = pacienteService.login(dto);
        return ResponseEntity.ok(token);
    }

    // 5. UPDATE FULL (Reemplazar todo el registro)
    @PutMapping("/{id}")
    public ResponseEntity<PacienteResponseDto> actualizarCompleto(
            @PathVariable Long id,
            @Valid @RequestBody RegistroPacienteDto dto) {
        PacienteResponseDto actualizado = pacienteService.actualizarCompleto(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    // 6. UPDATE PARTIAL (Actualizar solo algunos campos)
    @PatchMapping("/{id}")
    public ResponseEntity<PacienteResponseDto> actualizarParcial(
            @PathVariable Long id,
            @RequestBody Map<String, Object> campos) {
        PacienteResponseDto actualizado = pacienteService.actualizarParcial(id, campos);
        return ResponseEntity.ok(actualizado);
    }

    // 7. DELETE (Eliminar un registro)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pacienteService.eliminar(id);
        // 204 No Content es el estándar Senior para indicar que se borró y no hay nada que devolver
        return ResponseEntity.noContent().build();
    }
}
