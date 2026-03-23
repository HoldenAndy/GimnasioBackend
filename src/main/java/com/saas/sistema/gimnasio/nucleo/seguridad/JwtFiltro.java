package com.saas.sistema.gimnasio.nucleo.seguridad;

import com.saas.sistema.gimnasio.nucleo.configuracion.ContextoTenant;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class JwtFiltro extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtFiltro(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest peticion,
            @NonNull HttpServletResponse respuesta,
            @NonNull FilterChain cadena) throws ServletException, IOException {

        final String encabezadoAuth = peticion.getHeader("Authorization");

        if (encabezadoAuth == null || !encabezadoAuth.startsWith("Bearer ")) {
            cadena.doFilter(peticion, respuesta);
            return;
        }

        try {
            final String token = encabezadoAuth.substring(7);

            // 1. Extraemos e inyectamos el Tenant ID
            String tenantExtraido = jwtService.extraerTenantId(token);
            if (tenantExtraido != null) {
                ContextoTenant.setTenantId(UUID.fromString(tenantExtraido));
            }

            // 2. Extraemos el correo para Spring Security
            final String correo = jwtService.extraerCorreo(token);

            // 3. LA LÓGICA DE SPRING SECURITY
            // Si hay correo y el usuario aún no está autenticado en este hilo...
            if (correo != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Buscamos al usuario en la BD
                UserDetails usuarioDetalles = this.userDetailsService.loadUserByUsername(correo);

                // Validamos que el token pertenezca a este usuario y no esté expirado
                if (jwtService.esTokenValido(token, usuarioDetalles)) {

                    // Creamos el "Pase de Acceso" oficial de Spring Security
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            usuarioDetalles,
                            null, // No pasamos la contraseña por seguridad
                            usuarioDetalles.getAuthorities() // Aquí van los ROLES (ej. ROLE_ADMINISTRADOR)
                    );

                    // Le agregamos detalles de la petición web (IP, sesión, etc.)
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(peticion));

                    // ¡Registramos al usuario en el contexto de seguridad!
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            // Continuamos con el flujo normal de la petición
            cadena.doFilter(peticion, respuesta);

        } finally {
            ContextoTenant.limpiar(); // Se ejecuta siempre para evitar fugas de datos
        }
    }
}