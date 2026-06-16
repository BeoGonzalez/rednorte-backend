package com.example.ms_lista_espera.service;

import com.example.ms_lista_espera.model.CitaMedica;
import com.example.ms_lista_espera.model.SolicitudAtencion;
import com.example.ms_lista_espera.repository.CitaMedicaRepository;
import com.example.ms_lista_espera.repository.SolicitudRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsignadorCitasJobTest {

    @Mock private SolicitudRepository solicitudRepository;
    @Mock private CitaMedicaRepository citaMedicaRepository;
    @Mock private RestTemplate restTemplate;
    @Mock private StreamBridge streamBridge;

    @InjectMocks
    private AsignadorCitasJob asignadorCitasJob;

    private SolicitudAtencion solicitudPendiente;

    @BeforeEach
    void setUp() {
        solicitudPendiente = new SolicitudAtencion();
        solicitudPendiente.setId(100L);
        solicitudPendiente.setPacienteId(10L);
        solicitudPendiente.setTipoSolicitud("CARDIOLOGIA");
        solicitudPendiente.setEstado("PENDIENTE");
    }

    @Test
    void testAutoAsignarCitas_SinPendientes() {
        when(solicitudRepository.findByEstado("PENDIENTE")).thenReturn(Collections.emptyList());

        asignadorCitasJob.autoAsignarCitas();

        verify(restTemplate, never()).exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class));
        verify(citaMedicaRepository, never()).save(any());
        verify(streamBridge, never()).send(anyString(), any());
    }

    @Test
    void testAutoAsignarCitas_ConExito() {
        when(solicitudRepository.findByEstado("PENDIENTE")).thenReturn(List.of(solicitudPendiente));

        List<Map<String, Object>> doctoresDisponibles = List.of(Map.of("id", 5L));
        ResponseEntity<List<Map<String, Object>>> mockResponse = new ResponseEntity<>(doctoresDisponibles, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(mockResponse);

        // 🟢 EL FIX: Simulamos que la base de datos guardó la cita y le generó un ID
        when(citaMedicaRepository.save(any(CitaMedica.class))).thenAnswer(i -> {
            CitaMedica cita = i.getArgument(0);
            cita.setId(999L); // Esto evita el NullPointerException
            return cita;
        });

        when(solicitudRepository.save(any(SolicitudAtencion.class))).thenAnswer(i -> i.getArgument(0));

        when(streamBridge.send(anyString(), any())).thenReturn(true);

        asignadorCitasJob.autoAsignarCitas();

        verify(citaMedicaRepository, times(1)).save(argThat(cita ->
                cita.getDoctorId().equals(5L) && cita.getPacienteId().equals(10L)
        ));

        verify(solicitudRepository, times(1)).save(argThat(solicitud ->
                solicitud.getEstado().equals("ASIGNADO")
        ));

        verify(streamBridge, times(1)).send(eq("citaAsignada-out-0"), any());
    }

    @Test
    void testAutoAsignarCitas_CaminoNegativo_MicroservicioCaido() {
        when(solicitudRepository.findByEstado("PENDIENTE")).thenReturn(List.of(solicitudPendiente));

        // 🟢 CORRECCIÓN: anyString() y any() aquí también
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new RuntimeException("Connection refused"));

        asignadorCitasJob.autoAsignarCitas();

        assertEquals("PENDIENTE", solicitudPendiente.getEstado());
        verify(citaMedicaRepository, never()).save(any());
        verify(streamBridge, never()).send(anyString(), any());
    }
}