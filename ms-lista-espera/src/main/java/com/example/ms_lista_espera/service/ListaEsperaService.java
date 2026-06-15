package com.example.ms_lista_espera.service;

import com.example.ms_lista_espera.dto.PacienteDto;
import com.example.ms_lista_espera.dto.RegistroSolicitudDto;
import com.example.ms_lista_espera.dto.SolicitudResponseDto;
import com.example.ms_lista_espera.model.SolicitudAtencion;
import com.example.ms_lista_espera.repository.SolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio encargado de gestionar el flujo de solicitudes en la lista de espera médica.
 * <p>
 * Este componente es responsable de orquestar la comunicación con el microservicio de pacientes
 * ({@code ms-pacientes}) para validar identidades y enriquecer los datos de las solicitudes.
 * Gestiona la lógica de persistencia, priorización algorítmica y actualización de estados
 * de atención.
 * </p>
 */
@Service
public class ListaEsperaService {

    private final SolicitudRepository solicitudRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public ListaEsperaService(SolicitudRepository solicitudRepository, RestTemplate restTemplate) {
        this.solicitudRepository = solicitudRepository;
        this.restTemplate = restTemplate;
    }

    /**
     * Mapea una entidad de solicitud a un DTO, ocultando datos sensibles del paciente.
     * <p>
     * Se utiliza principalmente en listados generales donde no se requiere la información
     * completa del paciente por razones de seguridad o performance.
     * </p>
     *
     * @param s La entidad de solicitud de atención.
     * @return {@link SolicitudResponseDto} con datos básicos.
     */
    private SolicitudResponseDto mapearADtoSimple(SolicitudAtencion s) {
        return new SolicitudResponseDto(
                s.getId(), s.getPacienteId(), "Oculto en lista general", "Oculto en lista general",
                s.getTipoSolicitud(), s.getGravedad(), s.getEstado(), s.getFechaSolicitud()
        );
    }

    /**
     * Registra una nueva solicitud de atención tras validar la existencia del paciente.
     * <p>
     * Realiza una llamada externa vía {@link RestTemplate} al servicio de pacientes para
     * confirmar que el ID es válido antes de persistir la solicitud.
     * </p>
     *
     * @param dto   Objeto con la información básica de la solicitud.
     * @param token Token JWT de autorización requerido para consultar al servicio de pacientes.
     * @return {@link SolicitudResponseDto} con la información completa tras la persistencia.
     * @throws IllegalArgumentException si el paciente no existe o el token es inválido.
     */
    public SolicitudResponseDto registrarSolicitud(RegistroSolicitudDto dto, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<PacienteDto> response;
        try {
            response = restTemplate.exchange("http://ms-pacientes/api/pacientes/" + dto.pacienteId(), HttpMethod.GET, requestEntity, PacienteDto.class);
        } catch (HttpClientErrorException e) {
            throw new IllegalArgumentException("Error: El paciente con ID " + dto.pacienteId() + " no existe o el token es inválido.");
        }

        PacienteDto paciente = response.getBody();
        SolicitudAtencion solicitud = new SolicitudAtencion(dto.pacienteId(), dto.tipoSolicitud(), dto.gravedad().toUpperCase());
        SolicitudAtencion guardada = solicitudRepository.save(solicitud);

        return new SolicitudResponseDto(guardada.getId(), guardada.getPacienteId(), paciente.rut(), paciente.nombre() + " " + paciente.apellido(),
                guardada.getTipoSolicitud(), guardada.getGravedad(), guardada.getEstado(), guardada.getFechaSolicitud());
    }

    /**
     * Obtiene la lista de espera ordenada bajo criterios de prioridad.
     *
     * @return Lista de {@link SolicitudResponseDto} priorizados.
     */
    public List<SolicitudResponseDto> obtenerListaPriorizada() {
        return solicitudRepository.obtenerListaPriorizada().stream()
                .map(this::mapearADtoSimple).collect(Collectors.toList());
    }

