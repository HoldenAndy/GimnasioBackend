package com.saas.sistema.gimnasio.modulos.clientes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ClienteService {
    ClienteResponseDto crearCliente(ClienteRequestDto dto);

    Page<ClienteResponseDto> obtenerTodosLosClientes(Pageable pageable);

    ClienteResponseDto obtenerClientePorId(UUID id);

    ClienteResponseDto actualizarCliente(UUID id, ClienteRequestDto dto);

    void eliminarCliente(UUID id);
}