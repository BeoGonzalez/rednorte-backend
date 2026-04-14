package com.example.pacientes.service;

import com.example.pacientes.dto.LoginPacienteDto;
import com.example.pacientes.dto.TokenResponseDto;
import com.example.pacientes.dto.PacienteResponseDto;
import com.example.pacientes.dto.RegistroPacienteDto;
import com.example.pacientes.model.Paciente;
import com.example.pacientes.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PacienteService {
    private final PacienteRepository pacienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Autowired
    public PacienteService(PacienteRepository pacienteRepository, PasswordEncoder passwordEncoder, JwtService jwtService){
        this.pacienteRepository = pacienteRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    private PacienteResponseDto mapearADto(Paciente paciente) {
        return new PacienteResponseDto(
                paciente.getId(),
                paciente.getRut(),
                paciente.getEmail(),
                paciente.getNombre(),
                paciente.getApellido(),
                paciente.getFecharegistro()
        );
    }

    public PacienteResponseDto registrarPaciente(RegistroPacienteDto dto) {
        if (pacienteRepository.findByEmail(dto.email()).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado en RedNorte");
        }

        // NUEVO: Limpiamos el RUT (quitamos puntos y forzamos mayúscula para la K)
        String rutLimpio = dto.rut().replace(".", "").toUpperCase();

        Paciente paciente = new Paciente();
        paciente.setRut(rutLimpio);
        paciente.setEmail(dto.email());
        paciente.setNombre(dto.nombre());
        paciente.setApellido(dto.apellido());
        paciente.setPassword(dto.password());

        // NUEVO: Encriptamos la contraseña usando BCrypt ANTES de guardarla
        paciente.setPassword(passwordEncoder.encode(dto.password()));

        Paciente pacienteGuardado = pacienteRepository.save(paciente);

        return new PacienteResponseDto(
                pacienteGuardado.getId(),
                pacienteGuardado.getRut(),
                pacienteGuardado.getEmail(),
                pacienteGuardado.getNombre(),
                pacienteGuardado.getApellido(),
                pacienteGuardado.getFecharegistro()
        );
    }
    public TokenResponseDto login(LoginPacienteDto dto) {
        // 1. Buscamos al paciente
        Paciente paciente = pacienteRepository.findByEmail(dto.email())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales incorrectas"));

        // 2. Comparamos la contraseña usando BCrypt
        if (!passwordEncoder.matches(dto.password(), paciente.getPassword())) {
            throw new IllegalArgumentException("Credenciales incorrectas");
        }

        // 3. Generamos el Token
        String token = jwtService.generarToken(paciente.getEmail());
        return new TokenResponseDto(token, "Bearer");
    }

    public List<PacienteResponseDto> obtenerTodos() {
        return pacienteRepository.findAll()
                .stream()
                .map(this::mapearADto) // Convertimos cada Entidad a DTO
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

    public PacienteResponseDto actualizarCompleto(Long id, RegistroPacienteDto dto) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado con ID: " + id));

        paciente.setRut(dto.rut().replace(".", "").toUpperCase());
        paciente.setEmail(dto.email());
        paciente.setNombre(dto.nombre());
        paciente.setApellido(dto.apellido());

        // Solo si envían una nueva contraseña, la encriptamos
        if (dto.password() != null && !dto.password().isBlank()) {
            paciente.setPassword(passwordEncoder.encode(dto.password()));
        }

        Paciente pacienteActualizado = pacienteRepository.save(paciente);
        return mapearADto(pacienteActualizado);
    }

    public PacienteResponseDto actualizarParcial(Long id, Map<String, Object> campos) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado con ID: " + id));

        // Iteramos sobre el mapa. Usamos un switch para controlar exactamente qué campos se pueden editar.
        // Esto evita vulnerabilidades donde alguien intente modificar el ID o el Password por esta vía.
        campos.forEach((clave, valor) -> {
            switch (clave) {
                case "nombre":
                    paciente.setNombre((String) valor);
                    break;
                case "apellido":
                    paciente.setApellido((String) valor);
                    break;
                case "email":
                    paciente.setEmail((String) valor);
                    break;
                case "rut":
                    paciente.setRut(((String) valor).replace(".", "").toUpperCase());
                    break;
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
