package arcn.catalog.service;

import arcn.catalog.dto.ProductoDTO;
import arcn.catalog.dto.RestauranteDTO;
import arcn.catalog.model.Producto;
import arcn.catalog.model.Restaurante;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de mapeo entre entidades del dominio y DTOs.
 * Facilita la conversión entre objetos de dominio y objetos de transferencia de datos.
 */
@Service
public class MapperService {
    
    /**
     * Convierte una entidad Restaurante a RestauranteDTO
     */
    public RestauranteDTO convertirADTO(Restaurante restaurante) {
        if (restaurante == null) {
            return null;
        }
        
        return RestauranteDTO.builder()
                .id(restaurante.getId())
                .nombre(restaurante.getNombre())
                .descripcion(restaurante.getDescripcion())
                .direccion(restaurante.getDireccion())
                .latitud(restaurante.getLatitud())
                .longitud(restaurante.getLongitud())
                .telefono(restaurante.getTelefono())
                .email(restaurante.getEmail())
                .horaApertura(restaurante.getHoraApertura())
                .horaCierre(restaurante.getHoraCierre())
                .calificacion(restaurante.getCalificacion())
                .numeroResenas(restaurante.getNumeroResenas())
                .imagen(restaurante.getImagen())
                .categorias(restaurante.getCategorias())
                .menu(convertirMenuADTO(restaurante.getMenu()))
                .activo(restaurante.isActivo())
                .build();
    }
    
    /**
     * Convierte un RestauranteDTO a entidad Restaurante
     */
    public Restaurante convertirAEntidad(RestauranteDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return Restaurante.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .direccion(dto.getDireccion())
                .latitud(dto.getLatitud())
                .longitud(dto.getLongitud())
                .telefono(dto.getTelefono())
                .email(dto.getEmail())
                .horaApertura(dto.getHoraApertura())
                .horaCierre(dto.getHoraCierre())
                .calificacion(dto.getCalificacion())
                .numeroResenas(dto.getNumeroResenas())
                .imagen(dto.getImagen())
                .categorias(dto.getCategorias())
                .menu(convertirMenuAEntidad(dto.getMenu()))
                .activo(dto.isActivo())
                .build();
    }
    
    /**
     * Convierte una entidad Producto a ProductoDTO
     */
    public ProductoDTO convertirADTO(Producto producto) {
        if (producto == null) {
            return null;
        }
        
        return ProductoDTO.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .disponible(producto.isDisponible())
                .categoria(producto.getCategoria())
                .tiempoPreparacion(producto.getTiempoPreparacion())
                .imagen(producto.getImagen())
                .build();
    }
    
    /**
     * Convierte un ProductoDTO a entidad Producto
     */
    public Producto convertirAEntidad(ProductoDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return Producto.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .precio(dto.getPrecio())
                .disponible(dto.isDisponible())
                .categoria(dto.getCategoria())
                .tiempoPreparacion(dto.getTiempoPreparacion())
                .imagen(dto.getImagen())
                .build();
    }
    
    /**
     * Convierte una lista de Productos a lista de ProductoDTOs
     */
    private List<ProductoDTO> convertirMenuADTO(List<Producto> menu) {
        if (menu == null || menu.isEmpty()) {
            return Collections.emptyList();
        }
        return menu.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convierte una lista de ProductoDTOs a lista de Productos
     */
    private List<Producto> convertirMenuAEntidad(List<ProductoDTO> menu) {
        if (menu == null || menu.isEmpty()) {
            return Collections.emptyList();
        }
        return menu.stream()
                .map(this::convertirAEntidad)
                .collect(Collectors.toList());
    }
}
