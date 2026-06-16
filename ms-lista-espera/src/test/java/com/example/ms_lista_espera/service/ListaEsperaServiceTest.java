package com.example.ms_lista_espera.service;

import com.example.ms_lista_espera.dto.RegistroSolicitudDto;
import com.example.ms_lista_espera.dto.SolicitudResponseDto;
import com.example.ms_lista_espera.dto.PacienteDto; // 🟢 IMPORTANTE: Añadimos esta importación
import com.example.ms_lista_espera.exception.ResourceNotFoundException;
import com.example.ms_lista_espera.model.SolicitudAtencion;
import com.example.ms_lista_espera.repository.SolicitudRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListaEsperaServiceTest {

    @Mock
    private SolicitudRepository solicitudRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ListaEsperaService listaEsperaService;

    private SolicitudAtencion solicitudMock;
    private RegistroSolicitudDto registroDtoMock;

    @BeforeEach
    void setUp() {
        solicitudMock = new SolicitudAtencion();
        solicitudMock.setId(1L);
        solicitudMock.setPacienteId(10L);
        solicitudMock.setTipoSolicitud("CARDIOLOGIA");
        solicitudMock.setEstado("PENDIENTE");

        registroDtoMock = new RegistroSolicitudDto(10L, "CARDIOLOGIA", "Chequeo de rutina");
    }

    @Test
    void testRegistrarSolicitud_Exito() {
        // 🟢 CORRECCIÓN: Le pasamos los 4 datos obligatorios al record PacienteDto
        PacienteDto pacienteMock = new PacienteDto(10L, "Juan", "Perez", "juan@test.com");

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(PacienteDto.class)))
                .thenReturn(new ResponseEntity<>(pacienteMock, HttpStatus.OK));

        when(solicitudRepository.save(any(SolicitudAtencion.class))).thenReturn(solicitudMock);

        SolicitudResponseDto resultado = listaEsperaService.registrarSolicitud(registroDtoMock, "Bearer token-mock");

        assertNotNull(resultado);
        assertEquals("CARDIOLOGIA", resultado.tipoSolicitud());
        assertEquals("PENDIENTE", resultado.estado());
        verify(solicitudRepository, times(1)).save(any(SolicitudAtencion.class));
    }

    @Test
    void testRegistrarSolicitud_FallaPacienteNoExiste() {
        // 🟢 CORRECCIÓN: Usamos PacienteDto.class aquí también
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(PacienteDto.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThrows(RuntimeException.class, () -> {
            listaEsperaService.registrarSolicitud(registroDtoMock, "Bearer token-mock");
        });

        verify(solicitudRepository, never()).save(any(SolicitudAtencion.class));
    }

    @Test
    void testObtenerPorId_Existe() {
        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitudMock));

        SolicitudResponseDto resultado = listaEsperaService.obtenerPorId(1L, "Bearer token-mock");

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
    }

    @Test
    void testObtenerPorId_NoExiste() {
        when(solicitudRepository.findById(99L)).thenReturn(Optional.empty());

        // 🟢 CORRECCIÓN: Esperar IllegalArgumentException en lugar de ResourceNotFoundException
        assertThrows(IllegalArgumentException.class, () -> {
            listaEsperaService.obtenerPorId(99L, "Bearer token-mock");
        });
    }
}