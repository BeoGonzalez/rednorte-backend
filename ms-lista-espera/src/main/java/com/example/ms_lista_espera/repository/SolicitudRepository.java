package com.example.ms_lista_espera.repository;

import com.example.ms_lista_espera.model.SolicitudAtencion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SolicitudRepository extends JpaRepository<SolicitudAtencion, Long> {
    @Query("SELECT s FROM SolicitudAtencion s WHERE s.estado = 'PENDIENTE' OR s.estado = 'BUSCANDO_CITA' ORDER BY CASE WHEN s.gravedad = 'ALTA' THEN 1 WHEN s.gravedad = 'MEDIA' THEN 2 ELSE 3 END, s.fechaSolicitud ASC")
    List<SolicitudAtencion> obtenerListaPriorizada();

    List<SolicitudAtencion> findByEstado(String estado);

    // 🔴 Para el panel del médico
    List<SolicitudAtencion> findByEstadoAndTipoSolicitud(String estado, String tipoSolicitud);
}