package com.saas.sistema.gimnasio.modulos.clientes;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClienteResponseDto(
        UUID id,
        String nombres,
        String apellidos,
        String tipoDocumento,
        String numeroDocumento,
        String correo,
        String telefono,
        String contactoEmergencia,
        String notasMedicas,
        LocalDateTime fechaRegistro
) {}