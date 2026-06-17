package com.example.pacientes.service;

import com.example.pacientes.dto.NotificacionDto;
import com.example.pacientes.dto.PacienteRequestDto;
import com.example.pacientes.dto.PacienteResponseDto;
import com.example.pacientes.exception.BusinessRuleException;
import com.example.pacientes.exception.ResourceNotFoundException;
import com.example.pacientes.model.EstadoPaciente;
import com.example.pacientes.model.Notificacion;
import com.example.pacientes.model.Paciente;
import com.example.pacientes.repository.NotificacionRepository;
import com.example.pacientes.repository.PacienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PacienteServiceTest {

    @Mock private PacienteRepository pacienteRepository;
    @Mock private NotificacionRepository notificacionRepository;

    @InjectMocks private PacienteService pacienteService;

    private Paciente pacienteMock;

    // record order: authId, rut, nombre, apellido, email
    private final PacienteRequestDto requestDto =
            new PacienteRequestDto(10L, "12.345.678-9", "Juan", "Pérez", "juan@test.cl");

    @BeforeEach
    void setUp() {
        pacienteMock = new Paciente();
        pacienteMock.setId(1L);
        pacienteMock.setAuthId(10L);
        pacienteMock.setRut("12345678-9");
        pacienteMock.setNombre("Juan");
        pacienteMock.setApellido("Pérez");
        pacienteMock.setEmail("juan@test.cl");
        pacienteMock.setEstado("ACTIVO");
        pacienteMock.setFecharegistro(LocalDateTime.now());
    }

    // ─── obtenerTodos ─────────────────────────────────────────────
    @Test
    void obtenerTodos_deberiaRetornarListaMapeada() {
        when(pacienteRepository.findAll()).thenReturn(List.of(pacienteMock));

        List<PacienteResponseDto> result = pacienteService.obtenerTodos();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).nombre()).isEqualTo("Juan");
        verify(pacienteRepository).findAll();
    }

    @Test
    void obtenerTodos_listaVacia_deberiaRetornarVacio() {
        when(pacienteRepository.findAll()).thenReturn(List.of());

        assertThat(pacienteService.obtenerTodos()).isEmpty();
    }

    // ─── obtenerPorId ─────────────────────────────────────────────
    @Test
    void obtenerPorId_existente_deberiaRetornarDto() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(pacienteMock));

        PacienteResponseDto result = pacienteService.obtenerPorId(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.estado()).isEqualTo(EstadoPaciente.ACTIVO);
    }

    @Test
    void obtenerPorId_noExistente_deberiaLanzarResourceNotFoundException() {
        when(pacienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pacienteService.obtenerPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ─── obtenerPorAuthId ─────────────────────────────────────────
    @Test
    void obtenerPorAuthId_existente_deberiaRetornarDto() {
        when(pacienteRepository.findByAuthId(10L)).thenReturn(Optional.of(pacienteMock));

        PacienteResponseDto result = pacienteService.obtenerPorAuthId(10L);

        assertThat(result.authId()).isEqualTo(10L);
    }

    @Test
    void obtenerPorAuthId_noExistente_deberiaLanzarExcepcion() {
        when(pacienteRepository.findByAuthId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pacienteService.obtenerPorAuthId(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── crear ────────────────────────────────────────────────────
    @Test
    void crear_exitoso_deberiaGuardarConEstadoActivo() {
        when(pacienteRepository.findByAuthId(10L)).thenReturn(Optional.empty());
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(pacienteMock);

        PacienteResponseDto result = pacienteService.crear(requestDto);

        assertThat(result.nombre()).isEqualTo("Juan");
        assertThat(result.estado()).isEqualTo(EstadoPaciente.ACTIVO);
        verify(pacienteRepository).save(any(Paciente.class));
    }

    @Test
    void crear_authIdDuplicado_deberiaLanzarBusinessRuleException() {
        when(pacienteRepository.findByAuthId(10L)).thenReturn(Optional.of(pacienteMock));

        assertThatThrownBy(() -> pacienteService.crear(requestDto))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("authId");

        verify(pacienteRepository, never()).save(any());
    }

    @Test
    void crear_sinAuthId_deberiaGuardarSinValidarDuplicado() {
        PacienteRequestDto dtoSinAuth = new PacienteRequestDto(null, "99.888.777-6", "Ana", "Soto", "ana@test.cl");
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(pacienteMock);

        PacienteResponseDto result = pacienteService.crear(dtoSinAuth);

        assertThat(result).isNotNull();
        verify(pacienteRepository, never()).findByAuthId(any());
    }

    // ─── actualizarCompleto ───────────────────────────────────────
    @Test
    void actualizarCompleto_existente_deberiaActualizarCampos() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(pacienteMock));
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(pacienteMock);

        PacienteRequestDto update = new PacienteRequestDto(null, "98.765.432-1", "Juan", "Pérez", "nuevo@test.cl");

        PacienteResponseDto result = pacienteService.actualizarCompleto(1L, update);

        assertThat(result).isNotNull();
        verify(pacienteRepository).save(pacienteMock);
    }

    @Test
    void actualizarCompleto_noExistente_deberiaLanzarExcepcion() {
        when(pacienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pacienteService.actualizarCompleto(99L, requestDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── actualizarParcial ────────────────────────────────────────
    @Test
    void actualizarParcial_camposNombreYEmail_deberiaActualizar() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(pacienteMock));
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(pacienteMock);

        PacienteResponseDto result = pacienteService.actualizarParcial(1L,
                Map.of("nombre", "Carlos", "email", "carlos@test.cl"));

        assertThat(result).isNotNull();
        verify(pacienteRepository).save(pacienteMock);
    }

    @Test
    void actualizarParcial_noExistente_deberiaLanzarExcepcion() {
        when(pacienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pacienteService.actualizarParcial(99L, Map.of()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── eliminar ─────────────────────────────────────────────────
    @Test
    void eliminar_existente_deberiaInvocarDeleteById() {
        when(pacienteRepository.existsById(1L)).thenReturn(true);

        pacienteService.eliminar(1L);

        verify(pacienteRepository).deleteById(1L);
    }

    @Test
    void eliminar_noExistente_deberiaLanzarExcepcion() {
        when(pacienteRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> pacienteService.eliminar(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(pacienteRepository, never()).deleteById(anyLong());
    }

    // ─── obtenerNotificaciones ────────────────────────────────────
    @Test
    void obtenerNotificaciones_deberiaRetornarDtosMapeados() {
        Notificacion n = new Notificacion(1L, "Su agenda ha sido aceptada");
        when(notificacionRepository.findByPacienteIdOrderByFechaCreacionDesc(1L))
                .thenReturn(List.of(n));

        List<NotificacionDto> result = pacienteService.obtenerNotificaciones(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).mensaje()).isEqualTo("Su agenda ha sido aceptada");
        assertThat(result.get(0).leida()).isFalse();
    }

    @Test
    void obtenerNotificaciones_sinRegistros_deberiaRetornarVacio() {
        when(notificacionRepository.findByPacienteIdOrderByFechaCreacionDesc(1L))
                .thenReturn(List.of());

        assertThat(pacienteService.obtenerNotificaciones(1L)).isEmpty();
    }

    // ─── obtenerPorEstado ─────────────────────────────────────────
    @Test
    void obtenerPorEstado_deberiaFiltrarCorrectamente() {
        when(pacienteRepository.findByEstado("ACTIVO")).thenReturn(List.of(pacienteMock));

        List<PacienteResponseDto> result = pacienteService.obtenerPorEstado("ACTIVO");

        assertThat(result).hasSize(1);
        verify(pacienteRepository).findByEstado("ACTIVO");
    }
}
