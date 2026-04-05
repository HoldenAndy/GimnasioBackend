package com.saas.sistema.gimnasio.modulos.reportes;

import com.saas.sistema.gimnasio.modulos.ingresos.IngresoRepository;
import com.saas.sistema.gimnasio.modulos.pagos.PagoRepository;
import com.saas.sistema.gimnasio.modulos.suscripciones.EstadoSuscripcion;
import com.saas.sistema.gimnasio.modulos.suscripciones.SuscripcionRepository;
import com.saas.sistema.gimnasio.nucleo.configuracion.ContextoTenant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.UUID;

@Service
public class ReportesServiceImpl implements ReportesService {

    private final PagoRepository pagoRepository;
    private final SuscripcionRepository suscripcionRepository;
    private final IngresoRepository ingresoRepository;

    public ReportesServiceImpl(PagoRepository pagoRepository, SuscripcionRepository suscripcionRepository, IngresoRepository ingresoRepository) {
        this.pagoRepository = pagoRepository;
        this.suscripcionRepository = suscripcionRepository;
        this.ingresoRepository = ingresoRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public DashboardResponseDto obtenerMetricasDashboard() {
        UUID tenantActual = ContextoTenant.getTenantId();

        //Cálculos de Fechas Dinámicas
        LocalDate hoy = LocalDate.now();
        LocalDateTime inicioDelDia = hoy.atStartOfDay(); // 00:00:00
        LocalDateTime finDelDia = hoy.atTime(23, 59, 59);

        YearMonth mesActual = YearMonth.now();
        LocalDateTime inicioDelMes = mesActual.atDay(1).atStartOfDay();
        LocalDateTime finDelMes = mesActual.atEndOfMonth().atTime(23, 59, 59);

        LocalDate dentroDeSieteDias = hoy.plusDays(7);

        BigDecimal recaudacion = pagoRepository.calcularRecaudacionDelMes(tenantActual, inicioDelMes, finDelMes);
        long clientesActivos = suscripcionRepository.countByTenantIdAndEstadoSuscripcion(tenantActual, EstadoSuscripcion.ACTIVA);
        long suscripcionesPorVencer = suscripcionRepository.contarSuscripcionesPorVencer(tenantActual, EstadoSuscripcion.ACTIVA, hoy, dentroDeSieteDias);
        long asistencias = ingresoRepository.contarAsistenciasDelDia(tenantActual, inicioDelDia, finDelDia);

        return new DashboardResponseDto(
                recaudacion,
                clientesActivos,
                suscripcionesPorVencer,
                asistencias
        );
    }
}