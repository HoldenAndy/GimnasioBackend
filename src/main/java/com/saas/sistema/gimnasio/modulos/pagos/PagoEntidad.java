package com.saas.sistema.gimnasio.modulos.pagos;

import com.saas.sistema.gimnasio.modulos.gimnasios.EntidadBaseTenant;
import com.saas.sistema.gimnasio.modulos.suscripciones.SuscripcionEntidad;
import com.saas.sistema.gimnasio.modulos.usuarios.UsuarioEntidad;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pagos")
@Data
@EqualsAndHashCode(callSuper = true)
public class PagoEntidad extends EntidadBaseTenant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_suscripcion", nullable = false)
    private SuscripcionEntidad suscripcionEntidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_receptor", nullable = false)
    private UsuarioEntidad usuarioEntidadReceptor;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false, length = 50)
    private MetodoPago metodoPago;

    @Column(name = "numero_referencia", length = 100)
    private String numeroReferencia;

    @Column(name = "fecha_pago", nullable = false, updatable = false)
    private LocalDateTime fechaPago = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String notas;

    @Column(name = "estado_activo", nullable = false)
    private boolean estadoActivo;
}