package com.example.bff.controller;

import com.example.bff.client.ListaEsperaClient;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/bff/lista-espera")
public class BffListaEsperaController {

    @Autowired
    private ListaEsperaClient listaEsperaClient;

    // Lista priorizada (dashboard general)
    @GetMapping
    public ResponseEntity<?> obtenerListaPriorizada() {
        try {
            return listaEsperaClient.obtenerListaPriorizada();
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).body(e.contentUTF8());
        }
    }

    // Detalle de una solicitud (requiere token para enriquecer con datos del paciente)
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        try {
            return listaEsperaClient.obtenerPorId(id, token);
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).body(e.contentUTF8());
        }
    }

    // Crear solicitud de atención
    @PostMapping
    public ResponseEntity<?> registrarSolicitud(
            @RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String token) {
        try {
            return listaEsperaClient.registrarSolicitud(request, token);
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).body(e.contentUTF8());
        }
    }

    // Filtrar por estado y tipo (panel del médico)
    @GetMapping("/filtrar")
    public ResponseEntity<?> filtrar(
            @RequestParam String estado,
            @RequestParam String tipoCita) {
        try {
            return listaEsperaClient.filtrar(estado, tipoCita);
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).body(e.contentUTF8());
        }
    }

    // Por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> obtenerPorEstado(@PathVariable String estado) {
        try {
            return listaEsperaClient.obtenerPorEstado(estado);
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).body(e.contentUTF8());
        }
    }

    // Actualizar parcialmente (ej: cambiar estado)
    @PatchMapping("/{id}")
    public ResponseEntity<?> actualizarParcial(
            @PathVariable Long id,
            @RequestBody Map<String, Object> campos) {
        try {
            return listaEsperaClient.actualizarParcial(id, campos);
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).body(e.contentUTF8());
        }
    }
}
