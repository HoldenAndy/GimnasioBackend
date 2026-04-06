package com.saas.sistema.gimnasio.modulos.planes;

import java.math.BigDecimal;
import java.util.UUID;

public record PlanMembresiaResponseDto(
        UUID id,
        String nombre,
        String descripcion,
        Integer duracionDias,
        BigDecimal precio,
        boolean estadoActivo
) {}