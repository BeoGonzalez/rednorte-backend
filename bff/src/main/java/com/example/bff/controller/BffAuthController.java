package com.example.bff.controller;

import com.example.bff.client.AuthClient;
import feign.FeignException; // 🔴 IMPORTANTE
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bff/auth")
public class BffAuthController {
    @Autowired
    private AuthClient authClient;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Object payload) {
        try {
            // Intenta el registro normal
            return authClient.register(payload);
        } catch (FeignException e) {
            // Si Security dice "Usuario ya existe" (400), se lo pasamos a Angular tal cual
            return ResponseEntity.status(e.status()).body(e.contentUTF8());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Object payload) {
        try {
            return authClient.login(payload);
        } catch (feign.FeignException e) {
            return ResponseEntity.status(e.status()).body(e.contentUTF8());
        }
    }
}