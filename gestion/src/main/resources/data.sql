-- Datos de prueba para que el Dashboard tenga graficos apenas se despliega.
-- Se ejecuta automaticamente gracias a spring.sql.init.mode=always.
-- ON CONFLICT evita duplicados si el servicio se reinicia varias veces.

INSERT INTO kpi_diario (fecha, ventas_totales, retrasos_logisticos, margen_promedio, estado_sistema)
VALUES ('2026-07-06', 4500000, 2, 1545000.00, 'OK')
ON CONFLICT (fecha) DO NOTHING;

INSERT INTO kpi_diario (fecha, ventas_totales, retrasos_logisticos, margen_promedio, estado_sistema)
VALUES ('2026-07-07', 5200000, 1, 1805000.00, 'OK')
ON CONFLICT (fecha) DO NOTHING;

INSERT INTO kpi_diario (fecha, ventas_totales, retrasos_logisticos, margen_promedio, estado_sistema)
VALUES ('2026-07-08', 3800000, 4, 1270000.00, 'OK')
ON CONFLICT (fecha) DO NOTHING;

INSERT INTO kpi_diario (fecha, ventas_totales, retrasos_logisticos, margen_promedio, estado_sistema)
VALUES ('2026-07-09', 6100000, 0, 2135000.00, 'OK')
ON CONFLICT (fecha) DO NOTHING;
