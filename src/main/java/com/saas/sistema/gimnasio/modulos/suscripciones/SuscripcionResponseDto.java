package com.saas.sistema.gimnasio.modulos.suscripciones;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record SuscripcionResponseDto(
        UUID id,
        String nombreCliente,
        String documentoCliente,
        String nombrePlan,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        EstadoSuscripcion estadoSuscripcion,
        boolean planActivo,
        BigDecimal precioTotal,
        BigDecimal saldoPendiente
) {}