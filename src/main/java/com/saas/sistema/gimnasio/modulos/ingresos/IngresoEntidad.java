package com.saas.sistema.gimnasio.modulos.ingresos;

import com.saas.sistema.gimnasio.modulos.gimnasios.EntidadBaseTenant;
import com.saas.sistema.gimnasio.modulos.clientes.ClienteEntidad;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ingresos")
@Data
@EqualsAndHashCode(callSuper = true)
public class IngresoEntidad extends EntidadBaseTenant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private ClienteEntidad clienteEntidad;

    @Column(name = "fecha_hora_ingreso", nullable = false, updatable = false)
    private LocalDateTime fechaHoraIngreso = LocalDateTime.now();

    @Column(name = "acceso_permitido", nullable = false)
    private boolean accesoPermitido;

    @Column(name = "motivo_rechazo", length = 100)
    private String motivoRechazo;
}