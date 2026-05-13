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

@Service
public class ListaEsperaService {

    private final SolicitudRepository solicitudRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public ListaEsperaService(SolicitudRepository solicitudRepository, RestTemplate restTemplate) {
        this.solicitudRepository = solicitudRepository;
        this.restTemplate = restTemplate;
    }

    // --- MÉTODOS PRIVADOS DE AYUDA ---
    private SolicitudResponseDto mapearADtoSimple(SolicitudAtencion s) {
        return new SolicitudResponseDto(
                s.getId(), s.getPacienteId(), "Oculto en lista general", "Oculto en lista general",
                s.getTipoSolicitud(), s.getGravedad(), s.getEstado(), s.getFechaSolicitud()
        );
    }

    // --- 1. POST: CREAR ---
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

    // --- 2. GET: TODOS (Priorizados) ---
    public List<SolicitudResponseDto> obtenerListaPriorizada() {
        return solicitudRepository.obtenerListaPriorizada().stream()
                .map(this::mapearADtoSimple).collect(Collectors.toList());
    }

    // --- 3. GET: POR ID ---
    public SolicitudResponseDto obtenerPorId(Long id, String token) {
        SolicitudAtencion s = solicitudRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        // Como es solo uno, vamos a buscar sus datos reales a ms-pacientes
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<PacienteDto> response = restTemplate.exchange("http://ms-pacientes/api/pacientes/" + s.getPacienteId(), HttpMethod.GET, requestEntity, PacienteDto.class);
            PacienteDto p = response.getBody();
            return new SolicitudResponseDto(s.getId(), s.getPacienteId(), p.rut(), p.nombre() + " " + p.apellido(), s.getTipoSolicitud(), s.getGravedad(), s.getEstado(), s.getFechaSolicitud());
        } catch (Exception e) {
            return mapearADtoSimple(s); // Si falla la conexión, devolvemos los datos básicos
        }
    }

    // --- 4. GET: POR ESTADO ---
    public List<SolicitudResponseDto> obtenerPorEstado(String estado) {
        return solicitudRepository.findByEstado(estado.toUpperCase()).stream()
                .map(this::mapearADtoSimple).collect(Collectors.toList());
    }

    // --- 5. PUT: ACTUALIZAR COMPLETO ---
    public SolicitudResponseDto actualizarCompleto(Long id, RegistroSolicitudDto dto) {
        SolicitudAtencion s = solicitudRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        // REGLA DE NEGOCIO: No actualizamos el pacienteId, solo los datos médicos
        s.setTipoSolicitud(dto.tipoSolicitud());
        s.setGravedad(dto.gravedad().toUpperCase());

        return mapearADtoSimple(solicitudRepository.save(s));
    }

    // --- 6. PATCH: ACTUALIZAR PARCIAL ---
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

    // ... agregar este método a tu clase service actual ...
    public List<SolicitudResponseDto> filtrar(String estado, String tipoCita) {
        return solicitudRepository.findByEstadoAndTipoSolicitud(estado.toUpperCase(), tipoCita.toUpperCase())
                .stream()
                .map(this::mapearADtoSimple)
                .collect(Collectors.toList());
    }

    // --- 7. DELETE: ELIMINAR ---
    public void eliminar(Long id) {
        if (!solicitudRepository.existsById(id)) {
            throw new IllegalArgumentException("Solicitud no encontrada");
        }
        solicitudRepository.deleteById(id);
    }


}
