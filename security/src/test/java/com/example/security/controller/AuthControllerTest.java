package com.example.security.controller;

import com.example.security.dto.LoginRequestDto;
import com.example.security.dto.RegisterRequestDto;
import com.example.security.entity.Usuario;
import com.example.security.repository.UsuarioRepository;
import com.example.security.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserDetailsService userDetailsService;
    @Mock private JwtService jwtService;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    private RegisterRequestDto registerDto;
    private LoginRequestDto loginDto;

    @BeforeEach
    void setUp() {
        registerDto = new RegisterRequestDto();
        registerDto.setUsername("nuevo_user");
        registerDto.setPassword("pass123");
        registerDto.setRol("ROLE_PACIENTE");

        loginDto = new LoginRequestDto();
        loginDto.setUsername("usuario_existente");
        loginDto.setPassword("pass123");
    }

    @Test
    void testRegister_UsuarioYaExiste() {
        when(usuarioRepository.findByUsername("nuevo_user")).thenReturn(Optional.of(new Usuario()));

        ResponseEntity<Map<String, Object>> response = authController.register(registerDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Usuario ya existe", response.getBody().get("error"));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testRegister_Exito() {
        when(usuarioRepository.findByUsername("nuevo_user")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass123")).thenReturn("encodedPass");

        Usuario userGuardado = new Usuario();
        userGuardado.setId(1L);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(userGuardado);

        ResponseEntity<Map<String, Object>> response = authController.register(registerDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().get("authId"));
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testLogin_Exito() {
        UserDetails userDetails = new User("usuario_existente", "pass",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_PACIENTE")));

        Usuario usuario = new Usuario();
        usuario.setId(42L);
        usuario.setUsername("usuario_existente");
        when(usuarioRepository.findByUsername("usuario_existente")).thenReturn(Optional.of(usuario));
        when(userDetailsService.loadUserByUsername("usuario_existente")).thenReturn(userDetails);
        when(jwtService.generateToken(anyMap(), eq(userDetails))).thenReturn("mockJwtToken");

        // 🟢 CORRECCIÓN: Cambiado de ResponseEntity<Map<String, String>> a ResponseEntity<Map<String, Object>>
        ResponseEntity<Map<String, Object>> response = authController.login(loginDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        // El token sigue siendo un String, pero ahora lo extraemos de un Map de Objects
        assertEquals("mockJwtToken", response.getBody().get("token"));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testValidateToken_Valido() {
        when(jwtService.isTokenValid("tokenValido")).thenReturn(true);
        when(jwtService.extractUsername("tokenValido")).thenReturn("admin");
        when(jwtService.extractClaim(eq("tokenValido"), any())).thenReturn("ROLE_MEDICO");

        ResponseEntity<Map<String, Object>> response = authController.validateToken("tokenValido");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody().get("valid"));
        assertEquals("admin", response.getBody().get("username"));
    }

    @Test
    void testValidateToken_Invalido() {
        when(jwtService.isTokenValid("tokenInvalido")).thenReturn(false);

        ResponseEntity<Map<String, Object>> response = authController.validateToken("tokenInvalido");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(false, response.getBody().get("valid"));
    }

    @Test
    void testRegister_UsernameEnBlanco_RetornaBadRequest() {
        registerDto.setUsername("   ");

        ResponseEntity<Map<String, Object>> response = authController.register(registerDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("El campo 'username' es obligatorio.", response.getBody().get("error"));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testRegister_PasswordEnBlanco_RetornaBadRequest() {
        registerDto.setPassword(null);

        ResponseEntity<Map<String, Object>> response = authController.register(registerDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("El campo 'password' es obligatorio.", response.getBody().get("error"));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testRegister_RolNulo_AsignaPacientePorDefecto() {
        registerDto.setRol(null);
        when(usuarioRepository.findByUsername("nuevo_user")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass123")).thenReturn("encodedPass");

        Usuario guardado = new Usuario();
        guardado.setId(2L);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(guardado);

        ResponseEntity<Map<String, Object>> response = authController.register(registerDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Usuario registrado como ROLE_PACIENTE", response.getBody().get("mensaje"));
    }

    @Test
    void testRegister_ExcepcionInterna_RetornaError500() {
        when(usuarioRepository.findByUsername("nuevo_user"))
                .thenThrow(new RuntimeException("Fallo de conexión a la BD"));

        ResponseEntity<Map<String, Object>> response = authController.register(registerDto);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Fallo de conexión a la BD", response.getBody().get("error"));
    }

    @Test
    void testLogin_UsuarioNoEncontradoEnRepositorio_LanzaExcepcion() {
        UserDetails userDetails = new User("usuario_existente", "pass",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_PACIENTE")));
        when(userDetailsService.loadUserByUsername("usuario_existente")).thenReturn(userDetails);
        when(usuarioRepository.findByUsername("usuario_existente")).thenReturn(Optional.empty());

        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class,
                () -> authController.login(loginDto));
    }
}