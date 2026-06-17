package com.example.ms_lista_espera.service;

import com.example.ms_lista_espera.dto.PacienteDto;
import com.example.ms_lista_espera.dto.RegistroSolicitudDto;
import com.example.ms_lista_espera.dto.SolicitudResponseDto;
import com.example.ms_lista_espera.exception.ResourceNotFoundException;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListaEsperaServiceTest {

    @Mock private SolicitudRepository solicitudRepository;
    @Mock private CitaMedicaRepository citaMedicaRepository;
    @Mock private RestTemplate restTemplate;
    @Mock private StreamBridge streamBridge;

    @InjectMocks private ListaEsperaService listaEsperaService;

    private SolicitudAtencion solicitudMock;
    private PacienteDto pacienteMock;

    // record: pacienteId, tipoSolicitud, gravedad
    private final RegistroSolicitudDto registroDto =
            new RegistroSolicitudDto(10L, "MEDICINA_GENERAL", "ALTA");

    @BeforeEach
    void setUp() {
        solicitudMock = new SolicitudAtencion(10L, "MEDICINA_GENERAL", "ALTA");
        solicitudMock.setId(1L);
        solicitudMock.setFechaSolicitud(LocalDateTime.now());

        // record: id, rut, nombre, apellido
        pacienteMock = new PacienteDto(10L, "12345678-9", "Juan", "Pérez");
    }

    // ─── registrarSolicitud ───────────────────────────────────────
    @Test
    void registrarSolicitud_exitoso_deberiaGuardarYRetornarDto() {
        // Given
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET),
                any(HttpEntity.class), eq(PacienteDto.class)))
                .thenReturn(new ResponseEntity<>(pacienteMock, HttpStatus.OK));
        when(solicitudRepository.save(any(SolicitudAtencion.class))).thenReturn(solicitudMock);

        // When
        SolicitudResponseDto result = listaEsperaService.registrarSolicitud(registroDto, "Bearer token");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.tipoSolicitud()).isEqualTo("MEDICINA_GENERAL");
        assertThat(result.estado()).isEqualTo("BUSCANDO_CITA");
        verify(solicitudRepository).save(any(SolicitudAtencion.class));
    }

    @Test
    void registrarSolicitud_pacienteNoExiste_deberiaLanzarIllegalArgumentException() {
        // Given
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET),
                any(HttpEntity.class), eq(PacienteDto.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // When / Then
        assertThatThrownBy(() -> listaEsperaService.registrarSolicitud(registroDto, "Bearer token"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("10");

        verify(solicitudRepository, never()).save(any());
    }

    // ─── obtenerListaPriorizada ───────────────────────────────────
    @Test
    void obtenerListaPriorizada_deberiaRetornarListaMapeada() {
        // Given
        when(solicitudRepository.obtenerListaPriorizada()).thenReturn(List.of(solicitudMock));

        // When
        List<SolicitudResponseDto> result = listaEsperaService.obtenerListaPriorizada();

        // Then
        assertThat(result).hasSize(1);
        verify(solicitudRepository).obtenerListaPriorizada();
    }

    // ─── obtenerPorId ─────────────────────────────────────────────
    @Test
    void obtenerPorId_existente_fetchPacienteExitoso_deberiaRetornarDtoCompleto() {
        // Given
        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitudMock));
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET),
                any(HttpEntity.class), eq(PacienteDto.class)))
                .thenReturn(new ResponseEntity<>(pacienteMock, HttpStatus.OK));

        // When
        SolicitudResponseDto result = listaEsperaService.obtenerPorId(1L, "Bearer token");

        // Then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.rutPaciente()).isEqualTo("12345678-9");
    }

    @Test
    void obtenerPorId_existente_fetchFalla_deberiaRetornarDtoSimple() {
        // Given — paciente endpoint unreachable
        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitudMock));
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET),
                any(HttpEntity.class), eq(PacienteDto.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        // When
        SolicitudResponseDto result = listaEsperaService.obtenerPorId(1L, "Bearer token");

        // Then — fallback to mapearADtoSimple (datos ocultos)
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.rutPaciente()).isEqualTo("Oculto en lista general");
    }

    @Test
    void obtenerPorId_noExistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(solicitudRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> listaEsperaService.obtenerPorId(99L, "Bearer token"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ─── obtenerPorEstado ─────────────────────────────────────────
    @Test
    void obtenerPorEstado_deberiaConvertirAMayusculasYFiltrar() {
        // Given
        when(solicitudRepository.findByEstado("BUSCANDO_CITA")).thenReturn(List.of(solicitudMock));

        // When
        List<SolicitudResponseDto> result = listaEsperaService.obtenerPorEstado("buscando_cita");

        // Then
        assertThat(result).hasSize(1);
        verify(solicitudRepository).findByEstado("BUSCANDO_CITA");
    }

    // ─── filtrar ──────────────────────────────────────────────────
    @Test
    void filtrar_fetchPacienteExitoso_deberiaRetornarListaConDatosReales() {
        // Given
        when(solicitudRepository.findByEstadoAndTipoSolicitud("BUSCANDO_CITA", "MEDICINA_GENERAL"))
                .thenReturn(List.of(solicitudMock));
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET),
                any(HttpEntity.class), eq(PacienteDto.class)))
                .thenReturn(new ResponseEntity<>(pacienteMock, HttpStatus.OK));

        // When
        List<SolicitudResponseDto> result =
                listaEsperaService.filtrar("buscando_cita", "medicina_general", "Bearer token");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).rutPaciente()).isEqualTo("12345678-9");
    }

    @Test
    void filtrar_fetchFalla_deberiaUsarFallback() {
        // Given
        when(solicitudRepository.findByEstadoAndTipoSolicitud(anyString(), anyString()))
                .thenReturn(List.of(solicitudMock));
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET),
                any(HttpEntity.class), eq(PacienteDto.class)))
                .thenThrow(new RuntimeException("timeout"));

        // When
        List<SolicitudResponseDto> result =
                listaEsperaService.filtrar("BUSCANDO_CITA", "MEDICINA_GENERAL", "Bearer token");

        // Then
        assertThat(result.get(0).rutPaciente()).isEqualTo("Oculto en lista general");
    }

    // ─── actualizarCompleto ───────────────────────────────────────
    @Test
    void actualizarCompleto_existente_deberiaActualizarCampos() {
        // Given
        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitudMock));
        when(solicitudRepository.save(any(SolicitudAtencion.class))).thenReturn(solicitudMock);

        RegistroSolicitudDto updateDto = new RegistroSolicitudDto(10L, "NEUROLOGIA", "BAJA");

        // When
        SolicitudResponseDto result = listaEsperaService.actualizarCompleto(1L, updateDto);

        // Then
        assertThat(result).isNotNull();
        verify(solicitudRepository).save(solicitudMock);
    }

    @Test
    void actualizarCompleto_noExistente_deberiaLanzarExcepcion() {
        // Given
        when(solicitudRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> listaEsperaService.actualizarCompleto(99L, registroDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── actualizarParcial ────────────────────────────────────────
    @Test
    void actualizarParcial_estadoAceptada_deberiaGuardarCitaYEmitirEvento() {
        // Given
        solicitudMock.setDoctorId(5L);
        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitudMock));
        when(solicitudRepository.save(any(SolicitudAtencion.class))).thenReturn(solicitudMock);
        when(citaMedicaRepository.save(any(CitaMedica.class))).thenReturn(new CitaMedica());

        Map<String, Object> campos = Map.of("estado", "ACEPTADA", "doctorId", "5");

        // When
        SolicitudResponseDto result = listaEsperaService.actualizarParcial(1L, campos);

        // Then
        assertThat(result).isNotNull();
        verify(citaMedicaRepository).save(any(CitaMedica.class));
        verify(streamBridge).send(eq("citaActualizada-out-0"), any(Map.class));
    }

    @Test
    void actualizarParcial_estadoRechazada_noDeberiaGuardarCita_peroSiEmitirEvento() {
        // Given
        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitudMock));
        when(solicitudRepository.save(any(SolicitudAtencion.class))).thenReturn(solicitudMock);

        Map<String, Object> campos = Map.of("estado", "RECHAZADA");

        // When
        listaEsperaService.actualizarParcial(1L, campos);

        // Then
        verify(citaMedicaRepository, never()).save(any(CitaMedica.class));
        verify(streamBridge).send(eq("citaActualizada-out-0"), any(Map.class));
    }

    @Test
    void actualizarParcial_noExistente_deberiaLanzarExcepcion() {
        // Given
        when(solicitudRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> listaEsperaService.actualizarParcial(99L, Map.of()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── eliminar ─────────────────────────────────────────────────
    @Test
    void eliminar_existente_deberiaInvocarDeleteById() {
        // Given
        when(solicitudRepository.existsById(1L)).thenReturn(true);

        // When
        listaEsperaService.eliminar(1L);

        // Then
        verify(solicitudRepository).deleteById(1L);
    }

    @Test
    void eliminar_noExistente_deberiaLanzarExcepcion() {
        // Given
        when(solicitudRepository.existsById(99L)).thenReturn(false);

        // When / Then
        assertThatThrownBy(() -> listaEsperaService.eliminar(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(solicitudRepository, never()).deleteById(anyLong());
    }
}
