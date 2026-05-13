package com.example.security.controller;

import com.example.security.dto.*;
import com.example.security.entity.Usuario;
import com.example.security.repository.UsuarioRepository;
import com.example.security.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager am, UserDetailsService uds, JwtService js, UsuarioRepository ur, PasswordEncoder pe) {
        this.authenticationManager = am;
        this.userDetailsService = uds;
        this.jwtService = js;
        this.usuarioRepository = ur;
        this.passwordEncoder = pe;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequestDto request) {
        if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Usuario ya existe"));
        }
        Usuario user = new Usuario();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // 🔴 Se asigna el rol enviado desde Angular
        String rolFinal = (request.getRol() != null) ? request.getRol() : "ROLE_PACIENTE";
        user.setRol(rolFinal);

        usuarioRepository.save(user);
        return ResponseEntity.ok(Map.of("mensaje", "Usuario registrado como " + rolFinal));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequestDto request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String jwtToken = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(Map.of("token", jwtToken));
    }
}