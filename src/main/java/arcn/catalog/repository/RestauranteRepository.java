package arcn.catalog.repository;

import arcn.catalog.model.Restaurante;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Restaurante.
 * Proporciona acceso a datos de MongoDB para operaciones CRUD y consultas personalizadas.
 */
@Repository
public interface RestauranteRepository extends MongoRepository<Restaurante, String> {
    
    /**
     * Busca restaurantes por nombre
     */
    List<Restaurante> findByNombreContainingIgnoreCase(String nombre);
    
    /**
     * Busca restaurantes activos
     */
    List<Restaurante> findByActivo(boolean activo);
    
    /**
     * Busca restaurantes por categoría
     */
    List<Restaurante> findByCategoriasContaining(String categoria);
    
    /**
     * Busca restaurantes activos por categoría
     */
    @Query("{ 'activo': true, 'categorias': ?0 }")
    List<Restaurante> findActivosByCategoria(String categoria);
    
    /**
     * Busca restaurantes con calificación mínima
     */
    @Query("{ 'activo': true, 'calificacion': { $gte: ?0 } }")
    List<Restaurante> findByCalificacionMinima(Double calificacionMinima);
    
    /**
     * Busca restaurantes abiertos a cierta hora
     */
    @Query("{ 'activo': true, 'horaApertura': { $lte: ?0 }, 'horaCierre': { $gte: ?0 } }")
    List<Restaurante> findAbiertosEnHora(LocalTime hora);
    
    /**
     * Busca restaurante por email
     */
    Optional<Restaurante> findByEmail(String email);
    
    /**
     * Busca restaurante por nombre exacto
     */
    Optional<Restaurante> findByNombreIgnoreCase(String nombre);
}
