package cl.grupocordillera.gestion.repository;

import cl.grupocordillera.gestion.entity.KpiDiario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class KpiRepositoryTest {

    @Autowired
    private KpiRepository kpiRepository;

    @Test
    void testFindTopByOrderByFechaDescDevuelveElMasReciente() {
        kpiRepository.save(kpi(LocalDate.of(2026, 7, 7), "OK"));
        kpiRepository.save(kpi(LocalDate.of(2026, 7, 9), "OK"));
        kpiRepository.save(kpi(LocalDate.of(2026, 7, 8), "OK"));

        Optional<KpiDiario> masReciente = kpiRepository.findTopByOrderByFechaDesc();

        assertThat(masReciente).isPresent();
        assertThat(masReciente.get().getFecha()).isEqualTo(LocalDate.of(2026, 7, 9));
    }

    @Test
    void testFindByFecha() {
        kpiRepository.save(kpi(LocalDate.of(2026, 7, 9), "OK"));

        Optional<KpiDiario> encontrado = kpiRepository.findByFecha(LocalDate.of(2026, 7, 9));

        assertThat(encontrado).isPresent();
    }

    @Test
    void testFindTopByOrderByFechaDescVacioSinDatos() {
        Optional<KpiDiario> resultado = kpiRepository.findTopByOrderByFechaDesc();

        assertThat(resultado).isEmpty();
    }

    private KpiDiario kpi(LocalDate fecha, String estado) {
        return KpiDiario.builder()
                .fecha(fecha)
                .ventasTotales(BigDecimal.valueOf(1_000_000))
                .retrasosLogisticos(1)
                .margenPromedio(350_000.0)
                .estadoSistema(estado)
                .build();
    }
}
