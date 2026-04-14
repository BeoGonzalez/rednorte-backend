package com.example.pacientes.service;

import com.example.pacientes.model.Paciente;
import com.example.pacientes.repository.PacienteRepository;
import com.example.pacientes.security.PacienteUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PacienteUserDetailsService implements UserDetailsService {

    private final PacienteRepository repository;

    //Inyeccion de dependencias dentro de constructor
    @Autowired
    public PacienteUserDetailsService(PacienteRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Paciente paciente = repository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Email del paciente no encontrado: " + email));
        return new PacienteUserDetails(paciente);
    }
}

