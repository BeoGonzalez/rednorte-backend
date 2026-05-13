package com.example.ms_lista_espera.controller;

import com.example.ms_lista_espera.dto.RegistroSolicitudDto;
import com.example.ms_lista_espera.dto.SolicitudResponseDto;
import com.example.ms_lista_espera.service.ListaEsperaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // 🔴 Importación añadida
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/listas-espera")
public class ListaEsperaController {

    private final ListaEsperaService listaEsperaService;

    @Autowired
    public ListaEsperaController(ListaEsperaService listaEsperaService) {
        this.listaEsperaService = listaEsperaService;
    }

    // 1. GET ALL (La lista principal ordenada con el algoritmo)
    @GetMapping
    public ResponseEntity<List<SolicitudResponseDto>> obtenerListaPriorizada() {
        return ResponseEntity.ok(listaEsperaService.obtenerListaPriorizada());
    }

    // 2. GET BY ID (Pide el token para ir a buscar el nombre a ms-pacientes)
    @GetMapping("/{id}")
    public ResponseEntity<SolicitudResponseDto> obtenerPorId(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(listaEsperaService.obtenerPorId(id, token));
    }

    // 🔴 Solo los médicos pueden ver la lista de espera
    @GetMapping("/filtrar")
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
    public ResponseEntity<List<SolicitudResponseDto>> obtenerPorEstadoYTipo( // 🔴 DTO Corregido
                                                                             @RequestParam String estado,     // Ej: "BUSCANDO_CITA"
                                                                             @RequestParam String tipoCita) { // Ej: "CARDIOLOGIA"

        return ResponseEntity.ok(listaEsperaService.filtrar(estado, tipoCita));
    }

    // 3. GET BY ESTADO
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<SolicitudResponseDto>> obtenerPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(listaEsperaService.obtenerPorEstado(estado));
    }

    // 4. POST
    @PostMapping("/registro")
    public ResponseEntity<SolicitudResponseDto> registrar(
            @RequestBody RegistroSolicitudDto dto,
            @RequestHeader("Authorization") String token) {
        SolicitudResponseDto response = listaEsperaService.registrarSolicitud(dto, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 5. PUT
    @PutMapping("/{id}")
    public ResponseEntity<SolicitudResponseDto> actualizarCompleto(
            @PathVariable Long id,
            @RequestBody RegistroSolicitudDto dto) {
        return ResponseEntity.ok(listaEsperaService.actualizarCompleto(id, dto));
    }

    // 6. PATCH (Ideal para que un médico cambie el estado de PENDIENTE a EN_PROCESO)
    @PatchMapping("/{id}")
    public ResponseEntity<SolicitudResponseDto> actualizarParcial(
            @PathVariable Long id,
            @RequestBody Map<String, Object> campos) {
        return ResponseEntity.ok(listaEsperaService.actualizarParcial(id, campos));
    }

    // 7. DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        listaEsperaService.eliminar(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}