package cl.grupocordillera.gestion.service;

import cl.grupocordillera.gestion.client.LogisticaClient;
import cl.grupocordillera.gestion.client.VentasClient;
import cl.grupocordillera.gestion.dto.LogisticaResumenDTO;
import cl.grupocordillera.gestion.dto.RespuestaKpiDTO;
import cl.grupocordillera.gestion.dto.VentaResumenDTO;
import cl.grupocordillera.gestion.entity.KpiDiario;
import cl.grupocordillera.gestion.repository.KpiRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class GestionService {

    private static final Logger log = LoggerFactory.getLogger(GestionService.class);

    // Regla de negocio de ejemplo para el margen: 35% de margen bruto sobre
    // ventas, penalizado por cada retraso logistico. Ajustar segun finanzas.
    private static final BigDecimal MARGEN_BRUTO_ESPERADO = BigDecimal.valueOf(0.35);
    private static final BigDecimal PENALIZACION_POR_RETRASO = BigDecimal.valueOf(15000);

    @Autowired
    private VentasClient ventasClient;

    @Autowired
    private LogisticaClient logisticaClient;

    @Autowired
    private KpiRepository kpiRepository;

    /**
     * Consolida en vivo los datos de ms-ventas y ms-logistica, calcula el
     * margen del dia y lo persiste. Si cualquiera de los dos microservicios
     * falla (o esta lento), se activa el fallback con datos historicos.
     */
    @CircuitBreaker(name = "gestionCB", fallbackMethod = "fallbackKpiDiario")
    public RespuestaKpiDTO calcularKpiDiario() {
        VentaResumenDTO ventas = ventasClient.obtenerResumen();
        LogisticaResumenDTO logistica = logisticaClient.obtenerResumen();

        Double margen = calcularMargen(ventas.getTotalVentas(), logistica.getRetrasos());

        KpiDiario kpi = KpiDiario.builder()
                .fecha(LocalDate.now())
                .ventasTotales(ventas.getTotalVentas())
                .retrasosLogisticos(logistica.getRetrasos())
                .margenPromedio(margen)
                .estadoSistema("OK")
                .build();

        kpiRepository.save(kpi);

        return mapearADto(kpi);
    }

    /**
     * Historial para graficos del dashboard (no pasa por el CB porque solo
     * lee de la base propia, no depende de otros microservicios).
     */
    public List<RespuestaKpiDTO> obtenerHistorial() {
        return kpiRepository.findTop10ByOrderByFechaDesc()
                .stream()
                .map(this::mapearADto)
                .toList();
    }

    private Double calcularMargen(BigDecimal ventasTotales, Integer retrasos) {
        BigDecimal margenBruto = ventasTotales.multiply(MARGEN_BRUTO_ESPERADO);
        BigDecimal penalizacion = PENALIZACION_POR_RETRASO.multiply(BigDecimal.valueOf(retrasos));
        return margenBruto.subtract(penalizacion).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private RespuestaKpiDTO mapearADto(KpiDiario kpi) {
        return RespuestaKpiDTO.builder()
                .fecha(kpi.getFecha())
                .ventasTotales(kpi.getVentasTotales())
                .retrasosLogisticos(kpi.getRetrasosLogisticos())
                .margenPromedio(kpi.getMargenPromedio())
                .estadoSistema(kpi.getEstadoSistema())
                .build();
    }

    /**
     * Fallback del Circuit Breaker "gestionCB". Se ejecuta cuando ms-ventas
     * o ms-logistica no responden. En vez de romper el Dashboard, devuelve
     * el ultimo KPI ya calculado y guardado (modo degradado, transparente
     * para el usuario gerencial).
     */
    private RespuestaKpiDTO fallbackKpiDiario(Throwable t) {
        log.warn("Circuit breaker gestionCB activado en calcularKpiDiario(): {}", t.getMessage());

        return kpiRepository.findTopByOrderByFechaDesc()
                .map(kpi -> RespuestaKpiDTO.builder()
                        .fecha(kpi.getFecha())
                        .ventasTotales(kpi.getVentasTotales())
                        .retrasosLogisticos(kpi.getRetrasosLogisticos())
                        .margenPromedio(kpi.getMargenPromedio())
                        .estadoSistema("DATOS_HISTORICOS")
                        .build())
                .orElse(RespuestaKpiDTO.builder()
                        .fecha(LocalDate.now())
                        .ventasTotales(BigDecimal.ZERO)
                        .retrasosLogisticos(0)
                        .margenPromedio(0.0)
                        .estadoSistema("SISTEMA_DEGRADADO_SIN_HISTORIAL")
                        .build());
    }
}
