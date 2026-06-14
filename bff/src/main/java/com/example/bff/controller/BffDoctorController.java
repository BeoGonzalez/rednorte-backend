package com.example.bff.controller;

import com.example.bff.client.DoctorClient;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bff/doctores")
public class BffDoctorController {

    @Autowired
    private DoctorClient doctorClient;

    @GetMapping("/auth/{authId}")
    public ResponseEntity<?> obtenerPorAuthId(@PathVariable Long authId) {
        try {
            return doctorClient.obtenerPorAuthId(authId);
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).body(e.contentUTF8());
        }
    }

    @GetMapping("/especialidad/{especialidad}")
    public ResponseEntity<?> obtenerPorEspecialidad(@PathVariable String especialidad) {
        try {
            return doctorClient.obtenerPorEspecialidad(especialidad);
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).body(e.contentUTF8());
        }
    }
}
