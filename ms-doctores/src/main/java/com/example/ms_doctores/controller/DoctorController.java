package com.example.ms_doctores.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.example.ms_doctores.dto.DoctorRequestDTO;
import com.example.ms_doctores.entity.Doctor;
import com.example.ms_doctores.exception.ResourceNotFoundException;
import com.example.ms_doctores.service.DoctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST encargado de gestionar el ciclo de vida de los perfiles médicos.
 * <p>
 * Este controlador expone los endpoints necesarios para el registro y consulta de doctores,
 * sirviendo como puente entre las peticiones externas y la lógica de negocio contenida
 * en {@link DoctorService}. Todas las operaciones están protegidas bajo el esquema de
 * seguridad 'BearerAuth'.
 * </p>
 */
@RestController
@RequestMapping("/api/doctores")
@Tag(name = "Gestión de Perfiles Médicos", description = "Endpoints para el registro y consulta de doctores")
@SecurityRequirement(name = "BearerAuth")
public class DoctorController {

    private final DoctorService doctorService;

    /**
     * Constructor para la inyección de dependencias por constructor.
     *
     * @param doctorService Servicio de dominio que contiene la lógica de negocio para doctores.
     */
    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    /**
     * Registra un nuevo perfil profesional para un médico.
     * <p>
     * Este endpoint toma los datos del DTO proporcionado, valida la existencia del médico
     * y crea el vínculo entre el usuario de seguridad (Auth ID) y su perfil clínico.
     * </p>
     *
     * @param requestDTO Objeto de transferencia de datos con la información profesional (nombre, especialidad, registro).
     * @return {@link ResponseEntity} conteniendo la entidad {@link Doctor} recién creada.
     */
    @PostMapping("/perfil")
    @Operation(
            summary = "Crear un nuevo perfil médico",
            description = "Registra los datos profesionales de un doctor (nombre, especialidad, registro médico) y lo vincula a su Auth ID."
    )
    public ResponseEntity<Doctor> crearPerfilDoctor(@RequestBody DoctorRequestDTO requestDTO) {
        Doctor nuevoDoctor = doctorService.registrarDoctor(requestDTO);
        return ResponseEntity.ok(nuevoDoctor);
    }

    /**
     * Busca la información de un doctor basándose en su ID de autenticación central.
     * <p>
     * Ideal para consultas de contexto donde se conoce el ID de usuario del sistema de seguridad
     * pero se requiere obtener los detalles del perfil profesional clínico.
     * </p>
     *
     * @param authId El identificador único proveniente del microservicio de autenticación.
     * @return {@link ResponseEntity} con la entidad {@link Doctor} encontrada, o 404 Not Found si no existe.
     */
    @GetMapping("/auth/{authId}")
    @Operation(
            summary = "Buscar doctor por Auth ID",
            description = "Obtiene la información pública de un médico utilizando el identificador de su cuenta de seguridad central."
    )
    public ResponseEntity<Doctor> obtenerDoctorPorAuthId(@PathVariable Long authId) {
        Doctor doctor = doctorService.buscarPorAuthId(authId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor no encontrado con Auth ID: " + authId));
        return ResponseEntity.ok(doctor);
    }
}
