package com.example.security.repository;

import com.example.security.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Este método es oro puro: buscará al usuario por su nombre para verificar su contraseña
    Optional<Usuario> findByUsername(String username);
}
