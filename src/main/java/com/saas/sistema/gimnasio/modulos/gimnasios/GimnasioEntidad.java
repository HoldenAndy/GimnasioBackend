package com.saas.sistema.gimnasio.modulos.gimnasios;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "gimnasios")
@Data
public class GimnasioEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "razon_social", nullable = false)
    private String razonSocial;

    @Column(nullable = false, unique = true, length = 20)
    private String ruc;

    @Column(name = "correo_contacto", nullable = false)
    private String correoContacto;

    @Column(name = "telefono_contacto", length = 20)
    private String telefonoContacto;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @Column(name = "estado_activo", nullable = false)
    private boolean estadoActivo = true;
}