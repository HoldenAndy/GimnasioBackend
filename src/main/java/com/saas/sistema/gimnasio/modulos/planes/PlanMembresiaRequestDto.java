package com.saas.sistema.gimnasio.modulos.planes;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PlanMembresiaRequestDto(
        @NotBlank(message = "El nombre del plan es obligatorio")
        String nombre,

        String descripcion,

        @NotNull(message = "La duración en días es obligatoria")
        @Min(value = 1, message = "El plan debe durar al menos 1 día")
        Integer duracionDias,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo")
        BigDecimal precio
) {}