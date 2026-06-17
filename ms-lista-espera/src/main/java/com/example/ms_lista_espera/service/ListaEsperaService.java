package com.example.ms_lista_espera.service;

import com.example.ms_lista_espera.dto.PacienteDto;
import com.example.ms_lista_espera.dto.RegistroSolicitudDto;
import com.example.ms_lista_espera.dto.SolicitudResponseDto;
import com.example.ms_lista_espera.exception.ResourceNotFoundException;
import com.example.ms_lista_espera.model.CitaMedica;
import com.example.ms_lista_espera.model.SolicitudAtencion;
import com.example.ms_lista_espera.repository.CitaMedicaRepository;
import com.example.ms_lista_espera.repository.SolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ListaEsperaService {

    private final SolicitudRepository solicitudRepository;
    private final CitaMedicaRepository citaMedicaRepository;
    private final RestTemplate restTemplate;
    private final StreamBridge streamBridge;

    @Autowired
    public ListaEsperaService(SolicitudRepository solicitudRepository,
                              CitaMedicaRepository citaMedicaRepository,
                              RestTemplate restTemplate,
                              StreamBridge streamBridge) {
        this.solicitudRepository = solicitudRepository;
        this.citaMedicaRepository = citaMedicaRepository;
        this.restTemplate = restTemplate;
        this.streamBridge = streamBridge;
    }

    // --- Private helpers ---

    private SolicitudResponseDto mapearADtoSimple(SolicitudAtencion s) {
        return new SolicitudResponseDto(
                s.getId(), s.getPacienteId(),
                "Oculto en lista general", "Oculto en lista general",
                s.getTipoSolicitud(), s.getGravedad(), s.getEstado(),
                s.getFechaSolicitud(), s.getDoctorId());
    }

    private PacienteDto fetchPaciente(Long pacienteId, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<PacienteDto> response = restTemplate.exchange(
                "http://ms-pacientes/api/pacientes/" + pacienteId,
                HttpMethod.GET, entity, PacienteDto.class);
        return response.getBody();
    }

    // --- Public methods ---

    public SolicitudResponseDto registrarSolicitud(RegistroSolicitudDto dto, String token) {
        PacienteDto paciente;
        try {
            paciente = fetchPaciente(dto.pacienteId(), token);
        } catch (HttpClientErrorException e) {
            throw new IllegalArgumentException(
                    "El paciente con ID " + dto.pacienteId() + " no existe o el token es inválido.");
        }

        SolicitudAtencion solicitud = new SolicitudAtencion(
                dto.pacienteId(), dto.tipoSolicitud(), dto.gravedad().toUpperCase());
        SolicitudAtencion guardada = solicitudRepository.save(solicitud);

        return new SolicitudResponseDto(
                guardada.getId(), guardada.getPacienteId(),
                paciente.rut(), paciente.nombre() + " " + paciente.apellido(),
                guardada.getTipoSolicitud(), guardada.getGravedad(),
                guardada.getEstado(), guardada.getFechaSolicitud(), guardada.getDoctorId());
    }

    public List<SolicitudResponseDto> obtenerListaPriorizada() {
        return solicitudRepository.obtenerListaPriorizada().stream()
                .map(this::mapearADtoSimple)
                .collect(Collectors.toList());
    }

    public SolicitudResponseDto obtenerPorId(Long id, String token) {
        SolicitudAtencion s = solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada: " + id));
        try {
            PacienteDto p = fetchPaciente(s.getPacienteId(), token);
            return new SolicitudResponseDto(
                    s.getId(), s.getPacienteId(),
                    p.rut(), p.nombre() + " " + p.apellido(),
                    s.getTipoSolicitud(), s.getGravedad(), s.getEstado(),
                    s.getFechaSolicitud(), s.getDoctorId());
        } catch (Exception e) {
            return mapearADtoSimple(s);
        }
    }

    public List<SolicitudResponseDto> obtenerPorEstado(String estado) {
        return solicitudRepository.findByEstado(estado.toUpperCase()).stream()
                .map(this::mapearADtoSimple)
                .collect(Collectors.toList());
    }

    public List<SolicitudResponseDto> filtrar(String estado, String tipoCita, String token) {
        List<SolicitudAtencion> solicitudes = solicitudRepository
                .findByEstadoAndTipoSolicitud(estado.toUpperCase(), tipoCita.toUpperCase());

        return solicitudes.stream().map(s -> {
            try {
                PacienteDto p = fetchPaciente(s.getPacienteId(), token);
                return new SolicitudResponseDto(
                        s.getId(), s.getPacienteId(),
                        p.rut(), p.nombre() + " " + p.apellido(),
                        s.getTipoSolicitud(), s.getGravedad(), s.getEstado(),
                        s.getFechaSolicitud(), s.getDoctorId());
            } catch (Exception e) {
                return mapearADtoSimple(s);
            }
        }).collect(Collectors.toList());
    }

    public SolicitudResponseDto actualizarCompleto(Long id, RegistroSolicitudDto dto) {
        SolicitudAtencion s = solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada: " + id));
        s.setTipoSolicitud(dto.tipoSolicitud());
        s.setGravedad(dto.gravedad().toUpperCase());
        return mapearADtoSimple(solicitudRepository.save(s));
    }

    /**
     * Processes the doctor's decision (ACEPTADA or RECHAZADA).
     * When ACEPTADA: persists a CitaMedica and emits a citaActualizada event to RabbitMQ.
     * When RECHAZADA: updates the estado and emits the event so downstream services can react.
     */
    @Transactional
    public SolicitudResponseDto actualizarParcial(Long id, Map<String, Object> campos) {
        SolicitudAtencion s = solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada: " + id));

        campos.forEach((clave, valor) -> {
            switch (clave) {
                case "tipoSolicitud" -> s.setTipoSolicitud((String) valor);
                case "gravedad"      -> s.setGravedad(((String) valor).toUpperCase());
                case "estado"        -> s.setEstado(((String) valor).toUpperCase());
                case "doctorId"      -> s.setDoctorId(Long.parseLong(valor.toString()));
            }
        });

        SolicitudAtencion guardada = solicitudRepository.save(s);
        Long doctorId = guardada.getDoctorId() != null ? guardada.getDoctorId() : 1L;

        if ("ACEPTADA".equals(guardada.getEstado())) {
            CitaMedica cita = new CitaMedica(guardada.getId(), guardada.getPacienteId(), doctorId);
            citaMedicaRepository.save(cita);
        }

        // Emit event for both ACEPTADA and RECHAZADA so all services stay in sync
        Map<String, Object> evento = Map.of(
                "solicitudId",   guardada.getId(),
                "pacienteId",    guardada.getPacienteId(),
                "doctorId",      doctorId,
                "nuevoEstado",   guardada.getEstado(),
                "tipoSolicitud", guardada.getTipoSolicitud()
        );
        streamBridge.send("citaActualizada-out-0", evento);

        return mapearADtoSimple(guardada);
    }

    public void eliminar(Long id) {
        if (!solicitudRepository.existsById(id)) {
            throw new ResourceNotFoundException("Solicitud no encontrada: " + id);
        }
        solicitudRepository.deleteById(id);
    }
}
