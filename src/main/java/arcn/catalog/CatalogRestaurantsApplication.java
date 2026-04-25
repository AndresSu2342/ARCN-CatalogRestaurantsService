package arcn.catalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Aplicación principal del microservicio de Catálogo de Restaurantes.
 * Bounded Context: Catálogo de Restaurantes
 */
@SpringBootApplication
@EnableMongoRepositories(basePackages = "arcn.catalog.repository")
public class CatalogRestaurantsApplication {
    public static void main(String[] args) {
        SpringApplication.run(CatalogRestaurantsApplication.class, args);
    }
}
