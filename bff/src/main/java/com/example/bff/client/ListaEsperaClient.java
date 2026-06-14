package com.example.bff.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "ms-lista-espera")
public interface ListaEsperaClient {

    @GetMapping("/api/listas-espera")
    ResponseEntity<List<?>> obtenerListaPriorizada();

    @GetMapping("/api/listas-espera/{id}")
    ResponseEntity<?> obtenerPorId(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String token);

    @PostMapping("/api/listas-espera/registro")
    ResponseEntity<?> registrarSolicitud(
            @RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String token);

    @GetMapping("/api/listas-espera/estado/{estado}")
    ResponseEntity<List<?>> obtenerPorEstado(@PathVariable("estado") String estado);

    @GetMapping("/api/listas-espera/filtrar")
    ResponseEntity<List<?>> filtrar(
            @RequestParam("estado") String estado,
            @RequestParam("tipoCita") String tipoCita);

    @PatchMapping("/api/listas-espera/{id}")
    ResponseEntity<?> actualizarParcial(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Object> campos);
}
