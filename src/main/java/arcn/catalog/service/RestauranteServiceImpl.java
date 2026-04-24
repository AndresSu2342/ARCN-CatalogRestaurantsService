package arcn.catalog.service;

import arcn.catalog.dto.RestauranteDTO;
import arcn.catalog.exception.RestauranteNoEncontradoException;
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
            throw new IllegalArgumentException("Los criterios de búsqueda son inválidos: latitud, longitud, radio y hora son requeridos");
        }

        log.info("Buscando restaurantes cercanos - Lat: {}, Lon: {}, Radio: {} km",
                criterios.getLatitudUsuario(), criterios.getLongitudUsuario(), criterios.getRadioKm());

        // Obtener restaurantes activos y filtrar horario en Java (MongoDB no compara LocalTime nativamente)
        List<Restaurante> candidatos = restauranteRepository.findByActivo(true);

        // Filtrar por horario de apertura en memoria
        if (criterios.getHora() != null) {
            candidatos = candidatos.stream()
                    .filter(r -> r.estaAbierto(criterios.getHora()))
                    .collect(Collectors.toList());
        }

        // Filtrar por categoría si se especifica
        if (criterios.getCategoria() != null && !criterios.getCategoria().isBlank()) {
            candidatos = candidatos.stream()
                    .filter(r -> r.getCategorias() != null && r.getCategorias().contains(criterios.getCategoria()))
                    .collect(Collectors.toList());
        }

        // Filtrar por calificación mínima
        if (criterios.getCalificacionMinima() != null && criterios.getCalificacionMinima() > 0) {
            candidatos = candidatos.stream()
                    .filter(r -> r.getCalificacion() != null && r.getCalificacion() >= criterios.getCalificacionMinima())
                    .collect(Collectors.toList());
        }

        // Calcular distancia y filtrar por radio
        List<RestauranteDTO> resultado = candidatos.stream()
                .map(r -> {
                    RestauranteDTO dto = mapperService.convertirADTO(r);
                    Double distancia = r.calcularDistancia(criterios.getLatitudUsuario(), criterios.getLongitudUsuario());
                    dto.setDistanciaKm(distancia);
                    return dto;
                })
                .filter(dto -> dto.getDistanciaKm() <= criterios.getRadioKm())
                .sorted((r1, r2) -> r1.getDistanciaKm().compareTo(r2.getDistanciaKm()))
                .collect(Collectors.toList());

        // Problema del Event Storming: "Sin cobertura — ¿qué mostrar al user?"
        if (resultado.isEmpty()) {
            throw new RestauranteNoEncontradoException(
                    String.format("Sin cobertura: no se encontraron restaurantes en un radio de %.1f km " +
                            "para la ubicación [%.4f, %.4f]",
                            criterios.getRadioKm(),
                            criterios.getLatitudUsuario(),
                            criterios.getLongitudUsuario())
            );
        }

        log.info("Se encontraron {} restaurantes cercanos", resultado.size());
        return resultado;
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
                .orElseThrow(() -> new RestauranteNoEncontradoException(
                        "Restaurante no encontrado con ID: " + restauranteId
                ));
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