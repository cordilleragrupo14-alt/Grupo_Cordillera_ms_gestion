package cl.grupocordillera.gestion.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Representa el resumen gerencial ya consolidado de un dia. Se persiste para
 * que las proximas consultas al Dashboard sean instantaneas (no hay que
 * volver a preguntarle a ventas/logistica cada vez), y para servir como
 * "datos historicos" cuando el Circuit Breaker esta abierto.
 */
@Entity
@Table(name = "kpi_diario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KpiDiario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private LocalDate fecha;

    @Column(name = "ventas_totales", nullable = false)
    private BigDecimal ventasTotales;

    @Column(name = "retrasos_logisticos", nullable = false)
    private Integer retrasosLogisticos;

    @Column(name = "margen_promedio", nullable = false)
    private Double margenPromedio;

    @Column(name = "estado_sistema", nullable = false)
    private String estadoSistema;
}
