package com.saas.sistema.gimnasio.modulos.suscripciones;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/suscripciones")
public class SuscripcionController {

    private final SuscripcionService suscripcionService;

    public SuscripcionController(SuscripcionService suscripcionService) {
        this.suscripcionService = suscripcionService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<SuscripcionResponseDto> crear(@Valid @RequestBody SuscripcionRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(suscripcionService.crearSuscripcion(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<Page<SuscripcionResponseDto>> listarSuscripciones(@PageableDefault(page = 0, size = 10, sort = "fechaInicio", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(suscripcionService.obtenerTodasLasSuscripciones(pageable));
    }

    @GetMapping("/cliente/{idCliente}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<List<SuscripcionResponseDto>> listarPorCliente(@PathVariable UUID idCliente) {
        return ResponseEntity.ok(suscripcionService.obtenerSuscripcionPorCliente(idCliente));
    }

    @PutMapping("/cancelarSuscripcion/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<SuscripcionResponseDto> cancelar(@PathVariable UUID id) {
        return ResponseEntity.ok(suscripcionService.cancelarSuscripcion(id));
    }
}