package com.saas.sistema.gimnasio.modulos.clientes;

import com.saas.sistema.gimnasio.modulos.suscripciones.EstadoSuscripcion;
import com.saas.sistema.gimnasio.modulos.suscripciones.SuscripcionRepository;
import com.saas.sistema.gimnasio.nucleo.configuracion.ContextoTenant;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final SuscripcionRepository suscripcionRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository, SuscripcionRepository suscripcionRepository) {
        this.clienteRepository = clienteRepository;
        this.suscripcionRepository = suscripcionRepository;
    }

    private ClienteResponseDto mapearADto(ClienteEntidad c) {
        return new ClienteResponseDto(
                c.getId(),
                c.getNombre(),
                c.getApellido(),
                c.getTipoDocumento(),
                c.getNumeroDocumento(),
                c.getCorreo(),
                c.getTelefono(),
                c.getContactoEmergencia(),
                c.getNotasMedicas(),
                c.getFechaRegistro()
        );
    }

    private ClienteEntidad obtenerEntidadPorId(UUID id) {
        UUID tenantActual = ContextoTenant.getTenantId();
        return clienteRepository.findByIdAndTenantId(id, tenantActual)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado o no pertenece a su gimnasio"));
    }

    @Transactional
    @Override
    public ClienteResponseDto crearCliente(ClienteRequestDto clienteRequestDto) {
        UUID tenantActual = ContextoTenant.getTenantId();

        if (clienteRepository.existsByNumeroDocumentoAndTenantId(clienteRequestDto.numeroDocumento(), tenantActual)) {
            throw new IllegalArgumentException("Ya existe un cliente con el documento: " + clienteRequestDto.numeroDocumento());
        }

       if (clienteRequestDto.correo() != null && !clienteRequestDto.correo().isBlank()) {
            if (clienteRepository.existsByCorreoIgnoreCaseAndTenantId(clienteRequestDto.correo().trim(), tenantActual)) {
                throw new IllegalArgumentException("El correo electrónico ya está registrado en este gimnasio: " + clienteRequestDto.correo());
            }
        }

        ClienteEntidad clienteEntidad = new ClienteEntidad();
        clienteEntidad.setTenantId(tenantActual);
        clienteEntidad.setNombre(clienteRequestDto.nombres());
        clienteEntidad.setApellido(clienteRequestDto.apellidos());
        clienteEntidad.setTipoDocumento(clienteRequestDto.tipoDocumento());
        clienteEntidad.setNumeroDocumento(clienteRequestDto.numeroDocumento());
        clienteEntidad.setCorreo(clienteRequestDto.correo());
        clienteEntidad.setTelefono(clienteRequestDto.telefono());
        clienteEntidad.setContactoEmergencia(clienteRequestDto.contactoEmergencia());
        clienteEntidad.setNotasMedicas(clienteRequestDto.notasMedicas());
        clienteEntidad.setFechaRegistro(LocalDateTime.now());

        return mapearADto(clienteRepository.save(clienteEntidad));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ClienteResponseDto> obtenerTodosLosClientes(String query, java.time.LocalDate inicio, LocalDate fin, boolean estadoActivo, Pageable pageable) {
        UUID tenantActual = ContextoTenant.getTenantId();

        LocalDateTime fechaInicio = (inicio != null) ? inicio.atStartOfDay() : java.time.LocalDateTime.of(1900, 1, 1, 0, 0);
        LocalDateTime fechaFin = (fin != null) ? fin.atTime(java.time.LocalTime.MAX) : java.time.LocalDateTime.of(2100, 12, 31, 23, 59);
        String queryPattern = (query != null && !query.isBlank()) ? "%" + query.trim().toLowerCase() + "%" : "%";
        Page<ClienteEntidad> paginaClientes = clienteRepository.buscarClientesConFiltros(
                tenantActual, queryPattern, fechaInicio, fechaFin, estadoActivo, pageable
        );

        return paginaClientes.map(this::mapearADto);
    }

    @Transactional(readOnly = true)
    @Override
    public ClienteResponseDto obtenerClientePorId(UUID id) {
        return mapearADto(obtenerEntidadPorId(id));
    }

    @Transactional
    @Override
    public void reactivarCliente(UUID id) {
        ClienteEntidad cliente = clienteRepository.findByIdAndTenantId(id, ContextoTenant.getTenantId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
        
        if (cliente.isEstadoActivo()) {
            throw new IllegalArgumentException("El cliente ya se encuentra activo.");
        }
        cliente.setEstadoActivo(true);
        clienteRepository.save(cliente);
    }

    @Transactional
    @Override
    public ClienteResponseDto actualizarCliente(UUID id, ClienteRequestDto dto) {
        UUID tenantActual = ContextoTenant.getTenantId();
        ClienteEntidad clienteEntidad = obtenerEntidadPorId(id);
        if (!clienteEntidad.isEstadoActivo()) {
            throw new IllegalArgumentException("No se puede modificar un cliente que ha sido eliminado/archivado.");
        }
        if (!clienteEntidad.getNumeroDocumento().equals(dto.numeroDocumento()) &&
                clienteRepository.existsByNumeroDocumentoAndTenantId(dto.numeroDocumento(), tenantActual)) {
            throw new IllegalArgumentException("El nuevo documento ya está registrado en otro cliente");
        }

        if (dto.correo() != null && !dto.correo().isBlank()) {
            boolean correoCambio = clienteEntidad.getCorreo() == null || 
                                  !clienteEntidad.getCorreo().equalsIgnoreCase(dto.correo().trim());
            
            if (correoCambio && clienteRepository.existsByCorreoIgnoreCaseAndTenantId(dto.correo().trim(), tenantActual)) {
                throw new IllegalArgumentException("El nuevo correo electrónico ya está siendo usado por otro cliente");
            }
        }

        clienteEntidad.setNombre(dto.nombres());
        clienteEntidad.setApellido(dto.apellidos());
        clienteEntidad.setTipoDocumento(dto.tipoDocumento());
        clienteEntidad.setNumeroDocumento(dto.numeroDocumento());
        clienteEntidad.setCorreo(dto.correo());
        clienteEntidad.setTelefono(dto.telefono());
        clienteEntidad.setContactoEmergencia(dto.contactoEmergencia());
        clienteEntidad.setNotasMedicas(dto.notasMedicas());

        return mapearADto(clienteRepository.save(clienteEntidad));
    }

    @Transactional
    @Override
    public void eliminarCliente(UUID id) {
        ClienteEntidad clienteEntidad = obtenerEntidadPorId(id);
        if (!clienteEntidad.isEstadoActivo()) {
            throw new IllegalArgumentException("El cliente ya se encuentra inactivo en el sistema.");
        }
        clienteEntidad.setEstadoActivo(false);
        clienteRepository.save(clienteEntidad);

        suscripcionRepository.cancelarSuscripcionesPorBajaDeCliente(
                id, 
                clienteEntidad.getTenantId(), 
                EstadoSuscripcion.CANCELADA,
                EstadoSuscripcion.ACTIVA,
                EstadoSuscripcion.PENDIENTE_PAGO
        );
    }
}