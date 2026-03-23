package com.saas.sistema.gimnasio.modulos.pagos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PagoResponseDto(
        UUID id,
        String nombreCliente,
        String planPagado,
        BigDecimal monto,
        MetodoPago metodoPago,
        String numeroReferencia,
        LocalDateTime fechaPago,
        String correoCajero,
        String notas,
        Boolean estadoActivo
) {}