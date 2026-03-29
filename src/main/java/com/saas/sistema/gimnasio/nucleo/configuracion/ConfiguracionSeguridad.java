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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

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
                // 1. Activamos el CORS usando el Bean que creamos abajo
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. Deshabilitar CSRF ya que se usa JWT (Stateless)
                .csrf(AbstractHttpConfigurer::disable)

                // 3. Configuramos los permisos de las rutas
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll() // Login y Registro son públicos
                        .anyRequest().authenticated()                  // El resto requiere token
                )

                // 4. Política de sesión: SIN ESTADO (Stateless)
                .sessionManagement(sesion -> sesion
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 5. Inyectamos nuestro proveedor de autenticación personalizado
                .authenticationProvider(authenticationProvider)

                // 6. Agregamos nuestro filtro JWT antes del filtro de usuario/contraseña de Spring
                .addFilterBefore(jwtFiltro, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // A. ¿Quién puede entrar? (Aquí pones la URL de tu Frontend)
        // Pongo los puertos por defecto de Angular (4200) y Vite/React (5173)
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200", "http://localhost:3000"));

        // B. ¿Qué métodos pueden usar?
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // C. ¿Qué cabeceras (Headers) aceptamos? (Vital para que pase el Authorization: Bearer token)
        configuration.setAllowedHeaders(List.of("*"));

        // D. Permitir que envíen credenciales (si en algún momento decides usar cookies)
        configuration.setAllowCredentials(true);

        // Aplicamos esta configuración a todas las rutas de nuestra API
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}