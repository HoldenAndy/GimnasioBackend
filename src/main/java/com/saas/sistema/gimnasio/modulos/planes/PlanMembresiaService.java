package com.saas.sistema.gimnasio.modulos.planes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PlanMembresiaService {
    PlanMembresiaResponseDto crearPlanMembresia(PlanMembresiaRequestDto dto);

    Page<PlanMembresiaResponseDto> obtenerTodosLosPlanesMembresia(Pageable pageable);

    List<PlanMembresiaResponseDto> obtenerPlanesParaVenta();

    PlanMembresiaResponseDto obtenerPlanMembresiaPorId(UUID id);

    PlanMembresiaResponseDto actualizarPlanMembresia(UUID id, PlanMembresiaRequestDto dto);

    void eliminarPlanMembresia(UUID id);
}