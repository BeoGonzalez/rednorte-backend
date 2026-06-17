package com.example.security.controller;

import com.example.security.dto.*;
import com.example.security.entity.Usuario;
import com.example.security.repository.UsuarioRepository;
import com.example.security.service.JwtService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
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
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequestDto request) {
        try {
            // Validate required fields before touching the DB
            if (request.getUsername() == null || request.getUsername().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El campo 'username' es obligatorio."));
            }
            if (request.getPassword() == null || request.getPassword().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El campo 'password' es obligatorio."));
            }

            if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Usuario ya existe"));
            }

            String rolFinal = (request.getRol() != null && !request.getRol().isBlank())
                    ? request.getRol()
                    : "ROLE_PACIENTE";

            Usuario user = new Usuario();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRol(rolFinal);

            user = usuarioRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Usuario registrado como " + rolFinal);
            response.put("authId", user.getId());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Error interno al registrar el usuario."));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequestDto request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        String rol = userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .findFirst()
                .orElse("ROLE_PACIENTE");

        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("rol", rol);

        String jwtToken = jwtService.generateToken(extraClaims, userDetails);

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwtToken);
        response.put("authId", usuario.getId());
        response.put("rol", rol);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasAnyAuthority('ROLE_MEDICO', 'ROLE_PACIENTE')")
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
