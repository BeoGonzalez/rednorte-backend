package com.example.security.dto;

public class RegisterRequestDto {
    private String username;
    private String password;
    private String rol;
    
    // Campo opcional para perfil de paciente
    private String rut;
    
    // Campos opcionales para perfil médico
    private String nombre;
    private String apellidos;
    private String specialty;
    private String registroMedico;

    // Getters y Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public String getRegistroMedico() { return registroMedico; }
    public void setRegistroMedico(String registroMedico) { this.registroMedico = registroMedico; }
}