package com.example.ms_doctores.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "doctores")
public class Doctor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ESTO ES CLAVE: Aquí guardamos el ID que generó ms-security
    @Column(name = "auth_id", nullable = false, unique = true)
    private Long authId;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellidos;

    @Column(nullable = false)
    private String especialidad;

    @Column(name = "registro_medico", unique = true)
    private String registroMedico; // Número de registro nacional (Superintendencia de Salud)

    // --- Constructores ---
    public Doctor() {
    }

    public Doctor(Long authId, String nombre, String apellidos, String especialidad, String registroMedico) {
        this.authId = authId;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.especialidad = especialidad;
        this.registroMedico = registroMedico;
    }

    // --- Getters y Setters explícitos ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAuthId() { return authId; }
    public void setAuthId(Long authId) { this.authId = authId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public String getRegistroMedico() { return registroMedico; }
    public void setRegistroMedico(String registroMedico) { this.registroMedico = registroMedico; }
}
