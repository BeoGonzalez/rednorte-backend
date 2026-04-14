package com.example.ms_lista_espera.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes")
public class SolicitudAtencion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Solo guardamos el ID del paciente, NO todo el objeto Paciente.
    // Esto es vital en microservicios para mantenerlos desacoplados.
    @Column(name = "paciente_id", nullable = false)
    private Long pacienteId;

    // Ejemplos: ATENCION_MEDICA, PROCEDIMIENTO, CIRUGIA
    @Column(nullable = false, length = 50)
    private String tipoSolicitud;

    // Ejemplos: ALTA, MEDIA, BAJA
    @Column(nullable = false, length = 20)
    private String gravedad;

    // Ejemplos: PENDIENTE, EN_PROCESO, ATENDIDO, CANCELADO
    @Column(nullable = false, length = 20)
    private String estado = "PENDIENTE";

    @Column(name = "fecha_solicitud", updatable = false)
    private LocalDateTime fechaSolicitud;

    // Este método se ejecuta automáticamente justo antes de guardar en la BD
    @PrePersist
    protected void onCreate() {
        this.fechaSolicitud = LocalDateTime.now();
    }

    // --- Constructor Vacío (Obligatorio para JPA) ---
    public SolicitudAtencion() {
    }

    // --- Constructor con parámetros (Opcional, pero útil) ---
    public SolicitudAtencion(Long pacienteId, String tipoSolicitud, String gravedad) {
        this.pacienteId = pacienteId;
        this.tipoSolicitud = tipoSolicitud;
        this.gravedad = gravedad;
        this.estado = "PENDIENTE";
    }

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public String getTipoSolicitud() {
        return tipoSolicitud;
    }

    public void setTipoSolicitud(String tipoSolicitud) {
        this.tipoSolicitud = tipoSolicitud;
    }

    public String getGravedad() {
        return gravedad;
    }

    public void setGravedad(String gravedad) {
        this.gravedad = gravedad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }
}
