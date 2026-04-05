package com.saas.sistema.gimnasio.modulos.suscripciones;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SuscripcionRepository extends JpaRepository<SuscripcionEntidad, UUID> {

    @Modifying
    @Query("UPDATE SuscripcionEntidad s SET s.estadoSuscripcion = :nuevoEstado WHERE s.estadoSuscripcion = :estadoViejo AND s.fechaFin < :hoy")
    int actualizarEstadosVencidos(@Param("estadoViejo") EstadoSuscripcion estadoViejo, @Param("nuevoEstado") EstadoSuscripcion nuevoEstado, @Param("hoy") LocalDate hoy);

    long countByTenantIdAndEstadoSuscripcion(UUID tenantId, com.saas.sistema.gimnasio.modulos.suscripciones.EstadoSuscripcion estado);

    @Query("SELECT COUNT(s) FROM SuscripcionEntidad s WHERE s.tenantId = :tenantId AND s.estadoSuscripcion = :estado AND s.fechaFin BETWEEN :hoy AND :limite")
    long contarSuscripcionesPorVencer(@Param("tenantId") UUID tenantId, @Param("estado") com.saas.sistema.gimnasio.modulos.suscripciones.EstadoSuscripcion estado, @Param("hoy") java.time.LocalDate hoy, @Param("limite") java.time.LocalDate limite);

    List<SuscripcionEntidad> findByTenantId(UUID tenantId);

    List<SuscripcionEntidad> findByClienteEntidadIdAndTenantIdOrderByFechaCreacionDesc(UUID clienteId, UUID tenantId);

    Optional<SuscripcionEntidad> findByIdAndTenantId(UUID id, UUID tenantId);

    boolean existsByClienteEntidadIdAndTenantIdAndEstadoSuscripcion(UUID clienteId, UUID tenantId, EstadoSuscripcion estado);

    Page<SuscripcionEntidad> findByTenantId(UUID tenantId, Pageable pageable);

}