    /**
     * Recupera una solicitud específica y enriquece sus datos consultando al servicio de pacientes.
     * <p>
     * Si la conexión con el servicio de pacientes falla, retorna los datos básicos de la solicitud
     * como medida de resiliencia.
     * </p>
     *
     * @param id    ID de la solicitud.
     * @param token Token JWT de autorización.
     * @return {@link SolicitudResponseDto} con datos enriquecidos o básicos.
     */
    public SolicitudResponseDto obtenerPorId(Long id, String token) {
        SolicitudAtencion s = solicitudRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<PacienteDto> response = restTemplate.exchange("http://ms-pacientes/api/pacientes/" + s.getPacienteId(), HttpMethod.GET, requestEntity, PacienteDto.class);
            PacienteDto p = response.getBody();
            return new SolicitudResponseDto(s.getId(), s.getPacienteId(), p.rut(), p.nombre() + " " + p.apellido(), s.getTipoSolicitud(), s.getGravedad(), s.getEstado(), s.getFechaSolicitud());
        } catch (Exception e) {
            return mapearADtoSimple(s);
        }
    }

    /**
     * Busca solicitudes filtradas por su estado actual.
     *
     * @param estado Estado de la solicitud (ej. PENDIENTE).
     * @return Lista de {@link SolicitudResponseDto}.
     */
    public List<SolicitudResponseDto> obtenerPorEstado(String estado) {
        return solicitudRepository.findByEstado(estado.toUpperCase()).stream()
                .map(this::mapearADtoSimple).collect(Collectors.toList());
    }

    /**
     * Realiza una actualización completa de los datos médicos de la solicitud.
     * <p>
     * Nota: La regla de negocio prohíbe modificar el {@code pacienteId} original.
     * </p>
     *
     * @param id  ID de la solicitud.
     * @param dto Datos con los nuevos parámetros médicos.
     * @return {@link SolicitudResponseDto} actualizado.
     */
    public SolicitudResponseDto actualizarCompleto(Long id, RegistroSolicitudDto dto) {
        SolicitudAtencion s = solicitudRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        s.setTipoSolicitud(dto.tipoSolicitud());
        s.setGravedad(dto.gravedad().toUpperCase());

        return mapearADtoSimple(solicitudRepository.save(s));
    }

    /**
     * Realiza una actualización flexible de campos específicos de la solicitud.
     *
     * @param id     ID de la solicitud.
     * @param campos Mapa con los campos a modificar (tipoSolicitud, gravedad, estado).
     * @return {@link SolicitudResponseDto} actualizado.
     */
    public SolicitudResponseDto actualizarParcial(Long id, Map<String, Object> campos) {
        SolicitudAtencion s = solicitudRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        campos.forEach((clave, valor) -> {
            switch (clave) {
                case "tipoSolicitud": s.setTipoSolicitud((String) valor); break;
                case "gravedad": s.setGravedad(((String) valor).toUpperCase()); break;
                case "estado": s.setEstado(((String) valor).toUpperCase()); break;
            }
        });
        return mapearADtoSimple(solicitudRepository.save(s));
    }

    /**
     * Filtra solicitudes basándose en el estado y el tipo de atención médica.
     *
     * @param estado   Estado de la solicitud.
     * @param tipoCita Tipo de atención.
     * @return Lista de {@link SolicitudResponseDto} filtrados.
     */
    public List<SolicitudResponseDto> filtrar(String estado, String tipoCita) {
        return solicitudRepository.findByEstadoAndTipoSolicitud(estado.toUpperCase(), tipoCita.toUpperCase())
                .stream()
                .map(this::mapearADtoSimple)
                .collect(Collectors.toList());
    }

    /**
     * Elimina una solicitud del sistema.
     *
     * @param id ID de la solicitud a eliminar.
     * @throws IllegalArgumentException si la solicitud no existe.
     */
    public void eliminar(Long id) {
        if (!solicitudRepository.existsById(id)) {
            throw new IllegalArgumentException("Solicitud no encontrada");
        }
        solicitudRepository.deleteById(id);
    }
}