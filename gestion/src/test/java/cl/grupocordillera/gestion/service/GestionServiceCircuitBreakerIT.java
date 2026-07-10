package cl.grupocordillera.gestion.service;

import cl.grupocordillera.gestion.client.LogisticaClient;
import cl.grupocordillera.gestion.client.VentasClient;
import cl.grupocordillera.gestion.entity.KpiDiario;
import cl.grupocordillera.gestion.repository.KpiRepository;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

// Contexto de Spring real: el proxy AOP de Resilience4j esta activo, asi que
// el fallback de @CircuitBreaker se dispara de verdad. Usa H2 (ver
// src/test/resources/application.yml), no PostgreSQL.
@SpringBootTest
@Transactional // cada test corre en su propia transaccion y se revierte al terminar
class GestionServiceCircuitBreakerIT {

    @Autowired
    private GestionService gestionService;

    @Autowired
    private KpiRepository kpiRepository;

    @MockBean
    private VentasClient ventasClient;

    @MockBean
    private LogisticaClient logisticaClient;

    @Test
    void testFallbackDevuelveElUltimoKpiHistoricoGuardado() {
        // Dado que ya existe un KPI guardado de "ayer"...
        kpiRepository.save(KpiDiario.builder()
                .fecha(LocalDate.now().minusDays(1))
                .ventasTotales(BigDecimal.valueOf(4_500_000))
                .retrasosLogisticos(2)
                .margenPromedio(1_545_000.0)
                .estadoSistema("OK")
                .build());

        // ...y ms-ventas esta caido
        Request request = Request.create(Request.HttpMethod.GET, "/api/ventas/resumen",
                Collections.emptyMap(), null, new RequestTemplate());
        when(ventasClient.obtenerResumen()).thenThrow(new FeignException.ServiceUnavailable(
                "ms-ventas caido", request, null, null));

        // El fallback debe activarse y devolver el historico, NO lanzar excepcion.
        var resultado = gestionService.calcularKpiDiario();

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEstadoSistema()).isEqualTo("DATOS_HISTORICOS");
        assertThat(resultado.getVentasTotales()).isEqualByComparingTo(BigDecimal.valueOf(4_500_000));
    }

    @Test
    void testFallbackSinHistorialDevuelveEstadoDegradado() {
        Request request = Request.create(Request.HttpMethod.GET, "/api/ventas/resumen",
                Collections.emptyMap(), null, new RequestTemplate());
        when(ventasClient.obtenerResumen()).thenThrow(new FeignException.ServiceUnavailable(
                "ms-ventas caido", request, null, null));

        var resultado = gestionService.calcularKpiDiario();

        assertThat(resultado.getEstadoSistema()).isEqualTo("SISTEMA_DEGRADADO_SIN_HISTORIAL");
    }
}
