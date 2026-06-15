package com.example.bff.controller;

import com.example.bff.client.DoctorClient;
import com.example.bff.client.PacienteClient;
import feign.FeignException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador de Backend For Frontend (BFF) encargado de la agregación de datos para el Dashboard principal.
 * <p>
 * Este componente orquesta múltiples llamadas a microservicios backend (Doctores y Pacientes)
 * para consolidar una respuesta única y coherente para el Frontend.
 * Su diseño prioriza la resiliencia mediante tolerancia a fallos, permitiendo que el
 * Dashboard se renderice parcialmente aunque uno de los servicios subyacentes no esté disponible.
 * </p>
 */
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/bff/dashboard")
public class BffDashboardController {

    private final PacienteClient pacienteClient;
    private final DoctorClient doctorClient;

    /**
     * Constructor para la inyección de dependencias de los clientes Feign.
     *
     * @param pacienteClient Cliente para comunicarse con el microservicio de pacientes.
     * @param doctorClient   Cliente para comunicarse con el microservicio de doctores.
     */
    public BffDashboardController(PacienteClient pacienteClient, DoctorClient doctorClient) {
        this.pacienteClient = pacienteClient;
        this.doctorClient = doctorClient;
    }

    /**
     * Genera un objeto de respuesta consolidado que contiene la información esencial para el Dashboard.
     * <p>
     * La operación sigue el patrón de <b>orquestación de datos</b>:
     * <ol>
     * <li>Consulta el perfil del doctor asociado al {@code authId}.</li>
     * <li>Consulta la lista completa de pacientes registrados.</li>
     * </ol>
     * En caso de error en alguna llamada (vía Feign), se captura la {@link FeignException}
     * y se añade una entrada de alerta en la respuesta, garantizando que el usuario
     * visualice la información disponible en lugar de un error general del sistema (500).
     * </p>
     *
     * @param authId El identificador único del usuario (médico) para consultar su contexto.
     * @return {@link ResponseEntity} con un mapa que contiene los perfiles orquestados y sus alertas si ocurrieron errores.
     */
    @GetMapping("/{authId}")
    public ResponseEntity<Map<String, Object>> obtenerDashboardCompleto(@PathVariable Long authId) {
        Map<String, Object> dashboardResponse = new HashMap<>();

        // 1. Orquestación: Ir a buscar el Perfil del Doctor
        try {
            ResponseEntity<?> doctorResponse = doctorClient.obtenerPorAuthId(authId);
            dashboardResponse.put("perfilDoctor", doctorResponse.getBody());
        } catch (FeignException e) {
            // Tolerancia a fallos: Si el microservicio de doctores cae, el dashboard no explota
            dashboardResponse.put("perfilDoctor", null);
            dashboardResponse.put("alertaDoctor", "No se pudo cargar el perfil del médico");
        }

        // 2. Orquestación: Ir a buscar los Pacientes
        try {
            ResponseEntity<?> pacientesResponse = pacienteClient.getPacientes();
            dashboardResponse.put("listaPacientes", pacientesResponse.getBody());
        } catch (FeignException e) {
            dashboardResponse.put("listaPacientes", null);
            dashboardResponse.put("alertaPacientes", "Los pacientes no están disponibles en este momento");
        }

        // 3. Devolver el JSON perfectamente ensamblado
        return ResponseEntity.ok(dashboardResponse);
    }
}