package com.example.pacientes.repository;

import com.example.pacientes.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente> findByEmail(String email);

    Optional<Paciente> findByRut(String rut);

    List<Paciente> findByEstado(String estado);
}
