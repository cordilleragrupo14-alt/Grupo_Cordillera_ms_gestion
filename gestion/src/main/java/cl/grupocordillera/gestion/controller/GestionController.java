package cl.grupocordillera.gestion.controller;

import cl.grupocordillera.gestion.dto.RespuestaKpiDTO;
import cl.grupocordillera.gestion.service.GestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/gestion")
@Tag(name = "Gestion KPI", description = "Consolidacion de KPIs gerenciales para el Dashboard")
public class GestionController {

    @Autowired
    private GestionService gestionService;

    @Operation(summary = "Calcula y devuelve el KPI del dia consolidando ventas + logistica")
    @GetMapping("/kpi/diario")
    public ResponseEntity<RespuestaKpiDTO> obtenerKpiDiario() {
        return ResponseEntity.ok(gestionService.calcularKpiDiario());
    }

    @Operation(summary = "Devuelve el historial de los ultimos 10 KPIs guardados")
    @GetMapping("/kpi/historial")
    public ResponseEntity<List<RespuestaKpiDTO>> obtenerHistorial() {
        return ResponseEntity.ok(gestionService.obtenerHistorial());
    }
}
