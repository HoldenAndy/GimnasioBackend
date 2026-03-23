package com.saas.sistema.gimnasio.modulos.ingresos;

import com.saas.sistema.gimnasio.nucleo.configuracion.ContextoTenant;
import com.saas.sistema.gimnasio.modulos.clientes.ClienteEntidad;
import com.saas.sistema.gimnasio.modulos.suscripciones.SuscripcionEntidad;
import com.saas.sistema.gimnasio.modulos.clientes.ClienteRepository;
import com.saas.sistema.gimnasio.modulos.suscripciones.EstadoSuscripcion;
import com.saas.sistema.gimnasio.modulos.suscripciones.SuscripcionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class IngresoServiceImpl implements IngresoService {

    private final IngresoRepository ingresoRepository;
    private final ClienteRepository clienteRepository;
    private final SuscripcionRepository suscripcionRepository;

    public IngresoServiceImpl(
            IngresoRepository ingresoRepository,
            ClienteRepository clienteRepository,
            SuscripcionRepository suscripcionRepository) {
        this.ingresoRepository = ingresoRepository;
        this.clienteRepository = clienteRepository;
        this.suscripcionRepository = suscripcionRepository;
    }

    private IngresoResponseDto mapearADto(IngresoEntidad i) {
        String mensaje = i.isAccesoPermitido()
                ? "Acceso concedido. ¡Buen entrenamiento!"
                : "Acceso denegado: " + i.getMotivoRechazo();

        String nombreCompleto = i.getClienteEntidad().getNombre();

        return new IngresoResponseDto(
                i.isAccesoPermitido(),
                mensaje,
                nombreCompleto,
                i.getFechaHoraIngreso()
        );
    }

    @Transactional
    @Override
    public IngresoResponseDto registrarIntentoIngresoPorId(UUID idCliente) {
        UUID tenantActual = ContextoTenant.getTenantId();
        LocalDateTime ahora = LocalDateTime.now();
        LocalDate hoy = ahora.toLocalDate();

        // 1. Buscar al clienteEntidad
        ClienteEntidad clienteEntidad = clienteRepository.findByIdAndTenantId(idCliente, tenantActual)
                .orElseThrow(() -> new EntityNotFoundException("ClienteEntidad no encontrado en este gimnasio"));

        // 2. Buscar si tiene una suscripción que esté ACTIVA
        List<SuscripcionEntidad> suscripciones = suscripcionRepository
                .findByClienteEntidadIdAndTenantIdOrderByFechaCreacionDesc(idCliente, tenantActual);

        Optional<SuscripcionEntidad> suscripcionValida = suscripciones.stream()
                .filter(s -> s.getEstadoSuscripcion().equals(EstadoSuscripcion.ACTIVA))
                .filter(s -> !hoy.isBefore(s.getFechaInicio()) && !hoy.isAfter(s.getFechaFin()))
                .findFirst();

        Optional<IngresoEntidad> ultimoIngreso = ingresoRepository.findFirstByClienteEntidadIdAndTenantIdOrderByFechaHoraIngresoDesc(idCliente, tenantActual);

        if (ultimoIngreso.isPresent()) {
            LocalDateTime hace5Minutos = ahora.minusMinutes(5);
            if (ultimoIngreso.get().getFechaHoraIngreso().isAfter(hace5Minutos)) {
                boolean eraPermitido = ultimoIngreso.get().isAccesoPermitido();
                String msj = eraPermitido ? "Acceso ya concedido hace un momento." : "Acceso denegado previamente.";
                return new IngresoResponseDto(eraPermitido, msj, clienteEntidad.getNombre(), ahora);
            }
        }

        IngresoEntidad registro = new IngresoEntidad();
        registro.setTenantId(tenantActual);
        registro.setClienteEntidad(clienteEntidad);
        registro.setFechaHoraIngreso(ahora);

        // 3. Lógica de decisión
        if (suscripcionValida.isPresent()) {
            registro.setAccesoPermitido(true);
            registro.setMotivoRechazo(null);
        } else {
            registro.setAccesoPermitido(false);
            registro.setMotivoRechazo("No tiene una suscripción activa o vigente para el día de hoy.");
        }

        // 4. Guardamos en la BD y lo pasamos por el mapeador para devolverlo
        return mapearADto(ingresoRepository.save(registro));
    }

    @Transactional
    @Override
    public IngresoResponseDto registrarIntentoIngresoPorDocumento(String numeroDocumento) {
        UUID tenantActual = ContextoTenant.getTenantId();

        ClienteEntidad clienteEntidad = clienteRepository.findByNumeroDocumentoAndTenantId(numeroDocumento, tenantActual)
                .orElseThrow(() -> new EntityNotFoundException("No existe un clienteEntidad con el documento " + numeroDocumento + " en este gimnasio."));

        return registrarIntentoIngresoPorId(clienteEntidad.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public Page<IngresoResponseDto> obtenerHistorialIngresos(Pageable pageable) {
        return ingresoRepository.findByTenantId(ContextoTenant.getTenantId(), pageable)
                .map(this::mapearADto);
    }
}