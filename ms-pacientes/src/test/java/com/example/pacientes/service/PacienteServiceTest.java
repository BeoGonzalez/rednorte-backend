package com.example.pacientes.service;

import com.example.pacientes.dto.PacienteRequestDto;
import com.example.pacientes.dto.PacienteResponseDto;
import com.example.pacientes.model.EstadoPaciente;
import com.example.pacientes.model.Paciente;
import com.example.pacientes.repository.PacienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class PacienteServiceTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private PacienteService pacienteService;

    private Paciente pacienteSimulado;
    private PacienteRequestDto requestDto;

    @BeforeEach
    void setUp() {
        // Simulamos la entidad principal
        pacienteSimulado = new Paciente();
        pacienteSimulado.setId(1L);
        pacienteSimulado.setRut("12345678-9"); // Con guion
        pacienteSimulado.setNombre("Juan");
        pacienteSimulado.setApellido("Perez");
        pacienteSimulado.setEmail("juan@test.com");
        pacienteSimulado.setEstado("ACTIVO");
        pacienteSimulado.setFecharegistro(LocalDateTime.now());

        requestDto = new PacienteRequestDto("12.345.678-9", "Juan", "Perez", "juan@test.com");
    }

    @Test
    void obtenerTodos_DeberiaRetornarLista() {
        Mockito.when(pacienteRepository.findAll()).thenReturn(List.of(pacienteSimulado));

        List<PacienteResponseDto> resultado = pacienteService.obtenerTodos();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("Juan", resultado.get(0).nombre());
        Mockito.verify(pacienteRepository, Mockito.times(1)).findAll();
    }

    // 🔴 NUEVO: Prueba el filtro por estado que faltaba
    @Test
    void obtenerPorEstado_DeberiaRetornarListaFiltrada() {
        Mockito.when(pacienteRepository.findByEstado("ACTIVO")).thenReturn(List.of(pacienteSimulado));

        List<PacienteResponseDto> resultado = pacienteService.obtenerPorEstado("ACTIVO");

        assertFalse(resultado.isEmpty());
        assertEquals(EstadoPaciente.ACTIVO, resultado.get(0).estado());
    }

    @Test
    void crear_DeberiaGuardarYRetornarPaciente() {
        Mockito.when(pacienteRepository.save(any(Paciente.class))).thenReturn(pacienteSimulado);

        PacienteResponseDto resultado = pacienteService.crear(requestDto);

        assertNotNull(resultado);
        assertEquals("Juan", resultado.nombre());
    }

    @Test
    void obtenerPorId_DeberiaRetornarPaciente_CuandoExiste() {
        Mockito.when(pacienteRepository.findById(1L)).thenReturn(Optional.of(pacienteSimulado));

        PacienteResponseDto resultado = pacienteService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals("Juan", resultado.nombre());
    }

    @Test
    void obtenerPorId_DeberiaLanzarExcepcion_CuandoNoExiste() {
        Mockito.when(pacienteRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            pacienteService.obtenerPorId(99L);
        });

        assertEquals("Paciente no encontrado con ID: 99", excepcion.getMessage());
    }

    // 🔴 NUEVO: Cubre la línea del operador ternario cuando el estado es null
    @Test
    void obtenerPorId_DeberiaAsignarActivo_CuandoEstadoEsNull() {
        pacienteSimulado.setEstado(null);
        Mockito.when(pacienteRepository.findById(1L)).thenReturn(Optional.of(pacienteSimulado));

        PacienteResponseDto resultado = pacienteService.obtenerPorId(1L);

        assertEquals(EstadoPaciente.ACTIVO, resultado.estado());
    }

    @Test
    void actualizarCompleto_DeberiaActualizarYRetornar() {
        Mockito.when(pacienteRepository.findById(1L)).thenReturn(Optional.of(pacienteSimulado));
        Mockito.when(pacienteRepository.save(any(Paciente.class))).thenReturn(pacienteSimulado);

        PacienteResponseDto resultado = pacienteService.actualizarCompleto(1L, requestDto);

        assertNotNull(resultado);
        Mockito.verify(pacienteRepository).save(pacienteSimulado);
    }

    @Test
    void actualizarCompleto_DeberiaLanzarExcepcion_CuandoNoExiste() {
        Mockito.when(pacienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> pacienteService.actualizarCompleto(99L, requestDto));
    }

    @Test
    void actualizarParcial_DeberiaModificarTodosLosCamposDelSwitch() {
        Mockito.when(pacienteRepository.findById(1L)).thenReturn(Optional.of(pacienteSimulado));
        Mockito.when(pacienteRepository.save(any(Paciente.class))).thenReturn(pacienteSimulado);

        // 🔴 NUEVO: Evaluamos todas las opciones del switch para no perder cobertura
        Map<String, Object> campos = Map.of(
                "nombre", "Pedro",
                "apellido", "Gomez",
                "email", "pedro@test.com",
                "rut", "11.111.111-1"
        );

        PacienteResponseDto resultado = pacienteService.actualizarParcial(1L, campos);

        assertNotNull(resultado);
        assertEquals("Pedro", pacienteSimulado.getNombre());
        assertEquals("Gomez", pacienteSimulado.getApellido());
        assertEquals("pedro@test.com", pacienteSimulado.getEmail());

        // 🔴 AQUÍ ESTÁ TU CORRECCIÓN DEL RUT APLICADA
        assertEquals("11111111-1", pacienteSimulado.getRut());
    }

    @Test
    void actualizarParcial_DeberiaLanzarExcepcion_CuandoNoExiste() {
        Mockito.when(pacienteRepository.findById(99L)).thenReturn(Optional.empty());
        Map<String, Object> campos = Map.of("nombre", "Pedro");

        assertThrows(IllegalArgumentException.class, () -> pacienteService.actualizarParcial(99L, campos));
    }

    @Test
    void eliminar_DeberiaEliminar_CuandoExiste() {
        Mockito.when(pacienteRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> pacienteService.eliminar(1L));
        Mockito.verify(pacienteRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void eliminar_DeberiaLanzarExcepcion_CuandoNoExiste() {
        Mockito.when(pacienteRepository.existsById(99L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> pacienteService.eliminar(99L));
    }
}