package com.saas.sistema.gimnasio.nucleo.seguridad;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${seguridad.jwt.llave-secreta}")
    private String llaveSecreta;

    public String extraerCorreo(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    public String extraerTenantId(String token) {
        return extraerClaimTodos(token).get("tenantId", String.class);
    }

    public String generarToken(UserDetails usuarioDetalles, String tenantId, String rol) {
        Map<String, Object> claimsExtra = new HashMap<>();
        claimsExtra.put("tenantId", tenantId);
        claimsExtra.put("rol", rol);

        return Jwts.builder()
                .setClaims(claimsExtra)
                .setSubject(usuarioDetalles.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 horas
                .signWith(obtenerLlaveFirma(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean esTokenValido(String token, UserDetails usuarioDetalles) {
        final String correo = extraerCorreo(token);
        return (correo.equals(usuarioDetalles.getUsername())) && !esTokenExpirado(token);
    }

    private boolean esTokenExpirado(String token) {
        return extraerClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extraerClaimTodos(token);
        return claimsResolver.apply(claims);
    }

    private Claims extraerClaimTodos(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(obtenerLlaveFirma())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key obtenerLlaveFirma() {
        byte[] keyBytes = Decoders.BASE64.decode(llaveSecreta);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}