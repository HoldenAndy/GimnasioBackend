package com.saas.sistema.gimnasio.modulos.usuarios;

import com.saas.sistema.gimnasio.modulos.gimnasios.EntidadBaseTenant;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
@Data
@EqualsAndHashCode(callSuper = true) // Para incluir el tenantId en equals/hashCode
public class UsuarioEntidad extends EntidadBaseTenant implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(name = "contrasena_hash", nullable = false)
    private String contrasenaHash;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Enumerated(EnumType.STRING)
    private RolUsuario rol;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "estado_activo", nullable = false)
    private boolean estadoActivo = true;

    // --- Métodos obligatorios de UserDetails (Spring Security) ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Spring Security espera que los roles empiecen con "ROLE_"
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.rol.name()));
    }

    @Override
    public String getPassword() {
        return this.contrasenaHash;
    }

    @Override
    public String getUsername() {
        return this.correo;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.estadoActivo;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.estadoActivo;
    }
}