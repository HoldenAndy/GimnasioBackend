package com.saas.sistema.gimnasio.modulos.suscripciones;

import com.saas.sistema.gimnasio.modulos.clientes.ClienteEntidad;
import com.saas.sistema.gimnasio.nucleo.configuracion.ContextoTenant;
import com.saas.sistema.gimnasio.modulos.planes.PlanMembresiaEntidad;
import com.saas.sistema.gimnasio.modulos.clientes.ClienteRepository;
import com.saas.sistema.gimnasio.modulos.planes.PlanMembresiaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SuscripcionServiceImpl implements SuscripcionService {

    private final SuscripcionRepository suscripcionRepository;
    private final ClienteRepository clienteRepository;
    private final PlanMembresiaRepository planRepository;

    public SuscripcionServiceImpl(
            SuscripcionRepository suscripcionRepository,
            ClienteRepository clienteRepository,
            PlanMembresiaRepository planRepository) {
        this.suscripcionRepository = suscripcionRepository;
        this.clienteRepository = clienteRepository;
        this.planRepository = planRepository;
    }

    private SuscripcionResponseDto mapearADto(SuscripcionEntidad s) {
        return new SuscripcionResponseDto(
                s.getId(),
                s.getClienteEntidad().getNombre() + " " + s.getClienteEntidad().getApellido(),
                s.getClienteEntidad().getNumeroDocumento(),
                s.getPlanMembresiaEntidad().getNombre(),
                s.getFechaInicio(),
                s.getFechaFin(),
                s.getEstadoSuscripcion()
        );
    }

    @Transactional
    @Override
    public SuscripcionResponseDto crearSuscripcion(SuscripcionRequestDto dto) {
        UUID tenantActual = ContextoTenant.getTenantId();

        boolean tieneActiva = suscripcionRepository.existsByClienteEntidadIdAndTenantIdAndEstadoSuscripcion(
                dto.idCliente(), tenantActual, EstadoSuscripcion.ACTIVA.name());

        if (tieneActiva) {
            throw new IllegalArgumentException("Este clienteEntidad ya tiene una suscripción activa. Cancele la actual o espere a que venza para renovar.");
        }

        // 1. Validar que el ClienteEntidad exista y esté ACTIVO
        ClienteEntidad clienteEntidad = clienteRepository.findByIdAndTenantId(dto.idCliente(), tenantActual)
                .orElseThrow(() -> new EntityNotFoundException("ClienteEntidad no encontrado"));
        if (!clienteEntidad.isEstadoActivo()) {
            throw new IllegalArgumentException("No se puede crearCliente una suscripción para un clienteEntidad inactivo.");
        }

        // 2. Validar que el Plan exista y esté ACTIVO
        PlanMembresiaEntidad plan = planRepository.findByIdAndTenantId(dto.idPlan(), tenantActual)
                .orElseThrow(() -> new EntityNotFoundException("Plan no encontrado"));
        if (!plan.isEstadoActivo()) {
            throw new IllegalArgumentException("El plan seleccionado ya no está disponible para la venta.");
        }

        // 3. Crear la suscripción y calcular fechas automáticamente
        SuscripcionEntidad suscripcionEntidad = new SuscripcionEntidad();
        suscripcionEntidad.setTenantId(tenantActual);
        suscripcionEntidad.setClienteEntidad(clienteEntidad);
        suscripcionEntidad.setPlanMembresiaEntidad(plan);
        suscripcionEntidad.setEstadoSuscripcion(EstadoSuscripcion.ACTIVA);
        suscripcionEntidad.setFechaCreacion(LocalDateTime.now());

        LocalDate hoy = LocalDate.now();
        suscripcionEntidad.setFechaInicio(hoy);
        suscripcionEntidad.setFechaFin(hoy.plusDays(plan.getDuracionDias()));

        return mapearADto(suscripcionRepository.save(suscripcionEntidad));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<SuscripcionResponseDto> obtenerTodasLasSuscripciones(Pageable pageable) {
        UUID tenantActual = ContextoTenant.getTenantId();
        return suscripcionRepository.findByTenantId(tenantActual, pageable)
                .map(this::mapearADto);
    }

    @Transactional(readOnly = true)
    @Override
    public List<SuscripcionResponseDto> obtenerSuscripcionPorCliente(UUID idCliente) {
        return suscripcionRepository.findByClienteEntidadIdAndTenantIdOrderByFechaCreacionDesc(idCliente, ContextoTenant.getTenantId())
                .stream()
                .map(this::mapearADto)
                .toList();
    }

    @Transactional
    @Override
    public SuscripcionResponseDto cancelarSuscripcion(UUID id) {
        UUID tenantActual = ContextoTenant.getTenantId();
        SuscripcionEntidad suscripcionEntidad = suscripcionRepository.findByIdAndTenantId(id, tenantActual)
                .orElseThrow(() -> new EntityNotFoundException("Suscripción no encontrada"));

        if (suscripcionEntidad.getEstadoSuscripcion().equals(EstadoSuscripcion.CANCELADA)) {
            throw new IllegalArgumentException("La suscripción ya se encuentra cancelada.");
        }

        suscripcionEntidad.setEstadoSuscripcion(EstadoSuscripcion.CANCELADA);
        return mapearADto(suscripcionRepository.save(suscripcionEntidad));
    }
}