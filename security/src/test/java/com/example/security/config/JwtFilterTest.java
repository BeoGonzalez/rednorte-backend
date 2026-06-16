package com.example.security.config;

import com.example.security.service.JwtService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock private JwtService jwtService;
    @Mock private UserDetailsService userDetailsService;
    @Mock private FilterChain filterChain;

    @InjectMocks
    private JwtFilter jwtFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternal_RutaPublicaIgnorada() throws Exception {
        request.setServletPath("/api/auth/login");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(jwtService);
    }

    @Test
    void testDoFilterInternal_SinHeaderAuthorization() throws Exception {
        request.setServletPath("/api/recurso-protegido");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_TokenValidoAsignaContexto() throws Exception {
        request.setServletPath("/api/recurso-protegido");
        request.addHeader("Authorization", "Bearer token-valido");

        UserDetails userDetails = new User("admin", "pass", Collections.emptyList());

        when(jwtService.extractUsername("token-valido")).thenReturn("admin");
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtService.isTokenValid("token-valido")).thenReturn(true);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("admin", SecurityContextHolder.getContext().getAuthentication().getName());
    }
}