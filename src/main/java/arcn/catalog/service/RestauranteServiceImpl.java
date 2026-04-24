package arcn.catalog.service;

import arcn.catalog.dto.ProductoDTO;
import arcn.catalog.dto.RestauranteDTO;
import arcn.catalog.model.BusquedaCriterios;
import arcn.catalog.model.Producto;
import arcn.catalog.model.Restaurante;
import arcn.catalog.repository.RestauranteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del Servicio de Dominio para Restaurantes.
 * Coordina la lógica de negocio del Bounded Context Catálogo de Restaurantes.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RestauranteServiceImpl implements RestauranteService {
    
    private final RestauranteRepository restauranteRepository;
    private final MapperService mapperService;
    
    @Override
    public List<RestauranteDTO> buscarRestaurantesCercanos(BusquedaCriterios criterios) {
        if (!criterios.esValido()) {
            log.warn("Criterios de búsqueda inválidos: {}", criterios);
            return List.of();
        }
        
        log.info("Buscando restaurantes cercanos - Lat: {}, Lon: {}, Radio: {} km", 
                criterios.getLatitudUsuario(), criterios.getLongitudUsuario(), criterios.getRadioKm());
        
        // Obtener restaurantes abiertos si se especifica hora
        List<Restaurante> restaurantesAbiertos;
        if (criterios.getHora() != null) {
            restaurantesAbiertos = restauranteRepository.findAbiertosEnHora(criterios.getHora());
        } else {
            restaurantesAbiertos = restauranteRepository.findByActivo(true);
        }
        
        // Filtrar por categoría si se especifica
        if (criterios.getCategoria() != null && !criterios.getCategoria().isBlank()) {
            restaurantesAbiertos = restaurantesAbiertos.stream()
                    .filter(r -> r.getCategorias() != null && r.getCategorias().contains(criterios.getCategoria()))
                    .collect(Collectors.toList());
        }
        
        // Filtrar por calificación mínima
        if (criterios.getCalificacionMinima() != null && criterios.getCalificacionMinima() > 0) {
            restaurantesAbiertos = restaurantesAbiertos.stream()
                    .filter(r -> r.getCalificacion() != null && r.getCalificacion() >= criterios.getCalificacionMinima())
                    .collect(Collectors.toList());
        }
        
        // Calcular distancia y filtrar por radio
        return restaurantesAbiertos.stream()
                .map(r -> {
                    RestauranteDTO dto = mapperService.convertirADTO(r);
                    Double distancia = r.calcularDistancia(criterios.getLatitudUsuario(), criterios.getLongitudUsuario());
                    dto.setDistanciaKm(distancia);
                    return dto;
                })
                .filter(dto -> dto.getDistanciaKm() <= criterios.getRadioKm())
                .sorted((r1, r2) -> r1.getDistanciaKm().compareTo(r2.getDistanciaKm()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<RestauranteDTO> obtenerTodosLosRestaurantes() {
        return restauranteRepository.findByActivo(true).stream()
                .map(mapperService::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<RestauranteDTO> obtenerRestaurantePorId(String id) {
        return restauranteRepository.findById(id)
                .filter(Restaurante::isActivo)
                .map(mapperService::convertirADTO);
    }
    
    @Override
    public RestauranteDTO crearRestaurante(RestauranteDTO restauranteDTO) {
        log.info("Creando nuevo restaurante: {}", restauranteDTO.getNombre());
        
        Restaurante restaurante = mapperService.convertirAEntidad(restauranteDTO);
        restaurante.setActivo(true);
        restaurante.inicializarMenu();
        
        if (!restaurante.esValido()) {
            throw new IllegalArgumentException("Datos del restaurante inválidos");
        }
        
        Restaurante guardado = restauranteRepository.save(restaurante);
        log.info("Restaurante creado con ID: {}", guardado.getId());
        
        return mapperService.convertirADTO(guardado);
    }
    
    @Override
    public Optional<RestauranteDTO> actualizarRestaurante(String id, RestauranteDTO restauranteDTO) {
        return restauranteRepository.findById(id)
                .map(restaurante -> {
                    log.info("Actualizando restaurante: {}", id);
                    
                    restaurante.setNombre(restauranteDTO.getNombre());
                    restaurante.setDescripcion(restauranteDTO.getDescripcion());
                    restaurante.setDireccion(restauranteDTO.getDireccion());
                    restaurante.setLatitud(restauranteDTO.getLatitud());
                    restaurante.setLongitud(restauranteDTO.getLongitud());
                    restaurante.setTelefono(restauranteDTO.getTelefono());
                    restaurante.setEmail(restauranteDTO.getEmail());
                    restaurante.setHoraApertura(restauranteDTO.getHoraApertura());
                    restaurante.setHoraCierre(restauranteDTO.getHoraCierre());
                    restaurante.setCalificacion(restauranteDTO.getCalificacion());
                    restaurante.setNumeroResenas(restauranteDTO.getNumeroResenas());
                    restaurante.setImagen(restauranteDTO.getImagen());
                    restaurante.setCategorias(restauranteDTO.getCategorias());
                    
                    Restaurante actualizado = restauranteRepository.save(restaurante);
                    return mapperService.convertirADTO(actualizado);
                });
    }
    
    @Override
    public boolean desactivarRestaurante(String id) {
        return restauranteRepository.findById(id)
                .map(restaurante -> {
                    log.info("Desactivando restaurante: {}", id);
                    restaurante.setActivo(false);
                    restauranteRepository.save(restaurante);
                    return true;
                })
                .orElse(false);
    }
    
    @Override
    public List<RestauranteDTO> buscarPorNombre(String nombre) {
        return restauranteRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .filter(Restaurante::isActivo)
                .map(mapperService::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<RestauranteDTO> buscarPorCategoria(String categoria) {
        return restauranteRepository.findActivosByCategoria(categoria).stream()
                .map(mapperService::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Producto> obtenerMenuDelRestaurante(String restauranteId) {
        return restauranteRepository.findById(restauranteId)
                .map(restaurante -> {
                    restaurante.inicializarMenu();
                    return restaurante.getMenu();
                })
                .orElse(List.of());
    }
    
    @Override
    public Optional<RestauranteDTO> agregarProductoAlMenu(String restauranteId, Producto producto) {
        return restauranteRepository.findById(restauranteId)
                .map(restaurante -> {
                    log.info("Agregando producto {} al menú del restaurante {}", producto.getNombre(), restauranteId);
                    restaurante.agregarProductoAlMenu(producto);
                    Restaurante actualizado = restauranteRepository.save(restaurante);
                    return mapperService.convertirADTO(actualizado);
                });
    }
}
