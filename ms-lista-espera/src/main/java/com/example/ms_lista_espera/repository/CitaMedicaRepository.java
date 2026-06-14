package com.example.ms_lista_espera.repository;

import com.example.ms_lista_espera.model.CitaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitaMedicaRepository extends JpaRepository<CitaMedica, Long> {
    List<CitaMedica> findByPacienteId(Long pacienteId);
    List<CitaMedica> findByDoctorId(Long doctorId);
}
