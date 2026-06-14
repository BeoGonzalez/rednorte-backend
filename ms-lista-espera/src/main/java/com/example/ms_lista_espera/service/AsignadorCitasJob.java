package com.example.ms_lista_espera.service;

import com.example.ms_lista_espera.model.CitaMedica;
import com.example.ms_lista_espera.model.SolicitudAtencion;
import com.example.ms_lista_espera.repository.CitaMedicaRepository;
import com.example.ms_lista_espera.repository.SolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class AsignadorCitasJob {

    private final SolicitudRepository solicitudRepository;
    private final CitaMedicaRepository citaMedicaRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public AsignadorCitasJob(SolicitudRepository solicitudRepository, CitaMedicaRepository citaMedicaRepository, RestTemplate restTemplate) {
        this.solicitudRepository = solicitudRepository;
        this.citaMedicaRepository = citaMedicaRepository;
        this.restTemplate = restTemplate;
    }

    // Se ejecuta cada 60 segundos
    @Scheduled(fixedRate = 60000)
    public void autoAsignarCitas() {
        System.out.println("🤖 [AsignadorCitasJob] Buscando solicitudes PENDIENTES...");
        
        List<SolicitudAtencion> pendientes = solicitudRepository.findByEstado("PENDIENTE");
        if (pendientes.isEmpty()) {
            return;
        }

        for (SolicitudAtencion solicitud : pendientes) {
            String especialidad = solicitud.getTipoSolicitud(); // Ej: "CARDIOLOGIA"
            
            try {
                // Buscar doctores disponibles con esa especialidad
                String url = "http://ms-doctores:8085/api/doctores/especialidad/" + especialidad;
                
                ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Map<String, Object>>>() {}
                );
                
                List<Map<String, Object>> doctores = response.getBody();
                
                if (doctores != null && !doctores.isEmpty()) {
                    // Tomamos el primer doctor disponible
                    Map<String, Object> doctorAsignado = doctores.get(0);
                    // Parseamos id a Long de forma segura, asumiendo que el ID del sistema es 'authId'
                    Long doctorId = Long.parseLong(doctorAsignado.get("id").toString());
                    
                    // Crear la cita
                    CitaMedica cita = new CitaMedica(solicitud.getId(), solicitud.getPacienteId(), doctorId);
                    citaMedicaRepository.save(cita);
                    
                    // Actualizar estado de la solicitud a ASIGNADO
                    solicitud.setEstado("ASIGNADO");
                    solicitudRepository.save(solicitud);
                    
                    System.out.println("✅ [AsignadorCitasJob] Cita agendada para solicitud " + solicitud.getId() + " con doctor ID " + doctorId);
                }
            } catch (Exception e) {
                System.err.println("⚠️ [AsignadorCitasJob] Error al buscar doctores para " + especialidad + ": " + e.getMessage());
            }
        }
    }
}
