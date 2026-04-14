package com.example.ms_lista_espera.repository;

import com.example.ms_lista_espera.model.SolicitudAtencion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudRepository extends JpaRepository<SolicitudAtencion, Long> {

    @Query("SELECT s FROM SolicitudAtencion s WHERE s.estado = 'PENDIENTE' ORDER BY " +
            "CASE s.gravedad " +
            "  WHEN 'ALTA' THEN 1 " +
            "  WHEN 'MEDIA' THEN 2 " +
            "  WHEN 'BAJA' THEN 3 " +
            "  ELSE 4 END ASC, " +
            "s.fechaSolicitud ASC")
    List<SolicitudAtencion> obtenerListaPriorizada();

    // NUEVO: Para el GET /estado/{estado}
    List<SolicitudAtencion> findByEstado(String estado);
}