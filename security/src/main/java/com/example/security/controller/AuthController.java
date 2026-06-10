package com.example.security.controller;

import com.example.security.dto.*;
import com.example.security.entity.Usuario;
import com.example.security.repository.UsuarioRepository;
import com.example.security.service.JwtService;
import org.springframework.http.HttpStatus;
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

        String rolFinal = (request.getRol() != null) ? request.getRol() : "ROLE_PACIENTE";
        user.setRol(rolFinal);

        usuarioRepository.save(user);
        return ResponseEntity.ok(Map.of("mensaje", "Usuario registrado como " + rolFinal));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequestDto request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        // 🟢 SOLUCIÓN: Extraer el rol real del usuario desde sus authorities
        String rol = userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .findFirst()
                .orElse("ROLE_PACIENTE");

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("rol", rol);

        // Generamos el token inyectando el claim de seguridad estructurado
        String jwtToken = jwtService.generateToken(extraClaims, userDetails);
        return ResponseEntity.ok(Map.of("token", jwtToken));
    }

    // 🔴 NUEVO ENDPOINT SENIOR: Valida de forma centralizada cualquier token del ecosistema
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestParam("token") String token) {
        if (jwtService.isTokenValid(token)) {
            String username = jwtService.extractUsername(token);
            String rol = jwtService.extractClaim(token, claims -> claims.get("rol", String.class));

            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("username", username);
            response.put("rol", rol);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("valid", false, "error", "Token inválido o expirado"));
    }
}