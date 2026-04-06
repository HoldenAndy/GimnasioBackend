package com.saas.sistema.gimnasio.modulos.planes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlanMembresiaRepository extends JpaRepository<PlanMembresiaEntidad, UUID> {
    org.springframework.data.domain.Page<PlanMembresiaEntidad> findByTenantId(UUID tenantId, Pageable pageable);

    Optional<PlanMembresiaEntidad> findByIdAndTenantId(UUID id, UUID tenantId);

    boolean existsByNombreIgnoreCaseAndTenantIdAndEstadoActivoTrue(String nombre, UUID tenantId);

    Page<PlanMembresiaEntidad> findByTenantIdAndEstadoActivoTrue(UUID tenantId, Pageable pageable);

    List<PlanMembresiaEntidad> findByTenantIdAndEstadoActivoTrue(UUID tenantId);

    Page<PlanMembresiaEntidad> findByTenantIdAndEstadoActivoFalse(UUID tenantId, Pageable pageable);
}