package com.saas.sistema.gimnasio.modulos.gimnasios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GimnasioRepository extends JpaRepository<GimnasioEntidad, UUID> {

    Optional<GimnasioEntidad> findByRuc(String ruc);
}