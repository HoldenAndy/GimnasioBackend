package com.saas.sistema.gimnasio.tareas;

import com.saas.sistema.gimnasio.modulos.suscripciones.EstadoSuscripcion;
import com.saas.sistema.gimnasio.modulos.suscripciones.SuscripcionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
public class SuscripcionScheduler {

    private static final Logger log = LoggerFactory.getLogger(SuscripcionScheduler.class);
    private final SuscripcionRepository suscripcionRepository;

    public SuscripcionScheduler(SuscripcionRepository suscripcionRepository) {
        this.suscripcionRepository = suscripcionRepository;
    }

    @Scheduled(cron = "0 1 0 * * ?", zone = "America/Lima")
    @Transactional
    public void verificarSuscripcionesVencidas() {
        log.info("Iniciando tarea automática: Verificación de suscripciones vencidas...");

        LocalDate hoy = LocalDate.now();

        int cantidadActualizadas = suscripcionRepository.actualizarEstadosVencidos(EstadoSuscripcion.ACTIVA, EstadoSuscripcion.VENCIDA, hoy
        );

        log.info("Tarea finalizada. Se cortó el acceso a {} suscripciones que vencieron ayer.", cantidadActualizadas);
    }
}