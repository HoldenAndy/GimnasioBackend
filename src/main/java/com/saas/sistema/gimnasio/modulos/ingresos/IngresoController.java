package com.saas.sistema.gimnasio.modulos.ingresos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ingresos")
public class IngresoController {

    private final IngresoService ingresoService;

    public IngresoController(IngresoService ingresoService) {
        this.ingresoService = ingresoService;
    }

    @PostMapping("/{idCliente}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<IngresoResponseDto> registrarIngresoPorId(@PathVariable UUID idCliente) {
        return ResponseEntity.ok(ingresoService.registrarIntentoIngresoPorId(idCliente));
    }

    @PostMapping("/cliente/documento/{numeroDocumento}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<IngresoResponseDto> registrarIngresoPorDocumentoIdentidad(@PathVariable String numeroDocumento) {
        return ResponseEntity.ok(ingresoService.registrarIntentoIngresoPorDocumento(numeroDocumento));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<Page<IngresoResponseDto>> obtenerHistorial(@PageableDefault(page = 0, size = 10, sort = "fechaHoraIngreso", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ingresoService.obtenerHistorialIngresos(pageable));
    }
}