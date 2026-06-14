package com.example.bff.controller;

import com.example.bff.client.AuthClient;
import com.example.bff.client.DoctorClient;
import com.example.bff.client.PacienteClient;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/bff/auth")
public class BffAuthController {

    @Autowired
    private AuthClient authClient;

    @Autowired
    private DoctorClient doctorClient;

    @Autowired
    private PacienteClient pacienteClient;

    /**
     * OPERACIÓN ORQUESTADA DE REGISTRO.
     * 1. Registra al usuario en ms-security.
     * 2. Si es ROLE_MEDICO → crea perfil en ms-doctores.
     * 3. Si es ROLE_PACIENTE → crea perfil en ms-pacientes.
     *
     * El frontend hace UN SOLO request y el BFF orquesta todo internamente.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> payload) {
        // Paso 1: Registrar usuario en Security
        ResponseEntity<?> authResponse;
        try {
            authResponse = authClient.register(payload);
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).body(e.contentUTF8());
        }

        // Extraer el rol del payload
        String rol = (String) payload.getOrDefault("rol", "ROLE_PACIENTE");

        // Paso 2: Según el rol, crear el perfil en el microservicio correspondiente
        if ("ROLE_MEDICO".equals(rol)) {
            try {
                Map<String, Object> doctorPayload = new HashMap<>();
                // El authId viene del response de security (si lo devuelve) o lo omitimos por ahora
                doctorPayload.put("nombre", payload.get("nombre"));
                doctorPayload.put("apellidos", payload.get("apellidos"));
                doctorPayload.put("specialty", payload.get("specialty"));
                doctorPayload.put("registroMedico", payload.get("registroMedico"));

                doctorClient.crearPerfil(doctorPayload);
            } catch (FeignException e) {
                System.err.println("[BFF] Error al crear perfil de doctor: " + e.getMessage());
            }
        } else if ("ROLE_PACIENTE".equals(rol)) {
            try {
                Map<String, Object> pacientePayload = new HashMap<>();
                pacientePayload.put("rut", payload.get("rut"));
                pacientePayload.put("nombre", payload.get("nombre"));
                pacientePayload.put("apellido", payload.get("apellidos"));
                pacientePayload.put("email", payload.get("username"));

                pacienteClient.crearPaciente(pacientePayload);
            } catch (FeignException e) {
                System.err.println("[BFF] Error al crear perfil de paciente: " + e.getMessage());
            }
        }

        return authResponse;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Object payload) {
        try {
            return authClient.login(payload);
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).body(e.contentUTF8());
        }
    }
}