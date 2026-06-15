package com.example.pacientes.service;

import com.example.pacientes.dto.PacienteResponseDto;
import com.example.pacientes.dto.PacienteRequestDto;
import com.example.pacientes.exception.ResourceNotFoundException;
import com.example.pacientes.model.Paciente;
import com.example.pacientes.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.example.pacientes.model.EstadoPaciente;

/**
 * Servicio de negocio para la gestión integral de pacientes.
 * <p>
 * Este servicio orquesta la persistencia en base de datos y la gestión de caché distribuida
 * mediante Redis. Se encarga de transformar entidades de dominio a objetos DTO
 * y asegurar la consistencia entre los datos persistidos y los datos cacheados.
 * </p>
 */
@Service
public class PacienteService {
    private final PacienteRepository pacienteRepository;

    @Autowired
    public PacienteService(PacienteRepository pacienteRepository){
        this.pacienteRepository = pacienteRepository;
    }

    /**
     * Mapea una entidad {@link Paciente} a un objeto de respuesta {@link PacienteResponseDto}.
     * <p>
     * Aplica la conversión del estado del paciente, asignando 'ACTIVO' por defecto si
     * el valor es nulo.
     * </p>
     * * @param paciente Entidad de base de datos.
     * @return DTO enriquecido para la capa de presentación.
     */
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

    /**
     * Recupera todos los pacientes registrados.
     * <p>
     * Resultado cacheados bajo la región {@code pacientes_lista}.
     * </p>
     * * @return Lista completa de pacientes mapeados.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "pacientes_lista")
    public List<PacienteResponseDto> obtenerTodos() {
        System.out.println("⏳ Cache Miss: Consultando todos los pacientes en PostgreSQL...");
        return pacienteRepository.findAll()
                .stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    /**
     * Registra un nuevo paciente.
     * <p>
     * Invalida las listas generales cacheadas para evitar inconsistencias y
     * almacena el nuevo registro individual en la caché {@code paciente}.
     * </p>
     * * @param dto Objeto de solicitud con los datos del paciente.
     * @return Paciente creado.
     */
    @Transactional
    @Caching(
            put = { @CachePut(value = "paciente", key = "#result.id") },
            evict = {
                    @CacheEvict(value = "pacientes_lista", allEntries = true),
                    @CacheEvict(value = "pacientes_estado", allEntries = true)
            }
    )
    public PacienteResponseDto crear(PacienteRequestDto dto) {
        Paciente paciente = new Paciente();

        paciente.setRut(dto.rut().replace(".", "").toUpperCase());
        paciente.setNombre(dto.nombre());
        paciente.setApellido(dto.apellido());
        paciente.setEmail(dto.email());

        Paciente guardado = pacienteRepository.save(paciente);
        return mapearADto(guardado);
    }

    /**
     * Busca pacientes filtrados por estado.
     * <p>
     * Utiliza la región de caché {@code pacientes_estado} indexada por el parámetro estado.
     * </p>
     * * @param estado Estado del paciente (ej: ACTIVO).
     * @return Lista filtrada de pacientes.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "pacientes_estado", key = "#estado")
    public List<PacienteResponseDto> obtenerPorEstado(String estado) {
        System.out.println("⏳ Cache Miss: Consultando pacientes con estado " + estado + " en PostgreSQL...");
        return pacienteRepository.findByEstado(estado)
                .stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un paciente por su ID.
     * <p>
     * Realiza una consulta directa a Redis mediante la región {@code paciente}.
     * </p>
     * * @param id Identificador único del paciente.
     * @return Paciente encontrado.
     * @throws IllegalArgumentException si no existe el paciente.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "paciente", key = "#id")
    public PacienteResponseDto obtenerPorId(Long id) {
        System.out.println("⏳ Cache Miss: Consultando paciente ID " + id + " en PostgreSQL...");
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con ID: " + id));

        return mapearADto(paciente);
    }

    /**
     * Actualiza la información completa de un paciente.
     * <p>
     * Actualiza la caché individual y purga todas las listas generales para asegurar
     * que los cambios se reflejen en futuras peticiones.
     * </p>
     * * @param id Identificador del paciente.
     * @param dto Objeto con los nuevos datos.
     * @return Paciente actualizado.
     */
    @Transactional
    @Caching(
            put = { @CachePut(value = "paciente", key = "#id") },
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

        Paciente pacienteActualizado = pacienteRepository.save(paciente);
        return mapearADto(pacienteActualizado);
    }

    /**
     * Realiza una actualización parcial de campos seleccionados del paciente.
     * <p>
     * Mantiene la integridad de la caché invalidando listas y actualizando el perfil individual.
     * </p>
     * * @param id Identificador del paciente.
     * @param campos Mapa de atributos a modificar.
     * @return Paciente actualizado.
     */
    @Transactional
    @Caching(
            put = { @CachePut(value = "paciente", key = "#id") },
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
                case "nombre" -> paciente.setNombre((String) valor);
                case "apellido" -> paciente.setApellido((String) valor);
                case "email" -> paciente.setEmail((String) valor);
                case "rut" -> paciente.setRut(((String) valor).replace(".", "").toUpperCase());
            }
        });

        Paciente pacienteActualizado = pacienteRepository.save(paciente);
        return mapearADto(pacienteActualizado);
    }

    /**
     * Elimina un paciente y purga todas las entradas relacionadas en caché.
     * * @param id Identificador del paciente.
     */
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "paciente", key = "#id"),
                    @CacheEvict(value = "pacientes_lista", allEntries = true),
                    @CacheEvict(value = "pacientes_estado", allEntries = true)
            }
    )
    public void eliminar(Long id) {
        if (!pacienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Paciente no encontrado con ID: " + id);
        }
        pacienteRepository.deleteById(id);
    }
}
