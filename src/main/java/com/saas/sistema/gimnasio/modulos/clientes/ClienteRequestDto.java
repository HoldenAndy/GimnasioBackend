package com.saas.sistema.gimnasio.modulos.clientes;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClienteRequestDto(
        @NotBlank(message = "Los nombres son obligatorios")
        String nombres,

        @NotBlank(message = "Los apellidos son obligatorios")
        String apellidos,

        @NotBlank(message = "El tipo de documento es obligatorio")
        String tipoDocumento,

        @NotBlank(message = "El número de documento es obligatorio")
        @Size(min = 8, max = 20, message = "El documento debe tener entre 8 y 20 caracteres")
        String numeroDocumento,

        @Email(message = "El formato del correo no es válido")
        String correo,

        @Pattern(regexp = "^\\+?[0-9\\s]{7,20}$", message = "El teléfono solo puede contener números, espacios y un '+' al inicio")
        String telefono,

        @Pattern(regexp = "^\\+?[0-9\\s]{7,20}$", message = "El teléfono solo puede contener números, espacios y un '+' al inicio")
        String contactoEmergencia,
        String notasMedicas
) {}