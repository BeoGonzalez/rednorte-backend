package com.example.ms_doctores.service;

import com.example.ms_doctores.dto.DoctorRequestDTO;
import com.example.ms_doctores.entity.Doctor;
import com.example.ms_doctores.exception.BusinessRuleException;
import com.example.ms_doctores.exception.ResourceNotFoundException;
import com.example.ms_doctores.repository.DoctorRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio encargado de la lógica de negocio para la gestión de perfiles médicos.
 * <p>
 * Este servicio implementa estrategias de caché distribuida (Redis) para optimizar
 * las operaciones de lectura y garantizar la consistencia de los datos tras las
 * modificaciones (creación y actualización).
 * </p>
 */
@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    /**
     * Constructor para la inyección de dependencias.
     *
     * @param doctorRepository Repositorio de persistencia de la entidad Doctor.
     */
    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    /**
     * Registra un nuevo médico en el sistema.
     * <p>
     * Esta operación:
     * <ul>
     * <li>Persiste la entidad en la base de datos.</li>
     * <li>Almacena el perfil recién creado en la caché {@code doctor_auth}.</li>
     * <li>Limpia la caché de {@code doctores_especialidad} para evitar datos inconsistentes.</li>
     * </ul>
     * </p>
     *
     * @param dto Datos del médico a registrar.
     * @return La entidad {@link Doctor} creada.
     * @throws RuntimeException Si el ID de autenticación ya existe en el sistema.
     */
    @Transactional
    @Caching(
            put = { @CachePut(value = "doctor_auth", key = "#dto.authId") },
            evict = { @CacheEvict(value = "doctores_especialidad", allEntries = true) }
    )
    public Doctor registrarDoctor(DoctorRequestDTO dto) {
        Optional<Doctor> existente = doctorRepository.findByAuthId(dto.getAuthId());
        if (existente.isPresent()) {
            throw new BusinessRuleException("El médico con este ID de autenticación ya está registrado");
        }

        Doctor doctor = new Doctor();
        doctor.setAuthId(dto.getAuthId());
        doctor.setNombre(dto.getNombre());
        doctor.setApellidos(dto.getApellidos());
        doctor.setEspecialidad(dto.getSpecialty());
        doctor.setRegistroMedico(dto.getRegistroMedico());

        return doctorRepository.save(doctor);
    }

    /**
     * Recupera un médico por su identificador de autenticación.
     * <p>
     * Utiliza una estrategia de lectura desde caché (Cache-aside). Si el dato existe
     * en Redis, se retorna instantáneamente; de lo contrario, se consulta a PostgreSQL.
     * </p>
     *
     * @param authId El identificador de autenticación del médico.
     * @return Un {@link Optional} que contiene al doctor si fue encontrado.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "doctor_auth", key = "#authId")
    public Optional<Doctor> buscarPorAuthId(Long authId) {
        System.out.println("⏳ Cache Miss: Consultando a PostgreSQL por el doctor con authId " + authId + "...");
        return doctorRepository.findByAuthId(authId);
    }

    /**
     * Recupera una lista de médicos agrupados por su especialidad.
     * <p>
     * El resultado de la consulta se almacena en la caché {@code doctores_especialidad}
     * para optimizar las consultas frecuentes por filtro de especialidad.
     * </p>
     *
     * @param especialidad El nombre de la especialidad para realizar el filtrado.
     * @return Lista de entidades {@link Doctor} que coinciden con la especialidad.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "doctores_especialidad", key = "#especialidad")
    public List<Doctor> buscarPorEspecialidad(String especialidad) {
        System.out.println("⏳ Cache Miss: Consultando a PostgreSQL por la especialidad " + especialidad + "...");
        return doctorRepository.findByEspecialidad(especialidad);
    }

    /**
     * Actualiza los datos profesionales de un médico existente.
     * <p>
     * Utiliza {@link CachePut} para actualizar la caché individual en memoria RAM
     * inmediatamente después de la escritura en BD, y {@link CacheEvict} para purgar
     * las listas generales y evitar lecturas obsoletas.
     * </p>
     *
     * @param dto Datos actualizados del médico.
     * @return La entidad {@link Doctor} actualizada.
     * @throws RuntimeException Si el doctor a actualizar no existe.
     */
    @Transactional
    @Caching(
            put = { @CachePut(value = "doctor_auth", key = "#dto.authId") },
            evict = { @CacheEvict(value = "doctores_especialidad", allEntries = true) }
    )
    public Doctor actualizarDoctor(DoctorRequestDTO dto) {

        Doctor doctorExistente = doctorRepository.findByAuthId(dto.getAuthId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor no encontrado para actualizar"));

        doctorExistente.setNombre(dto.getNombre());
        doctorExistente.setApellidos(dto.getApellidos());
        doctorExistente.setEspecialidad(dto.getSpecialty());
        doctorExistente.setRegistroMedico(dto.getRegistroMedico());

        return doctorRepository.save(doctorExistente);
    }
}
