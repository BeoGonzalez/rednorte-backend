package com.example.pacientes.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pacientes")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to the security microservice user (ms-security Usuario.id)
    @Column(name = "auth_id", unique = true)
    private Long authId;

    @Column(unique = true, nullable = false, length = 12)
    private String rut;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String nombre;

    private String apellido;

    private String password;

    @Column(nullable = false)
    private String rol = "ROLE_PACIENTE";

    @Column(name = "estado", length = 20)
    private String estado = "ACTIVO";

    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fecharegistro;

    @PrePersist
    protected void onCreate() {
        this.fecharegistro = LocalDateTime.now();
    }

    public Paciente() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAuthId() { return authId; }
    public void setAuthId(Long authId) { this.authId = authId; }

    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public LocalDateTime getFecharegistro() { return fecharegistro; }
    public void setFecharegistro(LocalDateTime fecharegistro) { this.fecharegistro = fecharegistro; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
