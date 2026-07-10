package cl.grupocordillera.gestion.repository;

import cl.grupocordillera.gestion.entity.KpiDiario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface KpiRepository extends JpaRepository<KpiDiario, Long> {

    Optional<KpiDiario> findByFecha(LocalDate fecha);

    // Usado por el fallback del Circuit Breaker: si no se puede calcular el
    // KPI en vivo, se devuelve el ultimo registro guardado.
    Optional<KpiDiario> findTopByOrderByFechaDesc();

    List<KpiDiario> findTop10ByOrderByFechaDesc();
}
