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

/**
 * Controlador REST encargado de la gestión de identidades, autenticación y autorización.
 * <p>
 * Este controlador actúa como el punto de entrada para el microservicio de seguridad ({@code ms-security}).
 * Proporciona endpoints para el registro de usuarios, emisión de tokens JWT y validación
 * de credenciales. Sigue el principio de responsabilidad única al centrarse exclusivamente
 * en la autenticación, delegando la orquestación de perfiles complejos al BFF.
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Inyección de dependencias necesaria para la gestión de seguridad y persistencia.
     */
    public AuthController(AuthenticationManager am, UserDetailsService uds, JwtService js, UsuarioRepository ur, PasswordEncoder pe) {
        this.authenticationManager = am;
        this.userDetailsService = uds;
        this.jwtService = js;
        this.usuarioRepository = ur;
        this.passwordEncoder = pe;
    }

    /**
     * Registra un nuevo usuario en el sistema de seguridad.
     * <p>
     * Este método implementa un registro simplificado:
     * <ol>
     * <li>Valida si el nombre de usuario ya existe.</li>
     * <li>Codifica la contraseña.</li>
     * <li>Asigna un rol por defecto (ROLE_PACIENTE) si no se especifica.</li>
     * <li>Persiste el usuario y retorna su {@code authId}.</li>
     * </ol>
     * <b>Nota:</b> La creación de perfiles asociados (médico o paciente) se realiza
     * posteriormente a través del BFF.
     * </p>
     *
     * @param request Datos de registro (username, password, rol).
     * @return {@link ResponseEntity} con el ID de autenticación generado y un mensaje de éxito.
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequestDto request) {
        if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Usuario ya existe"));
        }
        Usuario user = new Usuario();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        String rolFinal = (request.getRol() != null) ? request.getRol() : "ROLE_PACIENTE";
        user.setRol(rolFinal);

        user = usuarioRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Usuario registrado como " + rolFinal);
        response.put("authId", user.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Autentica a un usuario y genera un token JWT.
     * <p>
     * Valida las credenciales contra {@link AuthenticationManager} y, en caso exitoso,
     * extrae los roles del usuario para incluirlos como <i>claims</i> dentro del token JWT.
     * </p>
     *
     * @param request Credenciales de acceso (username, password).
     * @return {@link ResponseEntity} con el token JWT generado.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequestDto request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        String rol = userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .findFirst()
                .orElse("ROLE_PACIENTE");

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("rol", rol);

        String jwtToken = jwtService.generateToken(extraClaims, userDetails);
        return ResponseEntity.ok(Map.of("token", jwtToken));
    }

    /**
     * Valida la integridad y vigencia de un token JWT.
     * <p>
     * Extrae el nombre de usuario y los roles (claims) del token si este resulta ser válido.
     * </p>
     *
     * @param token El token JWT a validar.
     * @return {@link ResponseEntity} con el estado de validación y la información extraída del token.
     */
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