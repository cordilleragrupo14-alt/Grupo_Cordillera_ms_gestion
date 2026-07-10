package cl.grupocordillera.gestion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Lo unico que ve el frontend/BFF. Nunca se expone la entidad KpiDiario
 * directamente para no acoplar la base de datos con el contrato publico.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RespuestaKpiDTO {
    private LocalDate fecha;
    private BigDecimal ventasTotales;
    private Integer retrasosLogisticos;
    private Double margenPromedio;
    // OK | DATOS_HISTORICOS | SISTEMA_DEGRADADO
    private String estadoSistema;
}
