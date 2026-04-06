package com.saas.sistema.gimnasio.modulos.planes;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/planes")
public class PlanMembresiaController {

    private final PlanMembresiaService planMembresiaService;

    public PlanMembresiaController(PlanMembresiaService planService) {
        this.planMembresiaService = planService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<PlanMembresiaResponseDto> crear(@Valid @RequestBody PlanMembresiaRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(planMembresiaService.crearPlanMembresia(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<Page<PlanMembresiaResponseDto>> listarPlanes(
            @RequestParam(required = false, defaultValue = "false") boolean incluirInactivos,
            @PageableDefault(page = 0, size = 10, sort = "precio") Pageable pageable) {
        return ResponseEntity.ok(planMembresiaService.obtenerTodosLosPlanes(incluirInactivos, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<PlanMembresiaResponseDto> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(planMembresiaService.obtenerPlanMembresiaPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<PlanMembresiaResponseDto> actualizar(@PathVariable UUID id, @Valid @RequestBody PlanMembresiaRequestDto request) {
        return ResponseEntity.ok(planMembresiaService.actualizarPlanMembresia(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        planMembresiaService.eliminarPlanMembresia(id);
        return ResponseEntity.noContent().build();
    }
}