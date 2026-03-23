package com.saas.sistema.gimnasio.modulos.auth;

import com.saas.sistema.gimnasio.modulos.usuarios.UsuarioEntidad;
import com.saas.sistema.gimnasio.modulos.usuarios.UsuarioRepository;
import com.saas.sistema.gimnasio.nucleo.seguridad.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService{

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(
            UsuarioRepository usuarioRepository,
            JwtService jwtService,
            AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponseDto login(LoginDto request) {
        // 1. Spring Security valida el correo y la contraseña (hash) contra la BD
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.correo(), request.contrasena())
        );

        // 2. Si las credenciales son correctas, traemos al usuarioEntidad para sacar sus datos
        UsuarioEntidad usuarioEntidad = usuarioRepository.findByCorreo(request.correo())
                .orElseThrow(() -> new RuntimeException("Error: UsuarioEntidad no encontrado en la base de datos"));

        // 3. Generamos el token inyectando el tenantId y el rol en el payload
        String token = jwtService.generarToken(usuarioEntidad, usuarioEntidad.getTenantId().toString(), usuarioEntidad.getRol().name());

        // 4. Devolvemos el token empaquetado
        return new AuthResponseDto(token);
    }
}