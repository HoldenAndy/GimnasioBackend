package com.saas.sistema.gimnasio.modulos.clientes;

import com.saas.sistema.gimnasio.modulos.gimnasios.EntidadBaseTenant;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "clientes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tenant_id", "numero_documento"})
})
@Data
@EqualsAndHashCode(callSuper = true)
public class ClienteEntidad extends EntidadBaseTenant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tipo_documento", nullable = false, length = 10)
    private String tipoDocumento;

    @Column(name = "numero_documento", nullable = false, length = 20)
    private String numeroDocumento;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(length = 255)
    private String correo;

    @Column(length = 20)
    private String telefono;

    @Column(name = "contacto_emergencia", length = 50)
    private String contactoEmergencia;

    @Column(name = "notas_medicas", columnDefinition = "TEXT")
    private String notasMedicas;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @Column(name = "estado_activo", nullable = false)
    private boolean estadoActivo = true;
}