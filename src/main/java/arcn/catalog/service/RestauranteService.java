package arcn.catalog.service;

import arcn.catalog.dto.RestauranteDTO;
import arcn.catalog.model.BusquedaCriterios;
import arcn.catalog.model.Restaurante;
import arcn.catalog.model.Producto;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz del Servicio de Dominio para Restaurantes.
 * Define los casos de uso del Bounded Context Catálogo de Restaurantes.
 */
public interface RestauranteService {
    
    /**
     * Caso de uso: Buscar restaurantes cercanos
     * Filtra restaurantes por ubicación, horario y categoría
     */
    List<RestauranteDTO> buscarRestaurantesCercanos(BusquedaCriterios criterios);
    
    /**
     * Obtiene todos los restaurantes activos
     */
    List<RestauranteDTO> obtenerTodosLosRestaurantes();
    
    /**
     * Obtiene un restaurante por su ID
     */
    Optional<RestauranteDTO> obtenerRestaurantePorId(String id);
    
    /**
     * Crea un nuevo restaurante
     */
    RestauranteDTO crearRestaurante(RestauranteDTO restauranteDTO);
    
    /**
     * Actualiza un restaurante existente
     */
    Optional<RestauranteDTO> actualizarRestaurante(String id, RestauranteDTO restauranteDTO);
    
    /**
     * Elimina un restaurante (borrado lógico)
     */
    boolean desactivarRestaurante(String id);
    
    /**
     * Busca restaurantes por nombre
     */
    List<RestauranteDTO> buscarPorNombre(String nombre);
    
    /**
     * Busca restaurantes por categoría
     */
    List<RestauranteDTO> buscarPorCategoria(String categoria);
    
    /**
     * Obtiene el menú de un restaurante
     */
    List<Producto> obtenerMenuDelRestaurante(String restauranteId);
    
    /**
     * Agrega un producto al menú del restaurante
     */
    Optional<RestauranteDTO> agregarProductoAlMenu(String restauranteId, Producto producto);
}
