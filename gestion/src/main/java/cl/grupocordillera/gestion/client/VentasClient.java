package cl.grupocordillera.gestion.client;

import cl.grupocordillera.gestion.dto.VentaResumenDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "ms-ventas", url = "${clients.ventas.url}")
public interface VentasClient {

    @GetMapping("/api/ventas/resumen")
    VentaResumenDTO obtenerResumen();
}
