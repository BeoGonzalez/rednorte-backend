package com.example.pacientes.service;

import com.example.pacientes.dto.PacienteRequestDto;
import com.example.pacientes.dto.PacienteResponseDto;
import com.example.pacientes.exception.ResourceNotFoundException;
import com.example.pacientes.model.EstadoPaciente;
import com.example.pacientes.model.Paciente;
import com.example.pacientes.repository.PacienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PacienteServiceTest {

    @Mock
    private PacienteRepository repository;

    @InjectMocks
    private PacienteService service;

    @Test
    void testObtenerPaciente_NoExiste_LanzaExcepcion() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.obtenerPorId(1L));
    }

    @Test
    void testCrearPaciente_AsignaEstadoActivoPorDefecto() {
        // 1. Instanciamos el record (Request) usando el constructor
        PacienteRequestDto dto = new PacienteRequestDto(
                "12.345.678-9",
                "Juan",
                "Perez",
                "juan@test.com"
        );

        // 2. Mockear el repositorio para que devuelva el objeto que recibe (simulando guardado)
        when(repository.save(any(Paciente.class))).thenAnswer(i -> i.getArguments()[0]);

        // 3. LLAMADA AL SERVICIO
        // Asegúrate de que el método 'crear' en tu servicio realmente devuelva PacienteResponseDto
        PacienteResponseDto creado = service.crear(dto);

        // 4. Verificaciones
        assertNotNull(creado);

        // CORRECCIÓN: Al ser record, se accede al campo directamente con el nombre del método de acceso.
        // Si en tu record el campo se llama 'estado', el método es 'estado()'
        assertEquals(EstadoPaciente.ACTIVO, creado.estado());

        verify(repository, times(1)).save(any(Paciente.class));
    }
}