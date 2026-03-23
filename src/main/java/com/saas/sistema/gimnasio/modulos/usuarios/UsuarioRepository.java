package com.saas.sistema.gimnasio.modulos.usuarios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntidad, UUID> {
    Optional<UsuarioEntidad> findByCorreo(String correo);
}