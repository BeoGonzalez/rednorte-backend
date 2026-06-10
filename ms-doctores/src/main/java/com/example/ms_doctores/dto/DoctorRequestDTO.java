package com.example.ms_doctores.dto;

public class DoctorRequestDTO {

    private Long authId;
    private String nombre;
    private String apellidos;
    private String specialty; // Mantenemos consistencia con tu modelo interno
    private String registroMedico;

    // --- Constructor vacío exigido por Jackson ---
    public DoctorRequestDTO() {
    }

    public DoctorRequestDTO(Long authId, String nombre, String apellidos, String specialty, String registroMedico) {
        this.authId = authId;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.specialty = specialty;
        this.registroMedico = registroMedico;
    }

    // --- Getters y Setters Explícitos ---
    public Long getAuthId() { return authId; }
    public void setAuthId(Long authId) { this.authId = authId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public String getRegistroMedico() { return registroMedico; }
    public void setRegistroMedico(String registroMedico) { this.registroMedico = registroMedico; }
}
