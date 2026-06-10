package com.example.ms_doctores.service;

import com.example.ms_doctores.dto.DoctorRequestDTO;
import com.example.ms_doctores.entity.Doctor;
import com.example.ms_doctores.repository.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Transactional
    public Doctor registrarDoctor(DoctorRequestDTO dto) {
        // Aquí puedes meter lógica de negocio avanzada en el futuro
        // Ejemplo: Validar con ms-security si el authId realmente existe y tiene el rol médico

        Optional<Doctor> existente = doctorRepository.findByAuthId(dto.getAuthId());
        if (existente.isPresent()) {
            throw new RuntimeException("El médico con este ID de autenticación ya está registrado");
        }

        // Mapeo manual (Seguro, explícito y sin librerías mágicas)
        Doctor doctor = new Doctor();
        doctor.setAuthId(dto.getAuthId());
        doctor.setNombre(dto.getNombre());
        doctor.setApellidos(dto.getApellidos());
        doctor.setEspecialidad(dto.getSpecialty());
        doctor.setRegistroMedico(dto.getRegistroMedico());

        return doctorRepository.save(doctor);
    }

    @Transactional(readOnly = true)
    public Optional<Doctor> buscarPorAuthId(Long authId) {
        return doctorRepository.findByAuthId(authId);
    }
}
