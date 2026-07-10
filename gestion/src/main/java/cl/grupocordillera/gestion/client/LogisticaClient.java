package cl.grupocordillera.gestion.client;

import cl.grupocordillera.gestion.dto.LogisticaResumenDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "ms-logistica", url = "${clients.logistica.url}")
public interface LogisticaClient {

    @GetMapping("/api/logistica/resumen")
    LogisticaResumenDTO obtenerResumen();
}
