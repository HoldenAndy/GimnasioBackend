package com.saas.sistema.gimnasio.modulos.suscripciones;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SuscripcionRepository extends JpaRepository<SuscripcionEntidad, UUID> {

    List<SuscripcionEntidad> findByTenantId(UUID tenantId);

    List<SuscripcionEntidad> findByClienteEntidadIdAndTenantIdOrderByFechaCreacionDesc(UUID clienteId, UUID tenantId);

    Optional<SuscripcionEntidad> findByIdAndTenantId(UUID id, UUID tenantId);

    boolean existsByClienteEntidadIdAndTenantIdAndEstadoSuscripcion(UUID clienteId, UUID tenantId, String estado);

    Page<SuscripcionEntidad> findByTenantId(UUID tenantId, Pageable pageable);
}