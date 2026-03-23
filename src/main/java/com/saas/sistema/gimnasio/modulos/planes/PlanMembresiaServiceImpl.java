package com.saas.sistema.gimnasio.modulos.planes;

import com.saas.sistema.gimnasio.nucleo.configuracion.ContextoTenant;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PlanMembresiaServiceImpl implements PlanMembresiaService {

    private final PlanMembresiaRepository planMembresiaRepository;

    public PlanMembresiaServiceImpl(PlanMembresiaRepository planMembresiaRepository) {
        this.planMembresiaRepository = planMembresiaRepository;
    }

    private PlanMembresiaResponseDto mapearADto(PlanMembresiaEntidad p) {
        return new PlanMembresiaResponseDto(
                p.getId(),
                p.getNombre(),
                p.getDescripcion(),
                p.getDuracionDias(),
                p.getPrecio()
        );
    }

    private PlanMembresiaEntidad obtenerEntidadPorId(UUID id) {
        return planMembresiaRepository.findByIdAndTenantId(id, ContextoTenant.getTenantId())
                .orElseThrow(() -> new EntityNotFoundException("Plan de membresía no encontrado en este gimnasio"));
    }

    @Transactional
    @Override
    public PlanMembresiaResponseDto crearPlanMembresia(PlanMembresiaRequestDto dto) {
        UUID tenantActual = ContextoTenant.getTenantId();
        if (planMembresiaRepository.existsByNombreIgnoreCaseAndTenantIdAndEstadoActivoTrue(dto.nombre().trim(), tenantActual)) {
            throw new IllegalArgumentException("Ya existe un plan activo con el nombre: " + dto.nombre());
        }
        PlanMembresiaEntidad plan = new PlanMembresiaEntidad();
        plan.setNombre(dto.nombre());
        plan.setDescripcion(dto.descripcion());
        plan.setDuracionDias(dto.duracionDias());
        plan.setPrecio(dto.precio());
        plan.setEstadoActivo(true);
        plan.setTenantId(tenantActual);

        return mapearADto(planMembresiaRepository.save(plan));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<PlanMembresiaResponseDto> obtenerTodosLosPlanesMembresia(Pageable pageable) {
        UUID tenantActual = ContextoTenant.getTenantId();
        return planMembresiaRepository.findByTenantIdAndEstadoActivoTrue(tenantActual, pageable)
                .map(this::mapearADto);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PlanMembresiaResponseDto> obtenerPlanesParaVenta() {
        UUID tenantActual = ContextoTenant.getTenantId();
        return planMembresiaRepository.findByTenantIdAndEstadoActivoTrue(tenantActual)
                .stream()
                .map(this::mapearADto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public PlanMembresiaResponseDto obtenerPlanMembresiaPorId(UUID id) {
        return mapearADto(obtenerEntidadPorId(id));
    }

    @Transactional
    @Override
    public PlanMembresiaResponseDto actualizarPlanMembresia(UUID id, PlanMembresiaRequestDto dto) {
        PlanMembresiaEntidad plan = obtenerEntidadPorId(id);
        if (!plan.isEstadoActivo()) {
            throw new IllegalArgumentException("No se puede modificar un plan de membresía que ha sido eliminado/archivado.");
        }
        String nuevoNombre = dto.nombre().trim();
        if (!plan.getNombre().equalsIgnoreCase(nuevoNombre)) {
            if (planMembresiaRepository.existsByNombreIgnoreCaseAndTenantIdAndEstadoActivoTrue(nuevoNombre, ContextoTenant.getTenantId())) {
                throw new IllegalArgumentException("Ya existe otro plan activo con el nombre: " + nuevoNombre);
            }
        }
        plan.setNombre(dto.nombre());
        plan.setDescripcion(dto.descripcion());
        plan.setDuracionDias(dto.duracionDias());
        plan.setPrecio(dto.precio());

        return mapearADto(planMembresiaRepository.save(plan));
    }

    @Transactional
    @Override
    public void eliminarPlanMembresia(UUID id) {
        PlanMembresiaEntidad plan = obtenerEntidadPorId(id);
        if (!plan.isEstadoActivo()) {
            throw new IllegalArgumentException("El plan de membresía ya se encuentra inactivo/eliminado.");
        }
        plan.setEstadoActivo(false);
        planMembresiaRepository.save(plan);
    }
}