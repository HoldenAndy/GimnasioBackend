package com.saas.sistema.gimnasio.modulos.ingresos;

import java.time.LocalDateTime;

public record IngresoResponseDto(
        boolean accesoPermitido,
        String mensaje,
        String nombreCliente,
        LocalDateTime fechaHoraIngreso
) {}