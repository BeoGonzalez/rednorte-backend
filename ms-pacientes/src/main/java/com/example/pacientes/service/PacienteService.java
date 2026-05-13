package com.example.pacientes.service;

import com.example.pacientes.dto.PacienteResponseDto;
import com.example.pacientes.dto.PacienteRequestDto;
import com.example.pacientes.model.Paciente;
import com.example.pacientes.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.example.pacientes.model.EstadoPaciente;

@Service
public class PacienteService {
    private final PacienteRepository pacienteRepository;

    @Autowired
    public PacienteService(PacienteRepository pacienteRepository){
        this.pacienteRepository = pacienteRepository;
    }

    private PacienteResponseDto mapearADto(Paciente paciente) {
        EstadoPaciente estadoEnum = paciente.getEstado() != null
                ? EstadoPaciente.valueOf(paciente.getEstado().toUpperCase())
                : EstadoPaciente.ACTIVO;
        return new PacienteResponseDto(
                paciente.getId(),
                paciente.getRut(),
                paciente.getEmail(),
                paciente.getNombre(),
                paciente.getApellido(),
                estadoEnum,
                paciente.getFecharegistro()
        );
    }

    public List<PacienteResponseDto> obtenerTodos() {
        return pacienteRepository.findAll()
                .stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    public PacienteResponseDto obtenerPorId(Long id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado con ID: " + id));
        return mapearADto(paciente);
    }

    public List<PacienteResponseDto> obtenerPorEstado(String estado) {
        return pacienteRepository.findByEstado(estado)
                .stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    public PacienteResponseDto actualizarCompleto(Long id, PacienteRequestDto dto) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado con ID: " + id));

        paciente.setRut(dto.rut().replace(".", "").toUpperCase());
        paciente.setEmail(dto.email());
        paciente.setNombre(dto.nombre());
        paciente.setApellido(dto.apellido());

        Paciente pacienteActualizado = pacienteRepository.save(paciente);
        return mapearADto(pacienteActualizado);
    }

    public PacienteResponseDto actualizarParcial(Long id, Map<String, Object> campos) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado con ID: " + id));

        campos.forEach((clave, valor) -> {
            switch (clave) {
                case "nombre" -> paciente.setNombre((String) valor);
                case "apellido" -> paciente.setApellido((String) valor);
                case "email" -> paciente.setEmail((String) valor);
                case "rut" -> paciente.setRut(((String) valor).replace(".", "").toUpperCase());
            }
        });

        Paciente pacienteActualizado = pacienteRepository.save(paciente);
        return mapearADto(pacienteActualizado);
    }

    public void eliminar(Long id) {
        if (!pacienteRepository.existsById(id)) {
            throw new IllegalArgumentException("Paciente no encontrado con ID: " + id);
        }
        pacienteRepository.deleteById(id);
    }
}