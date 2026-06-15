package com.example.security.service;

import com.example.security.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Servicio especializado en la carga de detalles del usuario para Spring Security.
 * <p>
 * Implementa la interfaz {@link UserDetailsService} para servir como puente entre la base
 * de datos de usuarios ({@link UsuarioRepository}) y el mecanismo de autenticación de
 * Spring Security. Su responsabilidad es convertir la entidad interna de usuario
 * a un objeto {@link UserDetails} compatible con el flujo de seguridad.
 * </p>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Constructor para la inyección de dependencias del repositorio de usuarios.
     *
     * @param usuarioRepository Repositorio encargado de la persistencia de usuarios.
     */
    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Localiza al usuario basado en su nombre de usuario (username).
     * <p>
     * Este método es invocado por el {@code AuthenticationManager} durante el proceso
     * de inicio de sesión. Si el usuario existe, se mapea su información (username,
     * password y rol) a un objeto {@link User} de Spring Security.
     * </p>
     *
     * @param username El nombre de usuario único por el cual se desea autenticar.
     * @return Un objeto {@link UserDetails} que contiene la información del usuario autenticado.
     * @throws UsernameNotFoundException Si no se encuentra un usuario con el nombre proporcionado.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByUsername(username)
                .map(usuario -> new User(
                        usuario.getUsername(),
                        usuario.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority(usuario.getRol()))
                ))
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }
}