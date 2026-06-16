package com.example.security.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;
    private final String SECRET_KEY_MOCK = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Simulamos la inyección del @Value que hace Spring Boot
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET_KEY_MOCK);

        userDetails = new User("medico_test", "pass", Collections.emptyList());
    }

    @Test
    void testGenerateAndValidateToken_Exito() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", "ROLE_MEDICO");

        String token = jwtService.generateToken(claims, userDetails);

        assertNotNull(token);
        assertTrue(jwtService.isTokenValid(token));
        assertEquals("medico_test", jwtService.extractUsername(token));
        assertEquals("ROLE_MEDICO", jwtService.extractClaim(token, c -> c.get("rol", String.class)));
    }

    @Test
    void testIsTokenValid_FallaSiEsInvalidoOMalformado() {
        String tokenAlterado = "eyJhbGciOiJIUzI1NiJ9.TokenFalso.FirmaInvalida";

        assertFalse(jwtService.isTokenValid(tokenAlterado));
    }

    @Test
    void testIsTokenValid_FallaSiEstaExpirado() {
        // Generamos un token manualmente que expiró hace 1 hora
        String tokenExpirado = Jwts.builder()
                .setSubject("medico_test")
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY_MOCK)), SignatureAlgorithm.HS256)
                .compact();

        assertFalse(jwtService.isTokenValid(tokenExpirado));
    }
}