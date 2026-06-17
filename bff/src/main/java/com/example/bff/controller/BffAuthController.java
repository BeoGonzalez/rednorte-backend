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

/**
 * Controlador de Backend For Frontend (BFF) para el proceso de autenticación.
 * <p>
 * Este controlador actúa como un orquestador para las operaciones de registro y login.
 * Su responsabilidad principal es unificar las llamadas a múltiples microservicios
 * (seguridad, doctores y pacientes) en una sola petición desde el frontend,
 * simplificando la lógica de integración para los clientes.
 * </p>
 */
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
     * Ejecuta una operación orquestada de registro de usuario.
     * <p>
     * El flujo de trabajo es el siguiente:
     * <ol>
     * <li>Registra al usuario en el microservicio de seguridad ({@code ms-security}).</li>
     * <li>Si el registro es exitoso y el rol es 'ROLE_MEDICO', crea un perfil en el microservicio de doctores.</li>
     * <li>Si el registro es exitoso y el rol es 'ROLE_PACIENTE', crea un perfil en el microservicio de pacientes.</li>
     * </ol>
     * Las fallas en la creación del perfil (paso 2 o 3) se registran en los logs, pero no interrumpen
     * la respuesta exitosa de autenticación.
     * </p>
     *
     * @param payload Mapa con los datos del usuario (rol, nombre, apellidos, especialidad, RUT, etc.).
     * @return ResponseEntity con la respuesta del microservicio de seguridad.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> payload) {
        ResponseEntity<?> authResponse;
        try {
            authResponse = authClient.register(payload);
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).body(e.contentUTF8());
        }

        // Extraer authId de la respuesta de security
        Long authId = null;
        if (authResponse.getBody() instanceof Map<?, ?> body) {
            Object rawId = body.get("authId");
            if (rawId instanceof Number number) {
                authId = number.longValue();
            }
        }

        String rol = (String) payload.getOrDefault("rol", "ROLE_PACIENTE");

        if ("ROLE_MEDICO".equals(rol) && authId != null) {
            try {
                Map<String, Object> doctorPayload = new HashMap<>();
                doctorPayload.put("authId", authId);
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
                if (authId != null) pacientePayload.put("authId", authId);
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

    /**
     * Proxy para la operación de inicio de sesión.
     * <p>
     * Reenvía las credenciales al microservicio de seguridad y retorna el token o la respuesta de error correspondiente.
     * </p>
     *
     * @param payload Credenciales del usuario (ej. nombre de usuario y contraseña).
     * @return ResponseEntity con la respuesta del microservicio de autenticación.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Object payload) {
        try {
            return authClient.login(payload);
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).body(e.contentUTF8());
        }
    }
}