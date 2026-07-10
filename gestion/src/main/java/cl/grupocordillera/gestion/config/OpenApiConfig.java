package cl.grupocordillera.gestion.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gestionOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("ms-gestion-kpi API")
                .description("Microservicio de gestion/analitica de Grupo Cordillera. " +
                        "Consolida datos de ventas y logistica en KPIs gerenciales.")
                .version("1.0.0"));
    }
}
