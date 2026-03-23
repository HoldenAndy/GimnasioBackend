package com.saas.sistema.gimnasio.modulos.pagos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PagoService {

    public PagoResponseDto registrarPago(PagoRequestDto dto);

    public List<PagoResponseDto> obtenerPagoPorCliente(UUID idCliente);

    List<PagoResponseDto> obtenerPagosPorSuscripcion(UUID idSuscripcion);

    public PagoResponseDto anularPago(UUID id);

    Page<PagoResponseDto> obtenerHistorialPagos(Pageable pageable);
}
