package cl.grupocordillera.gestion.service;

import cl.grupocordillera.gestion.client.LogisticaClient;
import cl.grupocordillera.gestion.client.VentasClient;
import cl.grupocordillera.gestion.dto.LogisticaResumenDTO;
import cl.grupocordillera.gestion.dto.RespuestaKpiDTO;
import cl.grupocordillera.gestion.dto.VentaResumenDTO;
import cl.grupocordillera.gestion.entity.KpiDiario;
import cl.grupocordillera.gestion.repository.KpiRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GestionServiceTest {

    @Mock
    private VentasClient ventasClient;

    @Mock
    private LogisticaClient logisticaClient;

    @Mock
    private KpiRepository kpiRepository;

    @InjectMocks
    private GestionService gestionService;

    @Test
    void testCalcularKpiDiarioAplicaLaFormulaDeMargen() {
        // 35% de margen bruto sobre 1.000.000 = 350.000
        // menos 2 retrasos * 15.000 = 30.000 -> margen esperado 320.000
        when(ventasClient.obtenerResumen())
                .thenReturn(new VentaResumenDTO(BigDecimal.valueOf(1_000_000), 10));
        when(logisticaClient.obtenerResumen())
                .thenReturn(new LogisticaResumenDTO(20, 2));
        when(kpiRepository.save(any(KpiDiario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RespuestaKpiDTO resultado = gestionService.calcularKpiDiario();

        assertEquals(320_000.0, resultado.getMargenPromedio());
        assertEquals("OK", resultado.getEstadoSistema());
        assertEquals(2, resultado.getRetrasosLogisticos());
    }

    @Test
    void testCalcularKpiDiarioPersisteElResultado() {
        when(ventasClient.obtenerResumen())
                .thenReturn(new VentaResumenDTO(BigDecimal.valueOf(500_000), 5));
        when(logisticaClient.obtenerResumen())
                .thenReturn(new LogisticaResumenDTO(10, 0));
        when(kpiRepository.save(any(KpiDiario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        gestionService.calcularKpiDiario();

        ArgumentCaptor<KpiDiario> captor = ArgumentCaptor.forClass(KpiDiario.class);
        verify(kpiRepository, times(1)).save(captor.capture());
        assertEquals(LocalDate.now(), captor.getValue().getFecha());
        assertEquals("OK", captor.getValue().getEstadoSistema());
    }

    @Test
    void testObtenerHistorialMapeaEntidadesADto() {
        KpiDiario guardado = KpiDiario.builder()
                .id(1L)
                .fecha(LocalDate.of(2026, 7, 9))
                .ventasTotales(BigDecimal.valueOf(6_100_000))
                .retrasosLogisticos(0)
                .margenPromedio(2_135_000.0)
                .estadoSistema("OK")
                .build();
        when(kpiRepository.findTop10ByOrderByFechaDesc()).thenReturn(List.of(guardado));

        List<RespuestaKpiDTO> historial = gestionService.obtenerHistorial();

        assertEquals(1, historial.size());
        assertEquals(LocalDate.of(2026, 7, 9), historial.get(0).getFecha());
        assertEquals(0, historial.get(0).getRetrasosLogisticos());
    }
}
