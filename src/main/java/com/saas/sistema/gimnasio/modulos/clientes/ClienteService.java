package com.saas.sistema.gimnasio.modulos.clientes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

public interface ClienteService {
    ClienteResponseDto crearCliente(ClienteRequestDto dto);

    Page<ClienteResponseDto> obtenerTodosLosClientes(String query, LocalDate inicio, LocalDate fin, boolean estadoActivo, Pageable pageable);
    
    ClienteResponseDto obtenerClientePorId(UUID id);

    void reactivarCliente(UUID id);

    ClienteResponseDto actualizarCliente(UUID id, ClienteRequestDto dto);

    void eliminarCliente(UUID id);
}