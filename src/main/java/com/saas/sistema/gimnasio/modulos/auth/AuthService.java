package com.saas.sistema.gimnasio.modulos.auth;

public interface AuthService {
    AuthResponseDto login(LoginDto request);
}