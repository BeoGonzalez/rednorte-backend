package com.example.ms_doctores.controller;

import com.example.ms_doctores.dto.DoctorRequestDTO;
import com.example.ms_doctores.entity.Doctor;
import com.example.ms_doctores.service.DoctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctores")
public class DoctorController {

    private final DoctorService doctorService;

    // Inyección de dependencias pura por constructor
    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    // El API Gateway se asegurará perimetralmente de que solo entren usuarios con ROLE_MEDICO
    @PostMapping("/perfil")
    public ResponseEntity<Doctor> crearPerfilDoctor(@RequestBody DoctorRequestDTO requestDTO) {
        Doctor nuevoDoctor = doctorService.registrarDoctor(requestDTO);
        return ResponseEntity.ok(nuevoDoctor);
    }

    // El API Gateway permitirá el paso tanto a médicos como a pacientes
    @GetMapping("/auth/{authId}")
    public ResponseEntity<Doctor> obtenerDoctorPorAuthId(@PathVariable Long authId) {
        return doctorService.buscarPorAuthId(authId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}