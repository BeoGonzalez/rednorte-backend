package com.example.ms_lista_espera.service;

import com.example.ms_lista_espera.model.CitaMedica;
import com.example.ms_lista_espera.model.SolicitudAtencion;
import com.example.ms_lista_espera.repository.CitaMedicaRepository;
import com.example.ms_lista_espera.repository.SolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge; // 🟢 IMPORTANTE: Importar StreamBridge
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
    private final StreamBridge streamBridge; // 🟢 1. Declarar StreamBridge

    @Autowired
    public AsignadorCitasJob(SolicitudRepository solicitudRepository,
                             CitaMedicaRepository citaMedicaRepository,
                             RestTemplate restTemplate,
                             StreamBridge streamBridge) { // 🟢 2. Inyectarlo en el constructor
        this.solicitudRepository = solicitudRepository;
        this.citaMedicaRepository = citaMedicaRepository;
        this.restTemplate = restTemplate;
        this.streamBridge = streamBridge;
    }

    @Scheduled(fixedRate = 60000)
    public void autoAsignarCitas() {
        System.out.println("🤖 [AsignadorCitasJob] Buscando solicitudes PENDIENTES...");

        List<SolicitudAtencion> pendientes = solicitudRepository.findByEstado("PENDIENTE");
        if (pendientes.isEmpty()) return;

        for (SolicitudAtencion solicitud : pendientes) {
            String especialidad = solicitud.getTipoSolicitud();

            try {
                String url = "http://ms-doctores:8085/api/doctores/especialidad/" + especialidad;
                ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                        url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Map<String, Object>>>() {}
                );

                List<Map<String, Object>> doctores = response.getBody();

                if (doctores != null && !doctores.isEmpty()) {
                    Map<String, Object> doctorAsignado = doctores.get(0);
                    Long doctorId = Long.parseLong(doctorAsignado.get("id").toString());

                    CitaMedica cita = new CitaMedica(solicitud.getId(), solicitud.getPacienteId(), doctorId);
                    citaMedicaRepository.save(cita);

                    solicitud.setEstado("ASIGNADO");
                    solicitudRepository.save(solicitud);

                    System.out.println("✅ [AsignadorCitasJob] Cita agendada para solicitud " + solicitud.getId() + " con doctor ID " + doctorId);

                    // 🟢 3. EMITIR EL EVENTO A RABBITMQ
                    Map<String, Object> eventoPayload = Map.of(
                            "citaId", cita.getId(),
                            "pacienteId", solicitud.getPacienteId(),
                            "doctorId", doctorId,
                            "especialidad", especialidad,
                            "mensaje", "Cita asignada automáticamente por el sistema."
                    );

                    // El nombre "citaAsignada-out-0" DEBE coincidir con el application.yml
                    streamBridge.send("citaAsignada-out-0", eventoPayload);
                    System.out.println("📢 [RabbitMQ] Evento emitido al Exchange 'citas-events-exchange'");
                }
            } catch (Exception e) {
                System.err.println("⚠️ [AsignadorCitasJob] Error: " + e.getMessage());
            }
        }
    }
}