package com.example.bff.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// El "name" DEBE coincidir con el nombre que ms-pacientes usa en Eureka
@FeignClient(name = "ms-pacientes")
public interface PacienteClient {

    // Copiamos la misma firma del endpoint que existe en ms-pacientes
    @GetMapping("/api/pacientes/{id}")
    Object obtenerPacientePorId(@PathVariable("id") Long id);

    // NOTA: Usamos "Object" por rapidez, pero lo ideal es que crees un DTO
    // en el BFF que represente lo que quieres recibir.
}
