package cl.grupocordillera.gestion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Espejo del contrato que expone ms-ventas en GET /api/ventas/resumen.
 * Es un DTO propio de este microservicio: si ms-ventas cambia su modelo
 * interno, este contrato no se ve afectado mientras el endpoint no cambie.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaResumenDTO {
    private BigDecimal totalVentas;
    private Integer cantidadVentas;
}
