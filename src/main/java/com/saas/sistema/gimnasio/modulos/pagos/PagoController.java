package com.saas.sistema.gimnasio.modulos.pagos;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<PagoResponseDto> registrarPago(@Valid @RequestBody PagoRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoService.registrarPago(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<Page<PagoResponseDto>> listarPagos(
        @RequestParam(required = false) String query,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
        Pageable pageable) {
    return ResponseEntity.ok(pagoService.obtenerHistorialPagos(query, inicio, fin, pageable));
}

    @GetMapping("/suscripcion/{idSuscripcion}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<List<PagoResponseDto>> obtenerPagosPorSuscripcion(@PathVariable UUID idSuscripcion) {
        return ResponseEntity.ok(pagoService.obtenerPagosPorSuscripcion(idSuscripcion));
    }

    @GetMapping("/cliente/{idCliente}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<List<PagoResponseDto>> listarPagosPorCliente(@PathVariable UUID idCliente) {
        return ResponseEntity.ok(pagoService.obtenerPagoPorCliente(idCliente));
    }

    @PutMapping("/anular/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<PagoResponseDto> anularPago(@PathVariable UUID id) {
        return ResponseEntity.ok(pagoService.anularPago(id));
    }
}