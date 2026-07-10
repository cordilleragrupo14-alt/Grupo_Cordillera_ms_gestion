package cl.grupocordillera.gestion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackage = "cl.grupocordillera.gestion.client")
public class GestionApplication {
    public static void main(String[] args) {
        SpringApplication.run(GestionApplication.class, args);
    }
}
