package com.saas.sistema.gimnasio.modulos.suscripciones;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record SuscripcionRequestDto(
        @NotNull(message = "El ID del cliente es obligatorio")
        UUID idCliente,

        @NotNull(message = "El ID del plan de membresía es obligatorio")
        UUID idPlan
) {}