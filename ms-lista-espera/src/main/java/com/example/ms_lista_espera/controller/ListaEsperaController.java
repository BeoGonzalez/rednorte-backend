package com.example.ms_lista_espera.controller;

import com.example.ms_lista_espera.dto.RegistroSolicitudDto;
import com.example.ms_lista_espera.dto.SolicitudResponseDto;
import com.example.ms_lista_espera.service.ListaEsperaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/listas-espera")
@SecurityRequirement(name = "BearerAuth")
public class ListaEsperaController {

    private final ListaEsperaService listaEsperaService;

    @Autowired
    public ListaEsperaController(ListaEsperaService listaEsperaService) {
        this.listaEsperaService = listaEsperaService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
    public ResponseEntity<List<SolicitudResponseDto>> obtenerListaPriorizada() {
        return ResponseEntity.ok(listaEsperaService.obtenerListaPriorizada());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MEDICO', 'ROLE_PACIENTE')")
    public ResponseEntity<SolicitudResponseDto> obtenerPorId(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(listaEsperaService.obtenerPorId(id, token));
    }

    // Token forwarded so the service can call ms-pacientes to enrich patient name/RUT
    @GetMapping("/filtrar")
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
    public ResponseEntity<List<SolicitudResponseDto>> obtenerPorEstadoYTipo(
            @RequestParam String estado,
            @RequestParam String tipoCita,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(listaEsperaService.filtrar(estado, tipoCita, token));
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
    public ResponseEntity<List<SolicitudResponseDto>> obtenerPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(listaEsperaService.obtenerPorEstado(estado));
    }

    @PostMapping("/registro")
    @PreAuthorize("hasAnyAuthority('ROLE_MEDICO', 'ROLE_PACIENTE')")
    public ResponseEntity<SolicitudResponseDto> registrar(
            @RequestBody RegistroSolicitudDto dto,
            @RequestHeader("Authorization") String token) {
        SolicitudResponseDto response = listaEsperaService.registrarSolicitud(dto, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
    public ResponseEntity<SolicitudResponseDto> actualizarCompleto(
            @PathVariable Long id,
            @RequestBody RegistroSolicitudDto dto) {
        return ResponseEntity.ok(listaEsperaService.actualizarCompleto(id, dto));
    }

    // Doctor decision endpoint: body = { "estado": "ACEPTADA" | "RECHAZADA", "doctorId": 1 }
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
    public ResponseEntity<SolicitudResponseDto> actualizarParcial(
            @PathVariable Long id,
            @RequestBody Map<String, Object> campos) {
        return ResponseEntity.ok(listaEsperaService.actualizarParcial(id, campos));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        listaEsperaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
