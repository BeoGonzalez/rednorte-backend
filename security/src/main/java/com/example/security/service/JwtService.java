package com.example.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio utilitario para la gestión integral de Tokens Web JSON (JWT).
 * <p>
 * Este servicio centraliza las operaciones de:
 * <ul>
 * <li>Generación de tokens firmados mediante algoritmo HS256.</li>
 * <li>Extracción de claims (reclamaciones) y sujetos (subjet).</li>
 * <li>Validación de firmas y verificación de vigencia (expiración).</li>
 * </ul>
 * <b>Nota arquitectónica:</b> La propiedad {@code jwt.secret.key} debe ser consistente en
 * todos los microservicios que necesiten validar tokens.
 * </p>
 */
@Service
public class JwtService {

    @Value("${jwt.secret.key}")
    private String secretKey;

    /**
     * Extrae el nombre de usuario (subject) contenido en el token.
     *
     * @param token El token JWT.
     * @return El nombre de usuario (subject).
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae una reclamación (claim) específica del token utilizando un resolvedor funcional.
     *
     * @param token          El token JWT.
     * @param claimsResolver Función para extraer el valor deseado de los claims.
     * @param <T>            Tipo de dato de retorno.
     * @return El valor extraído de los claims.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Genera un token para un usuario sin claims adicionales.
     *
     * @param userDetails Detalles del usuario para configurar el subject.
     * @return El token JWT generado.
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Genera un token JWT firmado con claims adicionales personalizados.
     * <p>
     * El token se configura con una expiración de 24 horas y firma HS256.
     * </p>
     *
     * @param extraClaims Mapa de claims adicionales (ej: roles).
     * @param userDetails Detalles del usuario.
     * @return El token JWT firmado.
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 horas
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Parsea el token para obtener todos los claims.
     *
     * @param token El token JWT.
     * @return Objeto {@link Claims} con la carga útil del token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Obtiene la clave de firma decodificando la secretKey desde Base64.
     *
     * @return Objeto {@link Key} utilizado para la firma HMAC.
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Valida si un token es auténtico y no ha expirado.
     *
     * @param token El token JWT a validar.
     * @return true si el token es válido, false en caso contrario (incluso si la firma es incorrecta).
     */
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica si el token ha superado la fecha de expiración.
     *
     * @param token El token JWT.
     * @return true si el token ha expirado.
     */
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new java.util.Date());
    }
}