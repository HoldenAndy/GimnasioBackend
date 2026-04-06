package com.saas.sistema.gimnasio.modulos.pagos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PagoRepository extends JpaRepository<PagoEntidad, UUID> {

    @Query("SELECT p FROM PagoEntidad p WHERE p.tenantId = :tenantId " +
       "AND (LOWER(p.suscripcionEntidad.clienteEntidad.nombre) LIKE LOWER(:query) " +
       "OR LOWER(p.suscripcionEntidad.clienteEntidad.apellido) LIKE LOWER(:query) " +
       "OR p.suscripcionEntidad.clienteEntidad.numeroDocumento LIKE :query " +
       "OR p.numeroReferencia LIKE :query) " +
       "AND p.fechaPago BETWEEN :inicio AND :fin " +
       "ORDER BY p.fechaPago DESC")
    Page<PagoEntidad> buscarPagosConFiltros(
        @Param("tenantId") UUID tenantId,
        @Param("query") String query,
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin,
        Pageable pageable
    );

    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM PagoEntidad p WHERE p.tenantId = :tenantId AND p.estadoActivo = true AND p.fechaPago >= :inicioMes AND p.fechaPago <= :finMes")
    BigDecimal calcularRecaudacionDelMes(@Param("tenantId") UUID tenantId, @Param("inicioMes") java.time.LocalDateTime inicioMes, @Param("finMes") java.time.LocalDateTime finMes);

    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM PagoEntidad p WHERE p.suscripcionEntidad.id = :suscripcionId AND p.tenantId = :tenantId AND p.estadoActivo = true")
    BigDecimal obtenerTotalPagadoPorSuscripcion(@Param("suscripcionId") UUID suscripcionId, @Param("tenantId") UUID tenantId);

    List<PagoEntidad> findBySuscripcionEntidad_ClienteEntidad_IdAndTenantIdOrderByFechaPagoDesc(UUID clienteId, UUID tenantId);

    Optional<PagoEntidad> findByIdAndTenantId(UUID id, UUID tenantId);

    Page<PagoEntidad> findByTenantId(UUID tenantId, Pageable pageable);

    boolean existsByNumeroReferenciaAndTenantIdAndEstadoActivoTrue(String numeroReferencia, UUID tenantId);

    List<PagoEntidad> findBySuscripcionEntidad_IdAndTenantIdOrderByFechaPagoDesc(UUID idSuscripcion, UUID tenantId);
}