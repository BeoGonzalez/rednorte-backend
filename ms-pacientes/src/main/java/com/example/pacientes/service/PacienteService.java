package com.example.pacientes.service;

import com.example.pacientes.dto.NotificacionDto;
import com.example.pacientes.dto.PacienteResponseDto;
import com.example.pacientes.dto.PacienteRequestDto;
import com.example.pacientes.exception.ResourceNotFoundException;
import com.example.pacientes.model.EstadoPaciente;
import com.example.pacientes.model.Notificacion;
import com.example.pacientes.model.Paciente;
import com.example.pacientes.repository.NotificacionRepository;
import com.example.pacientes.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.pacientes.exception.BusinessRuleException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final NotificacionRepository notificacionRepository;

    @Autowired
    public PacienteService(PacienteRepository pacienteRepository,
                           NotificacionRepository notificacionRepository) {
        this.pacienteRepository = pacienteRepository;
        this.notificacionRepository = notificacionRepository;
    }

    private PacienteResponseDto mapearADto(Paciente paciente) {
        EstadoPaciente estadoEnum = paciente.getEstado() != null
                ? EstadoPaciente.valueOf(paciente.getEstado().toUpperCase())
                : EstadoPaciente.ACTIVO;
        return new PacienteResponseDto(
                paciente.getId(),
                paciente.getAuthId(),
                paciente.getRut(),
                paciente.getEmail(),
                paciente.getNombre(),
                paciente.getApellido(),
                estadoEnum,
                paciente.getFecharegistro()
        );
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "pacientes_lista")
    public List<PacienteResponseDto> obtenerTodos() {
        return pacienteRepository.findAll().stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "paciente", key = "#id")
    public PacienteResponseDto obtenerPorId(Long id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con ID: " + id));
        return mapearADto(paciente);
    }

    @Transactional(readOnly = true)
    public PacienteResponseDto obtenerPorAuthId(Long authId) {
        Paciente paciente = pacienteRepository.findByAuthId(authId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con authId: " + authId));
        return mapearADto(paciente);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "pacientes_estado", key = "#estado")
    public List<PacienteResponseDto> obtenerPorEstado(String estado) {
        return pacienteRepository.findByEstado(estado).stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Caching(
            put  = { @CachePut(value = "paciente", key = "#result.id") },
            evict = {
                    @CacheEvict(value = "pacientes_lista", allEntries = true),
                    @CacheEvict(value = "pacientes_estado", allEntries = true)
            }
    )
    public PacienteResponseDto crear(PacienteRequestDto dto) {
        if (dto.authId() != null) {
            pacienteRepository.findByAuthId(dto.authId()).ifPresent(existing -> {
                throw new BusinessRuleException("El perfil ya ha sido creado para authId: " + dto.authId());
            });
        }
        Paciente paciente = new Paciente();
        if (dto.authId() != null) paciente.setAuthId(dto.authId());
        paciente.setRut(dto.rut().replace(".", "").toUpperCase());
        paciente.setNombre(dto.nombre());
        paciente.setApellido(dto.apellido());
        paciente.setEmail(dto.email());
        return mapearADto(pacienteRepository.save(paciente));
    }

    @Transactional
    @Caching(
            put  = { @CachePut(value = "paciente", key = "#id") },
            evict = {
                    @CacheEvict(value = "pacientes_lista", allEntries = true),
                    @CacheEvict(value = "pacientes_estado", allEntries = true)
            }
    )
    public PacienteResponseDto actualizarCompleto(Long id, PacienteRequestDto dto) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con ID: " + id));
        paciente.setRut(dto.rut().replace(".", "").toUpperCase());
        paciente.setEmail(dto.email());
        paciente.setNombre(dto.nombre());
        paciente.setApellido(dto.apellido());
        return mapearADto(pacienteRepository.save(paciente));
    }

    @Transactional
    @Caching(
            put  = { @CachePut(value = "paciente", key = "#id") },
            evict = {
                    @CacheEvict(value = "pacientes_lista", allEntries = true),
                    @CacheEvict(value = "pacientes_estado", allEntries = true)
            }
    )
    public PacienteResponseDto actualizarParcial(Long id, Map<String, Object> campos) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con ID: " + id));
        campos.forEach((clave, valor) -> {
            switch (clave) {
                case "nombre"   -> paciente.setNombre((String) valor);
                case "apellido" -> paciente.setApellido((String) valor);
                case "email"    -> paciente.setEmail((String) valor);
                case "rut"      -> paciente.setRut(((String) valor).replace(".", "").toUpperCase());
            }
        });
        return mapearADto(pacienteRepository.save(paciente));
    }

    @Caching(evict = {
            @CacheEvict(value = "paciente", key = "#id"),
            @CacheEvict(value = "pacientes_lista", allEntries = true),
            @CacheEvict(value = "pacientes_estado", allEntries = true)
    })
    @Transactional(readOnly = true)
    public List<NotificacionDto> obtenerNotificaciones(Long pacienteId) {
        return notificacionRepository.findByPacienteIdOrderByFechaCreacionDesc(pacienteId)
                .stream()
                .map(n -> new NotificacionDto(n.getId(), n.getPacienteId(), n.getMensaje(), n.isLeida(), n.getFechaCreacion()))
                .collect(Collectors.toList());
    }

    public void eliminar(Long id) {
        if (!pacienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Paciente no encontrado con ID: " + id);
        }
        pacienteRepository.deleteById(id);
    }
}
