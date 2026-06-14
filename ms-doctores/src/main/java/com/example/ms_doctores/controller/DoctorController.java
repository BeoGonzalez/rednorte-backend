package com.example.ms_doctores.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.example.ms_doctores.dto.DoctorRequestDTO;
import com.example.ms_doctores.entity.Doctor;
import com.example.ms_doctores.service.DoctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctores")
@Tag(name = "Gestión de Perfiles Médicos", description = "Endpoints para el registro y consulta de doctores")
@SecurityRequirement(name = "BearerAuth") // 🟢 Aplica el candado de seguridad a todas las rutas de este controlador
public class DoctorController {

    private final DoctorService doctorService;

    // Inyección de dependencias pura por constructor
    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping("/perfil")
    @Operation(
            summary = "Crear un nuevo perfil médico",
            description = "Registra los datos profesionales de un doctor (nombre, especialidad, registro médico) y lo vincula a su Auth ID."
    )
    public ResponseEntity<Doctor> crearPerfilDoctor(@RequestBody DoctorRequestDTO requestDTO) {
        Doctor nuevoDoctor = doctorService.registrarDoctor(requestDTO);
        return ResponseEntity.ok(nuevoDoctor);
    }

    @GetMapping("/auth/{authId}")
    @Operation(
            summary = "Buscar doctor por Auth ID",
            description = "Obtiene la información pública de un médico utilizando el identificador de su cuenta de seguridad central."
    )
    public ResponseEntity<Doctor> obtenerDoctorPorAuthId(@PathVariable Long authId) {
        return doctorService.buscarPorAuthId(authId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}