package arcn.catalog.controller;

import arcn.catalog.dto.BusquedaRequestDTO;
import arcn.catalog.dto.ProductoDTO;
import arcn.catalog.dto.RestauranteDTO;
import arcn.catalog.model.BusquedaCriterios;
import arcn.catalog.model.Producto;
import arcn.catalog.service.MapperService;
import arcn.catalog.service.RestauranteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalTime;
import java.util.List;

/**
 * Controlador REST del Bounded Context Catálogo de Restaurantes.
 * Expone los endpoints de la API para operaciones sobre restaurantes.
 */
@RestController
@RequestMapping("/api/v1/restaurantes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class RestauranteController {
    
    private final RestauranteService restauranteService;
    private final MapperService mapperService;
    
    /**
     * Caso de uso: Buscar restaurantes cercanos
     * GET /api/v1/restaurantes/buscar?lat=...&lon=...&radio=...
     */
    @PostMapping("/buscar")
    public ResponseEntity<List<RestauranteDTO>> buscarRestaurantesCercanos(
            @Valid @RequestBody BusquedaRequestDTO busquedaRequest) {
        log.info("Búsqueda de restaurantes cercanos - Lat: {}, Lon: {}", 
                busquedaRequest.getLatitudUsuario(), busquedaRequest.getLongitudUsuario());
        
        BusquedaCriterios criterios = BusquedaCriterios.builder()
                .latitudUsuario(busquedaRequest.getLatitudUsuario())
                .longitudUsuario(busquedaRequest.getLongitudUsuario())
                .radioKm(busquedaRequest.getRadioKm() != null ? busquedaRequest.getRadioKm() : 5.0)
                .hora(busquedaRequest.getHora() != null ? busquedaRequest.getHora() : LocalTime.now())
                .categoria(busquedaRequest.getCategoria())
                .calificacionMinima(busquedaRequest.getCalificacionMinima())
                .build();
        
        List<RestauranteDTO> restaurantes = restauranteService.buscarRestaurantesCercanos(criterios);
        return ResponseEntity.ok(restaurantes);
    }
    
    /**
     * Obtiene todos los restaurantes activos
     * GET /api/v1/restaurantes
     */
    @GetMapping
    public ResponseEntity<List<RestauranteDTO>> obtenerTodos() {
        log.info("Obteniendo todos los restaurantes");
        List<RestauranteDTO> restaurantes = restauranteService.obtenerTodosLosRestaurantes();
        return ResponseEntity.ok(restaurantes);
    }
    
    /**
     * Obtiene un restaurante por ID
     * GET /api/v1/restaurantes/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<RestauranteDTO> obtenerPorId(@PathVariable String id) {
        log.info("Obteniendo restaurante con ID: {}", id);
        return restauranteService.obtenerRestaurantePorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Crea un nuevo restaurante
     * POST /api/v1/restaurantes
     */
    @PostMapping
    public ResponseEntity<RestauranteDTO> crear(@Valid @RequestBody RestauranteDTO restauranteDTO) {
        log.info("Creando nuevo restaurante: {}", restauranteDTO.getNombre());
        try {
            RestauranteDTO creado = restauranteService.crearRestaurante(restauranteDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (IllegalArgumentException e) {
            log.error("Error al crear restaurante: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Actualiza un restaurante existente
     * PUT /api/v1/restaurantes/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<RestauranteDTO> actualizar(
            @PathVariable String id,
            @Valid @RequestBody RestauranteDTO restauranteDTO) {
        log.info("Actualizando restaurante con ID: {}", id);
        return restauranteService.actualizarRestaurante(id, restauranteDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Desactiva un restaurante
     * DELETE /api/v1/restaurantes/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable String id) {
        log.info("Desactivando restaurante con ID: {}", id);
        boolean desactivado = restauranteService.desactivarRestaurante(id);
        return desactivado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    
    /**
     * Busca restaurantes por nombre
     * GET /api/v1/restaurantes/nombre?q=...
     */
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<List<RestauranteDTO>> buscarPorNombre(@PathVariable String nombre) {
        log.info("Buscando restaurantes por nombre: {}", nombre);
        List<RestauranteDTO> restaurantes = restauranteService.buscarPorNombre(nombre);
        return ResponseEntity.ok(restaurantes);
    }
    
    /**
     * Busca restaurantes por categoría
     * GET /api/v1/restaurantes/categoria?cat=...
     */
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<RestauranteDTO>> buscarPorCategoria(@PathVariable String categoria) {
        log.info("Buscando restaurantes por categoría: {}", categoria);
        List<RestauranteDTO> restaurantes = restauranteService.buscarPorCategoria(categoria);
        return ResponseEntity.ok(restaurantes);
    }
    
    /**
     * Obtiene el menú de un restaurante
     * GET /api/v1/restaurantes/{id}/menu
     */
    @GetMapping("/{id}/menu")
    public ResponseEntity<List<ProductoDTO>> obtenerMenu(@PathVariable String id) {
        log.info("Obteniendo menú del restaurante: {}", id);
        List<Producto> menu = restauranteService.obtenerMenuDelRestaurante(id);
        List<ProductoDTO> menuDTO = menu.stream()
                .map(p -> {
                    ProductoDTO dto = ProductoDTO.builder()
                            .id(p.getId())
                            .nombre(p.getNombre())
                            .descripcion(p.getDescripcion())
                            .precio(p.getPrecio())
                            .disponible(p.isDisponible())
                            .categoria(p.getCategoria())
                            .tiempoPreparacion(p.getTiempoPreparacion())
                            .imagen(p.getImagen())
                            .build();
                    return dto;
                })
                .toList();
        return ResponseEntity.ok(menuDTO);
    }
    
    /**
     * Agrega un producto al menú del restaurante
     * POST /api/v1/restaurantes/{id}/menu
     */
    @PostMapping("/{id}/menu")
    public ResponseEntity<RestauranteDTO> agregarAlMenu(
            @PathVariable String id,
            @Valid @RequestBody ProductoDTO productoDTO) {
        log.info("Agregando producto al menú del restaurante: {}", id);
        
        Producto producto = Producto.builder()
                .nombre(productoDTO.getNombre())
                .descripcion(productoDTO.getDescripcion())
                .precio(productoDTO.getPrecio())
                .disponible(productoDTO.isDisponible())
                .categoria(productoDTO.getCategoria())
                .tiempoPreparacion(productoDTO.getTiempoPreparacion())
                .imagen(productoDTO.getImagen())
                .build();
        
        return restauranteService.agregarProductoAlMenu(id, producto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
