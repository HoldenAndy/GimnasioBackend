package com.saas.sistema.gimnasio.modulos.clientes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClienteRepository extends JpaRepository<ClienteEntidad, UUID> {

    List<ClienteEntidad> findByTenantId(UUID tenantId);

    Optional<ClienteEntidad> findByIdAndTenantId(UUID id, UUID tenantId);

    boolean existsByNumeroDocumentoAndTenantId(String numeroDocumento, UUID tenantId);

    Page<ClienteEntidad> findByTenantIdAndEstadoActivoTrue(UUID tenantId, Pageable pageable);

    boolean existsByCorreoIgnoreCaseAndTenantId(String correo, UUID tenantId);

    Optional<ClienteEntidad> findByNumeroDocumentoAndTenantId(String numeroDocumento, UUID tenantId);

}