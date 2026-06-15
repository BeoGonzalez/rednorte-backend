package com.example.bff.controller;

import com.example.bff.client.DoctorClient;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador Backend For Frontend (BFF) encargado de gestionar las consultas relacionadas con los médicos.
 * <p>
 * Este controlador actúa como un proxy seguro hacia el microservicio de doctores ({@code ms-doctores}).
 * Su objetivo es centralizar las peticiones del frontend para buscar perfiles de médicos o
 * listados por especialidad, encapsulando el manejo de errores de comunicación mediante {@link FeignException}.
 * </p>
 */
@RestController
@RequestMapping("/bff/doctores")
public class BffDoctorController {

    @Autowired
    private DoctorClient doctorClient;

    /**
     * Recupera el perfil completo de un médico utilizando su identificador de autenticación.
     * <p>
     * Realiza una llamada delegada a través de {@link DoctorClient}. Si el servicio remoto
     * no responde o devuelve un error, se captura la excepción y se propaga la respuesta
     * de error original (código de estado y cuerpo) hacia el cliente que realizó la petición.
     * </p>
     *
     * @param authId El identificador de autenticación del médico.
     * @return {@link ResponseEntity} con los datos del médico o la respuesta de error del servicio remoto.
     */
    @GetMapping("/auth/{authId}")
    public ResponseEntity<?> obtenerPorAuthId(@PathVariable Long authId) {
        try {
            return doctorClient.obtenerPorAuthId(authId);
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).body(e.contentUTF8());
        }
    }

    /**
     * Consulta la lista de médicos registrados en el sistema filtrados por una especialidad médica.
     * <p>
     * Actúa como proxy hacia el microservicio de doctores. En caso de fallos de comunicación,
     * maneja las excepciones de Feign devolviendo el estado HTTP y el mensaje de error
     * correspondiente proveniente del backend.
     * </p>
     *
     * @param especialidad El nombre de la especialidad para realizar el filtrado.
     * @return {@link ResponseEntity} con la lista de médicos encontrados o el error recibido del servicio remoto.
     */
    @GetMapping("/especialidad/{especialidad}")
    public ResponseEntity<?> obtenerPorEspecialidad(@PathVariable String especialidad) {
        try {
            return doctorClient.obtenerPorEspecialidad(especialidad);
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).body(e.contentUTF8());
        }
    }
}