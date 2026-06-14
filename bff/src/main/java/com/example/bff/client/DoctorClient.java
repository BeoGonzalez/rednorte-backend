package com.example.bff.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "ms-doctores")
public interface DoctorClient {

    @PostMapping("/api/doctores/perfil")
    ResponseEntity<?> crearPerfil(@RequestBody Map<String, Object> request);

    @GetMapping("/api/doctores/auth/{authId}")
    ResponseEntity<?> obtenerPorAuthId(@PathVariable("authId") Long authId);

    @GetMapping("/api/doctores/especialidad/{especialidad}")
    ResponseEntity<List<?>> obtenerPorEspecialidad(@PathVariable("especialidad") String especialidad);
}
