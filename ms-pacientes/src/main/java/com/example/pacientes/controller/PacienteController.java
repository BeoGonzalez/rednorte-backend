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

/**
 * Controlador REST encargado de gestionar las operaciones CRUD de la entidad Paciente.
 * <p>
 * Este controlador expone los endpoints necesarios para el registro, consulta, actualización
 * y eliminación de pacientes. Implementa controles de acceso basados en roles (RBAC)
 * mediante Spring Security, permitiendo diferentes niveles de acceso según el tipo de usuario.
 * </p>
 */
@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    /**
     * Constructor para la inyección de dependencias.
     *
     * @param pacienteService Servicio que encapsula la lógica de negocio de los pacientes.
     */
    @Autowired
    public PacienteController(PacienteService pacienteService){
        this.pacienteService = pacienteService;
    }

    /**
     * Recupera una lista completa de todos los pacientes registrados en el sistema.
     * <p>
     * Accesible tanto por médicos como por pacientes, facilitando la integración
     * en el Portal de Pacientes.
     * </p>
     *
     * @return {@link ResponseEntity} con la lista de {@link PacienteResponseDto}.
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_MEDICO', 'ROLE_PACIENTE')")
    public ResponseEntity<List<PacienteResponseDto>> obtenerTodos() {
        return ResponseEntity.ok(pacienteService.obtenerTodos());
    }

    /**
     * Obtiene el perfil de un paciente específico mediante su identificador único.
     * <p>
     * Disponible tanto para médicos (para consulta clínica) como para el propio paciente.
     * </p>
     *
     * @param id Identificador único del paciente.
     * @return {@link ResponseEntity} con el {@link PacienteResponseDto} solicitado.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MEDICO', 'ROLE_PACIENTE')")
    public ResponseEntity<PacienteResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pacienteService.obtenerPorId(id));
    }

    /**
     * Filtra a los pacientes según su estado actual (ej. ACTIVO, INACTIVO).
     * <p>
     * Endpoint restringido exclusivamente a usuarios con rol de MÉDICO.
     * </p>
     *
     * @param estado Estado por el cual se desea filtrar la lista de pacientes.
     * @return {@link ResponseEntity} con la lista de pacientes que coinciden con el criterio.
     */
    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
    public ResponseEntity<List<PacienteResponseDto>> obtenerPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(pacienteService.obtenerPorEstado(estado));
    }

    /**
     * Registra un nuevo paciente en el sistema.
     * <p>
     * Puede ser invocado por médicos para crear nuevas fichas o por el sistema de
     * autenticación durante el proceso de registro de un nuevo usuario.
     * </p>
     *
     * @param dto Objeto validado con la información del paciente.
     * @return {@link ResponseEntity} con el paciente creado y estado HTTP 201 (Created).
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_MEDICO', 'ROLE_PACIENTE')")
    public ResponseEntity<PacienteResponseDto> crearPaciente(@Valid @RequestBody PacienteRequestDto dto) {
        return ResponseEntity.status(201).body(pacienteService.crear(dto));
    }

    /**
     * Realiza una actualización completa de la información de un paciente existente.
     * <p>
     * Operación restringida exclusivamente a usuarios con rol de MÉDICO.
     * </p>
     *
     * @param id  ID del paciente a actualizar.
     * @param dto Objeto con los nuevos datos profesionales y personales.
     * @return {@link ResponseEntity} con el paciente actualizado.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
    public ResponseEntity<PacienteResponseDto> actualizarCompleto(
            @PathVariable Long id,
            @Valid @RequestBody PacienteRequestDto dto) {
        return ResponseEntity.ok(pacienteService.actualizarCompleto(id, dto));
    }

    /**
     * Realiza una actualización parcial de campos específicos de un paciente.
     * <p>
     * Operación restringida exclusivamente a usuarios con rol de MÉDICO.
     * </p>
     *
     * @param id     ID del paciente a modificar.
     * @param campos Mapa que contiene los pares clave-valor de los campos a actualizar.
     * @return {@link ResponseEntity} con el paciente reflejando los cambios.
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
    public ResponseEntity<PacienteResponseDto> actualizarParcial(
            @PathVariable Long id,
            @RequestBody Map<String, Object> campos) {
        return ResponseEntity.ok(pacienteService.actualizarParcial(id, campos));
    }

    /**
     * Elimina permanentemente el registro de un paciente del sistema.
     * <p>
     * Operación restringida exclusivamente a usuarios con rol de MÉDICO.
     * </p>
     *
     * @param id ID del paciente a eliminar.
     * @return {@link ResponseEntity} vacío con estado 204 (No Content).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pacienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}