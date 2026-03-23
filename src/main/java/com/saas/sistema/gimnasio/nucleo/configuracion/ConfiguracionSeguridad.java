package com.saas.sistema.gimnasio.nucleo.configuracion;

import com.saas.sistema.gimnasio.nucleo.seguridad.JwtFiltro;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Permite usar @PreAuthorize en los controladores más adelante
public class ConfiguracionSeguridad {

    private final JwtFiltro jwtFiltro;
    private final AuthenticationProvider authenticationProvider;

    public ConfiguracionSeguridad(JwtFiltro jwtFiltro, AuthenticationProvider authenticationProvider) {
        this.jwtFiltro = jwtFiltro;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Deshabilitar CSRF ya que se usa JWT (Stateless)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Configuramos los permisos de las rutas
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll() // Login y Registro son públicos
                        .anyRequest().authenticated()                  // El resto requiere token
                )

                // 3. Política de sesión: SIN ESTADO (Stateless)
                .sessionManagement(sesion -> sesion
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 4. Inyectamos nuestro proveedor de autenticación personalizado
                .authenticationProvider(authenticationProvider)

                // 5. Agregamos nuestro filtro JWT antes del filtro de usuario/contraseña de Spring
                .addFilterBefore(jwtFiltro, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}