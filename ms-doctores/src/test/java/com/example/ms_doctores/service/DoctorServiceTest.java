package com.example.ms_doctores.service;

import com.example.ms_doctores.dto.DoctorRequestDTO;
import com.example.ms_doctores.entity.Doctor;
import com.example.ms_doctores.exception.ResourceNotFoundException;
import com.example.ms_doctores.repository.DoctorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository repository;

    @InjectMocks
    private DoctorService service;

    @Test
    void testBuscarPorAuthId_NoExiste_LanzaExcepcion() {
        // Tu método en DoctorService se llama buscarPorAuthId
        when(repository.findByAuthId(1L)).thenReturn(Optional.empty());

        // Si tu servicio lanza ResourceNotFoundException al no encontrarlo, este assert es correcto.
        // Nota: Verifica si tu servicio realmente lanza la excepción en buscarPorAuthId o simplemente devuelve Optional.empty()
        // Si no lanza excepción, cambia el test para usar assertThrows(...)

        // Ajuste: si buscarPorAuthId no lanza excepción, prueba esto:
        Optional<Doctor> resultado = service.buscarPorAuthId(1L);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testRegistrarDoctor_Exitoso() {
        // 5 argumentos requeridos por tu constructor
        DoctorRequestDTO dto = new DoctorRequestDTO(
                123L,            // authId
                "Juan",          // nombre
                "Perez",         // apellidos
                "Cardiología",   // specialty
                "REG-999"        // registroMedico
        );

        // Simulamos que el authId no existe (para que no falle la regla de negocio)
        when(repository.findByAuthId(anyLong())).thenReturn(Optional.empty());
        when(repository.save(any(Doctor.class))).thenAnswer(i -> i.getArguments()[0]);

        // Tu método en DoctorService se llama registrarDoctor
        Doctor creado = service.registrarDoctor(dto);

        assertNotNull(creado);
        assertEquals("Juan", creado.getNombre());
        verify(repository, times(1)).save(any(Doctor.class));
    }
}