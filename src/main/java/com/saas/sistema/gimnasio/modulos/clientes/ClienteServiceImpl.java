package com.saas.sistema.gimnasio.modulos.clientes;

import com.saas.sistema.gimnasio.nucleo.configuracion.ContextoTenant;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
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
        return clienteRepository.findByIdAndTenantId(id, ContextoTenant.getTenantId())
                .orElseThrow(() -> new EntityNotFoundException("ClienteEntidad no encontrado o no pertenece a su gimnasio"));
    }

    @Transactional
    @Override
    public ClienteResponseDto crearCliente(ClienteRequestDto clienteRequestDto) {
        UUID tenantActual = ContextoTenant.getTenantId();

        if (clienteRepository.existsByNumeroDocumentoAndTenantId(clienteRequestDto.numeroDocumento(), tenantActual)) {
            throw new IllegalArgumentException("Ya existe un clienteEntidad con el documento: " + clienteRequestDto.numeroDocumento());
        }

        if (clienteRepository.existsByCorreoIgnoreCaseAndTenantId(clienteRequestDto.correo().trim(), tenantActual)) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado en este gimnasio: " + clienteRequestDto.correo());
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
    public Page<ClienteResponseDto> obtenerTodosLosClientes(Pageable pageable) {
        UUID tenantActual = ContextoTenant.getTenantId();

        Page<ClienteEntidad> paginaClientes = clienteRepository.findByTenantIdAndEstadoActivoTrue(tenantActual, pageable);

        return paginaClientes.map(this::mapearADto);
    }

    @Transactional(readOnly = true)
    @Override
    public ClienteResponseDto obtenerClientePorId(UUID id) {
        return mapearADto(obtenerEntidadPorId(id));
    }

    @Transactional
    @Override
    public ClienteResponseDto actualizarCliente(UUID id, ClienteRequestDto dto) {
        UUID tenantActual = ContextoTenant.getTenantId();
        ClienteEntidad clienteEntidad = obtenerEntidadPorId(id);
        if (!clienteEntidad.isEstadoActivo()) {
            throw new IllegalArgumentException("No se puede modificar un clienteEntidad que ha sido eliminado/archivado.");
        }
        if (!clienteEntidad.getNumeroDocumento().equals(dto.numeroDocumento()) &&
                clienteRepository.existsByNumeroDocumentoAndTenantId(dto.numeroDocumento(), tenantActual)) {
            throw new IllegalArgumentException("El nuevo documento ya está registrado en otro clienteEntidad");
        }

        if (!clienteEntidad.getCorreo().equalsIgnoreCase(dto.correo().trim()) &&
                clienteRepository.existsByCorreoIgnoreCaseAndTenantId(dto.correo().trim(), tenantActual)) {
            throw new IllegalArgumentException("El nuevo correo electrónico ya está siendo usado por otro clienteEntidad");
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
            throw new IllegalArgumentException("El clienteEntidad ya se encuentra inactivo en el sistema.");
        }
        clienteEntidad.setEstadoActivo(false);
        clienteRepository.save(clienteEntidad);
    }
}