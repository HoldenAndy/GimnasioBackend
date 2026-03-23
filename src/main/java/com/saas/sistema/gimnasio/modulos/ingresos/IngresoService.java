package com.saas.sistema.gimnasio.modulos.ingresos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IngresoService {
    IngresoResponseDto registrarIntentoIngresoPorId(UUID idCliente);

    IngresoResponseDto registrarIntentoIngresoPorDocumento(String numeroDocumento);

    Page<IngresoResponseDto> obtenerHistorialIngresos(Pageable pageable);
}