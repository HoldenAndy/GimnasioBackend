package com.saas.sistema.gimnasio.modulos.pagos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record PagoRequestDto(
        @NotNull(message = "El ID de la suscripción es obligatorio")
        UUID idSuscripcion,

        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "0.1", message = "El monto debe ser mayor a cero")
        BigDecimal monto,

        @NotNull(message = "El método de pago es obligatorio")
        MetodoPago metodoPago,

        String numeroReferencia,
        String notas
) {}