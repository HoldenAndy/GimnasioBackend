package com.saas.sistema.gimnasio.modulos.clientes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClienteRepository extends JpaRepository<ClienteEntidad, UUID> {

    @Query("SELECT c FROM ClienteEntidad c WHERE c.tenantId = :tenantId AND c.estadoActivo = :estadoActivo " +
           "AND (LOWER(c.nombre) LIKE :query OR LOWER(c.apellido) LIKE :query OR c.numeroDocumento LIKE :query) " +
           "AND c.fechaRegistro >= :inicio " +
           "AND c.fechaRegistro <= :fin")
    Page<ClienteEntidad> buscarClientesConFiltros(
        @Param("tenantId") UUID tenantId, 
        @Param("query") String query, 
        @Param("inicio") LocalDateTime inicio, 
        @Param("fin") LocalDateTime fin, 
        @Param("estadoActivo") boolean estadoActivo,
        Pageable pageable
    );

    List<ClienteEntidad> findByTenantId(UUID tenantId);

    Optional<ClienteEntidad> findByIdAndTenantId(UUID id, UUID tenantId);

    boolean existsByNumeroDocumentoAndTenantId(String numeroDocumento, UUID tenantId);

    Page<ClienteEntidad> findByTenantIdAndEstadoActivoTrue(UUID tenantId, Pageable pageable);

    boolean existsByCorreoIgnoreCaseAndTenantId(String correo, UUID tenantId);

    Optional<ClienteEntidad> findByNumeroDocumentoAndTenantId(String numeroDocumento, UUID tenantId);

}