package cl.grupocordillera.gestion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Espejo del contrato que expone ms-logistica en GET /api/logistica/resumen.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogisticaResumenDTO {
    private Integer totalDespachos;
    private Integer retrasos;
}
