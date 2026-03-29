package com.saas.sistema.gimnasio.modulos.reportes;

import java.math.BigDecimal;

public record DashboardResponseDto(
        BigDecimal recaudacionMensual,
        long clientesActivos,
        long suscripcionesPorVencer,
        long asistenciasHoy
) {}