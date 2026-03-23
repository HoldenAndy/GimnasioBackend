package com.saas.sistema.gimnasio.modulos.planes;

import com.saas.sistema.gimnasio.modulos.gimnasios.EntidadBaseTenant;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "planes_membresia")
@Data
@EqualsAndHashCode(callSuper = true)
public class PlanMembresiaEntidad extends EntidadBaseTenant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "duracion_dias", nullable = false)
    private Integer duracionDias;

    @Column(name = "estado_activo", nullable = false)
    private boolean estadoActivo = true;
}