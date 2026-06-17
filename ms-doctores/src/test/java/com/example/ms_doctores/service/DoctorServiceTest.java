package com.example.ms_doctores.service;

import com.example.ms_doctores.dto.DoctorRequestDTO;
import com.example.ms_doctores.entity.Doctor;
import com.example.ms_doctores.exception.BusinessRuleException;
import com.example.ms_doctores.exception.ResourceNotFoundException;
import com.example.ms_doctores.repository.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorService service;

    private Doctor doctorMock;
    private DoctorRequestDTO dto;

    @BeforeEach
    void setUp() {
        dto = new DoctorRequestDTO(123L, "Juan", "Pérez", "Cardiología", "REG-999");

        doctorMock = new Doctor();
        doctorMock.setId(1L);
        doctorMock.setAuthId(123L);
        doctorMock.setNombre("Juan");
        doctorMock.setApellidos("Pérez");
        doctorMock.setEspecialidad("Cardiología");
        doctorMock.setRegistroMedico("REG-999");
    }

    // ─── registrarDoctor ─────────────────────────────────────────
    @Test
    void registrarDoctor_exitoso_deberiaGuardarYRetornarDoctor() {
        // Given
        when(doctorRepository.findByAuthId(123L)).thenReturn(Optional.empty());
        when(doctorRepository.save(any(Doctor.class))).thenAnswer(i -> i.getArgument(0));

        // When
        Doctor creado = service.registrarDoctor(dto);

        // Then
        assertThat(creado).isNotNull();
        assertThat(creado.getNombre()).isEqualTo("Juan");
        assertThat(creado.getEspecialidad()).isEqualTo("Cardiología");
        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    void registrarDoctor_authIdDuplicado_deberiaLanzarBusinessRuleException() {
        // Given
        when(doctorRepository.findByAuthId(123L)).thenReturn(Optional.of(doctorMock));

        // When / Then
        assertThatThrownBy(() -> service.registrarDoctor(dto))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("ya está registrado");

        verify(doctorRepository, never()).save(any());
    }

    // ─── buscarPorAuthId ─────────────────────────────────────────
    @Test
    void buscarPorAuthId_existente_deberiaRetornarOptionalPresente() {
        // Given
        when(doctorRepository.findByAuthId(123L)).thenReturn(Optional.of(doctorMock));

        // When
        Optional<Doctor> resultado = service.buscarPorAuthId(123L);

        // Then
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getAuthId()).isEqualTo(123L);
    }

    @Test
    void buscarPorAuthId_noExistente_deberiaRetornarOptionalVacio() {
        // Given
        when(doctorRepository.findByAuthId(1L)).thenReturn(Optional.empty());

        // When
        Optional<Doctor> resultado = service.buscarPorAuthId(1L);

        // Then
        assertThat(resultado).isEmpty();
    }

    // ─── buscarPorEspecialidad ────────────────────────────────────
    @Test
    void buscarPorEspecialidad_deberiaRetornarListaDeDoctores() {
        // Given
        when(doctorRepository.findByEspecialidad("Cardiología")).thenReturn(List.of(doctorMock));

        // When
        List<Doctor> resultado = service.buscarPorEspecialidad("Cardiología");

        // Then
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEspecialidad()).isEqualTo("Cardiología");
        verify(doctorRepository).findByEspecialidad("Cardiología");
    }

    @Test
    void buscarPorEspecialidad_sinResultados_deberiaRetornarListaVacia() {
        // Given
        when(doctorRepository.findByEspecialidad("Neurología")).thenReturn(List.of());

        // When
        List<Doctor> resultado = service.buscarPorEspecialidad("Neurología");

        // Then
        assertThat(resultado).isEmpty();
    }

    // ─── actualizarDoctor ─────────────────────────────────────────
    @Test
    void actualizarDoctor_exitoso_deberiaActualizarCampos() {
        // Given
        when(doctorRepository.findByAuthId(123L)).thenReturn(Optional.of(doctorMock));
        when(doctorRepository.save(any(Doctor.class))).thenAnswer(i -> i.getArgument(0));

        DoctorRequestDTO updateDto = new DoctorRequestDTO(123L, "Carlos", "Soto", "Neurología", "REG-001");

        // When
        Doctor actualizado = service.actualizarDoctor(updateDto);

        // Then
        assertThat(actualizado.getNombre()).isEqualTo("Carlos");
        assertThat(actualizado.getEspecialidad()).isEqualTo("Neurología");
        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    void actualizarDoctor_noExistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(doctorRepository.findByAuthId(anyLong())).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> service.actualizarDoctor(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("no encontrado");

        verify(doctorRepository, never()).save(any());
    }
}
