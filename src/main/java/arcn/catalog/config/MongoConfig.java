package arcn.catalog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Configuración de MongoDB para el Bounded Context Catálogo de Restaurantes.
 * Define la configuración de repositorios y conexión a la base de datos.
 */
@Configuration
@EnableMongoRepositories(basePackages = "arcn.catalog.repository")
public class MongoConfig {
    
    // La configuración de conexión a MongoDB se realiza a través de 
    // propiedades en application.properties
    // Spring Boot Auto-configures MongoTemplate y MongoClient
    
}
