package com.saas.sistema.gimnasio.modulos.ingresos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IngresoRepository extends JpaRepository<IngresoEntidad, UUID> {
    List<IngresoEntidad> findByTenantIdOrderByFechaHoraIngresoDesc(UUID tenantId);

    Optional<IngresoEntidad> findFirstByClienteEntidadIdAndTenantIdOrderByFechaHoraIngresoDesc(UUID clienteId, UUID tenantId);

    Page<IngresoEntidad> findByTenantId(UUID tenantId, Pageable pageable);
}