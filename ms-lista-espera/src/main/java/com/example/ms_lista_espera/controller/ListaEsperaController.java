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

/**
 * Controlador REST encargado de gestionar el ciclo de vida de las solicitudes en la lista de espera.
 * <p>
 * Este servicio permite el registro, consulta (filtrada y priorizada), actualización y eliminación
 * de solicitudes de atención médica. Actúa como orquestador entre el frontend y el servicio
 * {@link ListaEsperaService}, integrando seguridad basada en tokens y controles de acceso por roles.
 * </p>
 */
@RestController
@RequestMapping("/api/listas-espera")
@SecurityRequirement(name = "BearerAuth")
public class ListaEsperaController {

    private final ListaEsperaService listaEsperaService;

    @Autowired
    public ListaEsperaController(ListaEsperaService listaEsperaService) {
        this.listaEsperaService = listaEsperaService;
    }

    /**
     * Obtiene la lista completa de solicitudes, priorizada algorítmicamente.
     * <p>
     * Este endpoint se utiliza generalmente para alimentar el Dashboard principal
     * donde se visualiza el orden de atención de los pacientes.
     * </p>
     *
     * @return {@link ResponseEntity} con la lista de solicitudes ordenadas por prioridad.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
    public ResponseEntity<List<SolicitudResponseDto>> obtenerListaPriorizada() {
        return ResponseEntity.ok(listaEsperaService.obtenerListaPriorizada());
    }

    /**
     * Recupera el detalle de una solicitud específica por su ID.
     * <p>
     * Requiere el token de autorización en la cabecera, ya que el servicio interno
     * utiliza este token para enriquecer los datos de la solicitud (por ejemplo,
     * consultando información del paciente en el microservicio correspondiente).
     * </p>
     *
     * @param id    Identificador único de la solicitud.
     * @param token Token JWT de autorización para la consulta segura.
     * @return {@link ResponseEntity} con el detalle de la solicitud.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MEDICO', 'ROLE_PACIENTE')")
    public ResponseEntity<SolicitudResponseDto> obtenerPorId(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(listaEsperaService.obtenerPorId(id, token));
    }

    /**
     * Filtra solicitudes según su estado y el tipo de especialidad médica.
     * <p>
     * Este endpoint está restringido exclusivamente a usuarios con rol de MÉDICO
     * para facilitar la gestión de su panel de atención.
     * </p>
     *
     * @param estado   El estado de la solicitud (ej. "BUSCANDO_CITA").
     * @param tipoCita El tipo de especialidad o tipo de atención (ej. "CARDIOLOGIA").
     * @return {@link ResponseEntity} con la lista filtrada de solicitudes.
     */
    @GetMapping("/filtrar")
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
    public ResponseEntity<List<SolicitudResponseDto>> obtenerPorEstadoYTipo(
            @RequestParam String estado,
            @RequestParam String tipoCita) {

        return ResponseEntity.ok(listaEsperaService.filtrar(estado, tipoCita));
    }

    /**
     * Obtiene todas las solicitudes que se encuentran en un estado determinado.
     *
     * @param estado El estado a filtrar.
     * @return {@link ResponseEntity} con la lista de solicitudes encontradas.
     */
    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
    public ResponseEntity<List<SolicitudResponseDto>> obtenerPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(listaEsperaService.obtenerPorEstado(estado));
    }

    /**
     * Registra una nueva solicitud de atención en el sistema.
     * <p>
     * Requiere el token del usuario para asociar correctamente la solicitud al perfil
     * del paciente que realiza la petición.
     * </p>
     *
     * @param dto   Objeto con los datos de la solicitud (paciente, motivo, etc.).
     * @param token Token JWT de autorización del usuario.
     * @return {@link ResponseEntity} con la solicitud creada y estado HTTP 201 (Created).
     */
    @PostMapping("/registro")
    @PreAuthorize("hasAnyAuthority('ROLE_MEDICO', 'ROLE_PACIENTE')")
    public ResponseEntity<SolicitudResponseDto> registrar(
            @RequestBody RegistroSolicitudDto dto,
            @RequestHeader("Authorization") String token) {
        SolicitudResponseDto response = listaEsperaService.registrarSolicitud(dto, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Realiza una actualización completa de los datos de una solicitud.
     *
     * @param id  Identificador de la solicitud.
     * @param dto Nuevos datos para la solicitud.
     * @return {@link ResponseEntity} con la solicitud actualizada.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
    public ResponseEntity<SolicitudResponseDto> actualizarCompleto(
            @PathVariable Long id,
            @RequestBody RegistroSolicitudDto dto) {
        return ResponseEntity.ok(listaEsperaService.actualizarCompleto(id, dto));
    }

    /**
     * Realiza una actualización parcial de la solicitud, típicamente usada para
     * transiciones de estado (ej. de PENDIENTE a EN_PROCESO).
     *
     * @param id     Identificador de la solicitud.
     * @param campos Mapa con los campos a modificar.
     * @return {@link ResponseEntity} con la solicitud reflejando los cambios.
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
    public ResponseEntity<SolicitudResponseDto> actualizarParcial(
            @PathVariable Long id,
            @RequestBody Map<String, Object> campos) {
        return ResponseEntity.ok(listaEsperaService.actualizarParcial(id, campos));
    }

    /**
     * Elimina una solicitud de la lista de espera.
     *
     * @param id Identificador de la solicitud a eliminar.
     * @return {@link ResponseEntity} sin contenido (204) tras la eliminación exitosa.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        listaEsperaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}