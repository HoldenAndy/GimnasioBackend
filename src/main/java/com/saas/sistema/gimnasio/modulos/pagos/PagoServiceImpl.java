package com.saas.sistema.gimnasio.modulos.pagos;

import com.saas.sistema.gimnasio.modulos.suscripciones.EstadoSuscripcion;
import com.saas.sistema.gimnasio.nucleo.configuracion.ContextoTenant;
import com.saas.sistema.gimnasio.modulos.suscripciones.SuscripcionEntidad;
import com.saas.sistema.gimnasio.modulos.suscripciones.SuscripcionRepository;
import com.saas.sistema.gimnasio.modulos.usuarios.UsuarioEntidad;
import com.saas.sistema.gimnasio.modulos.usuarios.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final SuscripcionRepository suscripcionRepository;
    private final UsuarioRepository usuarioRepository;

    public PagoServiceImpl(
            PagoRepository pagoRepository,
            SuscripcionRepository suscripcionRepository,
            UsuarioRepository usuarioRepository) {
        this.pagoRepository = pagoRepository;
        this.suscripcionRepository = suscripcionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private PagoResponseDto mapearADto(PagoEntidad p) {
        String nombreCompleto = p.getSuscripcionEntidad().getClienteEntidad().getNombre() + " " +
                p.getSuscripcionEntidad().getClienteEntidad().getApellido();
        return new PagoResponseDto(
                p.getId(),
                nombreCompleto,
                p.getSuscripcionEntidad().getPlanMembresiaEntidad().getNombre(),
                p.getMonto(),
                p.getMetodoPago(),
                p.getNumeroReferencia(),
                p.getFechaPago(),
                p.getUsuarioEntidadReceptor().getCorreo(),
                p.getNotas(),
                p.isEstadoActivo()
        );
    }

    @Transactional
    @Override
    public PagoResponseDto registrarPago(PagoRequestDto dto) {
        UUID tenantActual = ContextoTenant.getTenantId();
        if (dto.numeroReferencia() != null && !dto.numeroReferencia().isBlank()) {
            if (pagoRepository.existsByNumeroReferenciaAndTenantIdAndEstadoActivoTrue(dto.numeroReferencia(), tenantActual)) {
                throw new IllegalArgumentException("Ya existe un pagoEntidad registrado con el número de referencia: " + dto.numeroReferencia());
            }
        }
        String correoCajero = SecurityContextHolder.getContext().getAuthentication().getName();
        UsuarioEntidad cajero = usuarioRepository.findByCorreo(correoCajero)
                .orElseThrow(() -> new EntityNotFoundException("UsuarioEntidad receptor no encontrado en el sistema"));

        SuscripcionEntidad suscripcionEntidad = suscripcionRepository.findByIdAndTenantId(dto.idSuscripcion(), tenantActual)
                .orElseThrow(() -> new EntityNotFoundException("La suscripción no existe o no pertenece a este gimnasio"));

        BigDecimal precioTotalDelPlan = suscripcionEntidad.getPlanMembresiaEntidad().getPrecio();
        BigDecimal totalYaPagado = pagoRepository.obtenerTotalPagadoPorSuscripcion(suscripcionEntidad.getId(), tenantActual);

        if (totalYaPagado.compareTo(precioTotalDelPlan) >= 0) {
            throw new IllegalArgumentException("Esta suscripción ya está pagada en su totalidad.");
        }

        BigDecimal nuevoTotalProyectado = totalYaPagado.add(dto.monto());
        if (nuevoTotalProyectado.compareTo(precioTotalDelPlan) > 0) {
            BigDecimal saldoPendiente = precioTotalDelPlan.subtract(totalYaPagado);
            throw new IllegalArgumentException("El monto ingresado supera la deuda. El saldo pendiente a pagar es de: " + saldoPendiente);
        }

        PagoEntidad pagoEntidad = new PagoEntidad();
        pagoEntidad.setTenantId(tenantActual);
        pagoEntidad.setSuscripcionEntidad(suscripcionEntidad);
        pagoEntidad.setUsuarioEntidadReceptor(cajero);
        pagoEntidad.setMonto(dto.monto());
        pagoEntidad.setMetodoPago(dto.metodoPago());
        pagoEntidad.setNumeroReferencia(dto.numeroReferencia());
        pagoEntidad.setNotas(dto.notas());
        pagoEntidad.setEstadoActivo(true);

        if (nuevoTotalProyectado.compareTo(precioTotalDelPlan) == 0) {
            suscripcionEntidad.setEstadoSuscripcion(EstadoSuscripcion.ACTIVA);
            suscripcionRepository.save(suscripcionEntidad);
        }

        return mapearADto(pagoRepository.save(pagoEntidad));
    }

    @Transactional(readOnly = true)
    @Override
    public List<PagoResponseDto> obtenerPagoPorCliente(UUID idCliente) {
        return pagoRepository.findBySuscripcionEntidad_ClienteEntidad_IdAndTenantIdOrderByFechaPagoDesc(
                        idCliente, ContextoTenant.getTenantId())
                .stream()
                .map(this::mapearADto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<PagoResponseDto> obtenerPagosPorSuscripcion(UUID idSuscripcion) {
        UUID tenantActual = ContextoTenant.getTenantId();
        return pagoRepository.findBySuscripcionEntidad_IdAndTenantIdOrderByFechaPagoDesc(idSuscripcion, tenantActual)
                .stream()
                .map(this::mapearADto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<PagoResponseDto> obtenerHistorialPagos(Pageable pageable) {
        UUID tenantActual = ContextoTenant.getTenantId();
        return pagoRepository.findByTenantId(tenantActual, pageable)
                .map(this::mapearADto);
    }

    @Transactional
    @Override
    public PagoResponseDto anularPago(UUID id) {
        UUID tenantActual = ContextoTenant.getTenantId();
        PagoEntidad pagoEntidad = pagoRepository.findByIdAndTenantId(id, tenantActual)
                .orElseThrow(() -> new EntityNotFoundException("PagoEntidad no encontrado"));

        if (!pagoEntidad.isEstadoActivo()) {
            throw new IllegalArgumentException("Este pagoEntidad ya se encuentra anulado.");
        }

        pagoEntidad.setEstadoActivo(false);
        String correoCajero = SecurityContextHolder.getContext().getAuthentication().getName();
        pagoEntidad.setNotas((pagoEntidad.getNotas() == null ? "" : pagoEntidad.getNotas() + " ") + "[ANULADO por: " + correoCajero + "]");

        pagoRepository.saveAndFlush(pagoEntidad);
        SuscripcionEntidad suscripcionEntidad = pagoEntidad.getSuscripcionEntidad();
        BigDecimal precioTotalDelPlan = suscripcionEntidad.getPlanMembresiaEntidad().getPrecio();

        BigDecimal totalYaPagado = pagoRepository.obtenerTotalPagadoPorSuscripcion(suscripcionEntidad.getId(), tenantActual);
        if (totalYaPagado.compareTo(precioTotalDelPlan) < 0) {
            suscripcionEntidad.setEstadoSuscripcion(EstadoSuscripcion.PENDIENTE_PAGO);
            suscripcionRepository.save(suscripcionEntidad);
        }
        return mapearADto(pagoEntidad);
    }
}