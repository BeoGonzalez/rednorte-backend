package com.example.bff.controller;

import com.example.bff.client.ListaEsperaClient;
import feign.FeignException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador Backend For Frontend (BFF) que gestiona las operaciones relacionadas con las listas de espera.
 * <p>
 * Este controlador actúa como un proxy seguro hacia el microservicio de listas de espera ({@code ms-lista-espera}).
 * Su propósito es abstraer la complejidad del backend y proporcionar al frontend puntos de entrada únicos
 * para la gestión de solicitudes, filtrado y actualización de estados de atención.
 * </p>
 */
@RestController
@RequestMapping("/bff/lista-espera")
@SecurityRequirement(name = "BearerAuth")
public class BffListaEsperaController {

    @Autowired
    private ListaEsperaClient listaEsperaClient;

    /**
     * Obtiene la lista priorizada de solicitudes para el Dashboard general.
     * <p>
     * Reenvía la petición al microservicio correspondiente y maneja las excepciones
     * de red en caso de que el servicio no esté disponible.
     * </p>
     * <p><b>Control de Acceso:</b> Restringido exclusivamente a 'ROLE_MEDICO'.</p>
     *
     * @return {@link ResponseEntity} con la lista priorizada o el mensaje de error del backend.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
    public ResponseEntity<?> obtenerListaPriorizada() {
        try {
            return listaEsperaClient.obtenerListaPriorizada();
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).body(e.contentUTF8());
        }
    }

    /**
     * Obtiene el detalle de una solicitud específica por su identificador.
     * <p>
     * Este método propaga el token de autorización, lo cual es necesario para que el
     * microservicio de destino pueda enriquecer los datos (por ejemplo, obtener
     * detalles adicionales del paciente).
     * </p>
     * <p><b>Control de Acceso:</b> Permitido para 'ROLE_MEDICO' y 'ROLE_PACIENTE'.</p>
     *
     * @param id    El identificador de la solicitud.
     * @param token El token JWT de autorización (requerido para seguridad y enriquecimiento).
     * @return {@link ResponseEntity} con los detalles de la solicitud o un error de comunicación.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MEDICO', 'ROLE_PACIENTE')")
    public ResponseEntity<?> obtenerPorId(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        try {
            return listaEsperaClient.obtenerPorId(id, token);
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).body(e.contentUTF8());
        }
    }

    /**
     * Registra una nueva solicitud de atención en el sistema.
     * <p><b>Control de Acceso:</b> Permitido para 'ROLE_MEDICO' y 'ROLE_PACIENTE'.</p>
     *
     * @param request Datos de la solicitud.
     * @param token   El token de autorización del usuario que realiza la solicitud.
     * @return {@link ResponseEntity} con el resultado de la creación o un error.
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_MEDICO', 'ROLE_PACIENTE')")
    public ResponseEntity<?> registrarSolicitud(
            @RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String token) {
        try {
            return listaEsperaClient.registrarSolicitud(request, token);
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).body(e.contentUTF8());
        }
    }

    /**
     * Filtra las solicitudes de atención basándose en su estado y tipo de cita.
     * <p>
     * Diseñado principalmente para alimentar el panel de gestión médica.
     * </p>
     * <p><b>Control de Acceso:</b> Restringido exclusivamente a 'ROLE_MEDICO'.</p>
     *
     * @param estado   El estado de la solicitud (ej. PENDIENTE, ATENDIDO).
     * @param tipoCita El tipo de cita a filtrar.
     * @return {@link ResponseEntity} con la lista filtrada de solicitudes.
     */
    @GetMapping("/filtrar")
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
    public ResponseEntity<?> filtrar(
            @RequestParam String estado,
            @RequestParam String tipoCita) {
        try {
            return listaEsperaClient.filtrar(estado, tipoCita);
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).body(e.contentUTF8());
        }
    }

    /**
     * Obtiene todas las solicitudes que se encuentran en un estado específico.
     * <p><b>Control de Acceso:</b> Restringido exclusivamente a 'ROLE_MEDICO'.</p>
     *
     * @param estado El estado por el cual filtrar las solicitudes.
     * @return {@link ResponseEntity} con la lista de solicitudes.
     */
    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
    public ResponseEntity<?> obtenerPorEstado(@PathVariable String estado) {
        try {
            return listaEsperaClient.obtenerPorEstado(estado);
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).body(e.contentUTF8());
        }
    }

    /**
     * Realiza una actualización parcial de una solicitud de atención (ej: cambio de estado).
     * <p><b>Control de Acceso:</b> Restringido exclusivamente a 'ROLE_MEDICO' para evitar que pacientes modifiquen su propia prioridad.</p>
     *
     * @param id     El ID de la solicitud a actualizar.
     * @param campos Un mapa conteniendo los campos a modificar.
     * @return {@link ResponseEntity} con el resultado de la actualización.
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
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