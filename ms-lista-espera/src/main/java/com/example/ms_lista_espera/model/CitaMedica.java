package com.example.ms_lista_espera.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "citas_medicas")
public class CitaMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long solicitudId;

    @Column(nullable = false)
    private Long pacienteId;

    @Column(nullable = false)
    private Long doctorId;

    @Column(nullable = false)
    private LocalDateTime fechaAsignacion = LocalDateTime.now();

    @Column(nullable = false)
    private String estado = "AGENDADA"; // AGENDADA, COMPLETADA, CANCELADA

    // Constructores
    public CitaMedica() {}

    public CitaMedica(Long solicitudId, Long pacienteId, Long doctorId) {
        this.solicitudId = solicitudId;
        this.pacienteId = pacienteId;
        this.doctorId = doctorId;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSolicitudId() { return solicitudId; }
    public void setSolicitudId(Long solicitudId) { this.solicitudId = solicitudId; }
    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }
    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public LocalDateTime getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(LocalDateTime fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
