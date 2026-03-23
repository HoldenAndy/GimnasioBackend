package com.saas.sistema.gimnasio.modulos.auth;

public record LoginDto(
        String correo,
        String contrasena
) {}