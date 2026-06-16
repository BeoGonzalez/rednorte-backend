package com.example.security.service;

import com.example.security.entity.Usuario;
import com.example.security.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        usuarioMock = new Usuario();
        usuarioMock.setUsername("admin");
        usuarioMock.setPassword("12345");
        usuarioMock.setRol("ROLE_MEDICO");
    }

    @Test
    void testLoadUserByUsername_UsuarioExiste() {
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuarioMock));

        UserDetails userDetails = userDetailsService.loadUserByUsername("admin");

        assertNotNull(userDetails);
        assertEquals("admin", userDetails.getUsername());
        assertEquals("12345", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MEDICO")));

        verify(usuarioRepository, times(1)).findByUsername("admin");
    }

    @Test
    void testLoadUserByUsername_UsuarioNoExiste() {
        when(usuarioRepository.findByUsername("desconocido")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("desconocido");
        });

        assertEquals("Usuario no encontrado: desconocido", exception.getMessage());
        verify(usuarioRepository, times(1)).findByUsername("desconocido");
    }
}