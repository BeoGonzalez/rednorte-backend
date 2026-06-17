package com.example.ms_lista_espera.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes")
public class SolicitudAtencion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "paciente_id", nullable = false)
    private Long pacienteId;

    // Set when a doctor manually accepts or rejects the solicitud
    @Column(name = "doctor_id")
    private Long doctorId;

    @Column(nullable = false, length = 50)
    private String tipoSolicitud;

    @Column(nullable = false, length = 20)
    private String gravedad;

    // BUSCANDO_CITA = waiting for a doctor to manually assign (this flow)
    // PENDIENTE     = waiting for the auto-assign job (AsignadorCitasJob)
    @Column(nullable = false, length = 20)
    private String estado = "BUSCANDO_CITA";

    @Column(name = "fecha_solicitud", updatable = false)
    private LocalDateTime fechaSolicitud;

    @PrePersist
    protected void onCreate() {
        this.fechaSolicitud = LocalDateTime.now();
    }

    public SolicitudAtencion() {}

    public SolicitudAtencion(Long pacienteId, String tipoSolicitud, String gravedad) {
        this.pacienteId = pacienteId;
        this.tipoSolicitud = tipoSolicitud;
        this.gravedad = gravedad;
        this.estado = "BUSCANDO_CITA";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public String getTipoSolicitud() { return tipoSolicitud; }
    public void setTipoSolicitud(String tipoSolicitud) { this.tipoSolicitud = tipoSolicitud; }

    public String getGravedad() { return gravedad; }
    public void setGravedad(String gravedad) { this.gravedad = gravedad; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
}
