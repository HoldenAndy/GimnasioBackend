package com.saas.sistema.gimnasio.modulos.suscripciones;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface SuscripcionService {
    SuscripcionResponseDto crearSuscripcion(SuscripcionRequestDto dto);

    Page<SuscripcionResponseDto> obtenerTodasLasSuscripciones(Pageable pageable);

    List<SuscripcionResponseDto> obtenerSuscripcionPorCliente(UUID idCliente);

    SuscripcionResponseDto cancelarSuscripcion(UUID id);

}