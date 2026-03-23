package com.saas.sistema.gimnasio.modulos.suscripciones;

import com.saas.sistema.gimnasio.modulos.clientes.ClienteEntidad;
import com.saas.sistema.gimnasio.modulos.gimnasios.EntidadBaseTenant;
import com.saas.sistema.gimnasio.modulos.planes.PlanMembresiaEntidad;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "suscripciones")
@Data
@EqualsAndHashCode(callSuper = true)
public class SuscripcionEntidad extends EntidadBaseTenant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private ClienteEntidad clienteEntidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_plan", nullable = false)
    private PlanMembresiaEntidad planMembresiaEntidad;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoSuscripcion estadoSuscripcion;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}