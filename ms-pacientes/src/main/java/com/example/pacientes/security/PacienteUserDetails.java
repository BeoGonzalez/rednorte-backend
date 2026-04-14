package com.example.pacientes.security;

import com.example.pacientes.model.Paciente;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class PacienteUserDetails implements UserDetails {
    //Paciente en su estado final para que llegue la info del paciente sn cambios
    private final Paciente paciente;

    public PacienteUserDetails(Paciente paciente){
        this.paciente = paciente;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(paciente.getRol()));
    }

    @Override
    public @Nullable String getPassword() {
        return paciente.getPassword();
    }

    @Override
    public String getUsername() {
        return paciente.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Paciente getPaciente(){ return this.paciente;}
}
