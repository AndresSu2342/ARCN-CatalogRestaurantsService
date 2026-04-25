package arcn.catalog.service;

import arcn.catalog.dto.ProductoDTO;
import arcn.catalog.dto.RestauranteDTO;
import arcn.catalog.model.Producto;
import arcn.catalog.model.Restaurante;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para MapperService.
 */
@ExtendWith(MockitoExtension.class)
class MapperServiceTest {
    
    @InjectMocks
    private MapperService mapperService;
    
    private Restaurante restaurante;
    private RestauranteDTO restauranteDTO;
    private Producto producto;
    private ProductoDTO productoDTO;
    
    @BeforeEach
    void setUp() {
        restaurante = Restaurante.builder()
                .id("1")
                .nombre("Pizzería Roma")
                .descripcion("Auténtica pizza italiana")
                .direccion("Via Roma 123")
                .latitud(40.7128)
                .longitud(-74.0060)
                .telefono("555-0101")
                .email("info@pizzeriaroma.com")
                .horaApertura(LocalTime.of(11, 0))
                .horaCierre(LocalTime.of(23, 0))
                .calificacion(4.5)
                .numeroResenas(100)
                .imagen("http://example.com/pizza.jpg")
                .categorias(List.of("Italiana", "Pizza"))
                .activo(true)
                .menu(Collections.emptyList())
                .build();
        
        restauranteDTO = RestauranteDTO.builder()
                .id("1")
                .nombre("Pizzería Roma")
                .descripcion("Auténtica pizza italiana")
                .direccion("Via Roma 123")
                .latitud(40.7128)
                .longitud(-74.0060)
                .telefono("555-0101")
                .email("info@pizzeriaroma.com")
                .horaApertura(LocalTime.of(11, 0))
                .horaCierre(LocalTime.of(23, 0))
                .calificacion(4.5)
                .numeroResenas(100)
                .imagen("http://example.com/pizza.jpg")
                .categorias(List.of("Italiana", "Pizza"))
                .activo(true)
                .menu(Collections.emptyList())
                .build();
        
        producto = Producto.builder()
                .id("p1")
                .nombre("Pizza Margherita")
                .descripcion("Pizza clásica")
                .precio(BigDecimal.valueOf(10.99))
                .disponible(true)
                .categoria("Pizza")
                .tiempoPreparacion(20)
                .imagen("http://example.com/margherita.jpg")
                .build();
        
        productoDTO = ProductoDTO.builder()
                .id("p1")
                .nombre("Pizza Margherita")
                .descripcion("Pizza clásica")
                .precio(BigDecimal.valueOf(10.99))
                .disponible(true)
                .categoria("Pizza")
                .tiempoPreparacion(20)
                .imagen("http://example.com/margherita.jpg")
                .build();
    }
    
    @Test
    void testConvertirRestauranteADTO() {
        // Act
        RestauranteDTO resultado = mapperService.convertirADTO(restaurante);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(restaurante.getId(), resultado.getId());
        assertEquals(restaurante.getNombre(), resultado.getNombre());
        assertEquals(restaurante.getDescripcion(), resultado.getDescripcion());
        assertEquals(restaurante.getDireccion(), resultado.getDireccion());
        assertEquals(restaurante.getLatitud(), resultado.getLatitud());
        assertEquals(restaurante.getLongitud(), resultado.getLongitud());
        assertEquals(restaurante.getTelefono(), resultado.getTelefono());
        assertEquals(restaurante.getEmail(), resultado.getEmail());
        assertEquals(restaurante.getHoraApertura(), resultado.getHoraApertura());
        assertEquals(restaurante.getHoraCierre(), resultado.getHoraCierre());
        assertEquals(restaurante.getCalificacion(), resultado.getCalificacion());
        assertEquals(restaurante.getNumeroResenas(), resultado.getNumeroResenas());
        assertEquals(restaurante.isActivo(), resultado.isActivo());
    }
    
    @Test
    void testConvertirRestauranteDTOAEntidad() {
        // Act
        Restaurante resultado = mapperService.convertirAEntidad(restauranteDTO);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(restauranteDTO.getId(), resultado.getId());
        assertEquals(restauranteDTO.getNombre(), resultado.getNombre());
        assertEquals(restauranteDTO.getDescripcion(), resultado.getDescripcion());
        assertEquals(restauranteDTO.getDireccion(), resultado.getDireccion());
        assertEquals(restauranteDTO.getLatitud(), resultado.getLatitud());
        assertEquals(restauranteDTO.getLongitud(), resultado.getLongitud());
        assertEquals(restauranteDTO.getTelefono(), resultado.getTelefono());
        assertEquals(restauranteDTO.getEmail(), resultado.getEmail());
    }
    
    @Test
    void testConvertirProductoADTO() {
        // Act
        ProductoDTO resultado = mapperService.convertirADTO(producto);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(producto.getId(), resultado.getId());
        assertEquals(producto.getNombre(), resultado.getNombre());
        assertEquals(producto.getDescripcion(), resultado.getDescripcion());
        assertEquals(producto.getPrecio(), resultado.getPrecio());
        assertEquals(producto.isDisponible(), resultado.isDisponible());
        assertEquals(producto.getCategoria(), resultado.getCategoria());
        assertEquals(producto.getTiempoPreparacion(), resultado.getTiempoPreparacion());
    }
    
    @Test
    void testConvertirProductoDTOAEntidad() {
        // Act
        Producto resultado = mapperService.convertirAEntidad(productoDTO);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(productoDTO.getId(), resultado.getId());
        assertEquals(productoDTO.getNombre(), resultado.getNombre());
        assertEquals(productoDTO.getDescripcion(), resultado.getDescripcion());
        assertEquals(productoDTO.getPrecio(), resultado.getPrecio());
        assertEquals(productoDTO.isDisponible(), resultado.isDisponible());
        assertEquals(productoDTO.getCategoria(), resultado.getCategoria());
    }
    
    @Test
    void testConvertirRestauranteADTONulo() {
        // Act
        RestauranteDTO resultado = mapperService.convertirADTO((Restaurante) null);
        
        // Assert
        assertNull(resultado);
    }
    
    @Test
    void testConvertirProductoADTONulo() {
        // Act
        ProductoDTO resultado = mapperService.convertirADTO((Producto) null);
        
        // Assert
        assertNull(resultado);
    }
    
    @Test
    void testConvertirRestauranteDTOAEntidadNulo() {
        // Act
        Restaurante resultado = mapperService.convertirAEntidad((RestauranteDTO) null);
        
        // Assert
        assertNull(resultado);
    }
    
    @Test
    void testConvertirProductoDTOAEntidadNulo() {
        // Act
        Producto resultado = mapperService.convertirAEntidad((ProductoDTO) null);
        
        // Assert
        assertNull(resultado);
    }
    
    @Test
    void testConvertirRestauranteConMenuVacio() {
        // Arrange
        restaurante.setMenu(Collections.emptyList());
        
        // Act
        RestauranteDTO resultado = mapperService.convertirADTO(restaurante);
        
        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getMenu());
        assertTrue(resultado.getMenu().isEmpty());
    }
    
    @Test
    void testConvertirRestauranteConMenuConProductos() {
        // Arrange
        restaurante.setMenu(List.of(producto));
        
        // Act
        RestauranteDTO resultado = mapperService.convertirADTO(restaurante);
        
        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getMenu());
        assertEquals(1, resultado.getMenu().size());
        assertEquals("Pizza Margherita", resultado.getMenu().get(0).getNombre());
    }
    
    @Test
    void testConvertirRestauranteConMenuNulo() {
        // Arrange
        restaurante.setMenu(null);
        
        // Act
        RestauranteDTO resultado = mapperService.convertirADTO(restaurante);
        
        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getMenu());
        assertTrue(resultado.getMenu().isEmpty());
    }
    
    @Test
    void testConvertirProductoNull() {
        // Act & Assert
        assertNull(mapperService.convertirADTO((Producto) null));
        assertNull(mapperService.convertirAEntidad((ProductoDTO) null));
    }

    @Test
    void testConvertirMenuAEntidadWithEmptyList() {
        // Arrange
        restauranteDTO.setMenu(Collections.emptyList());
        
        // Act
        Restaurante resultado = mapperService.convertirAEntidad(restauranteDTO);
        
        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getMenu());
        assertTrue(resultado.getMenu().isEmpty());
    }

    @Test
    void testConvertirMenuAEntidadWithNullList() {
        // Arrange
        restauranteDTO.setMenu(null);
        
        // Act
        Restaurante resultado = mapperService.convertirAEntidad(restauranteDTO);
        
        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getMenu());
        assertTrue(resultado.getMenu().isEmpty());
    }

    @Test
    void testConvertirMenuAEntidadWithValidElements() {
        // Arrange
        restauranteDTO.setMenu(List.of(productoDTO));
        
        // Act
        Restaurante resultado = mapperService.convertirAEntidad(restauranteDTO);
        
        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getMenu());
        assertEquals(1, resultado.getMenu().size());
        assertEquals(productoDTO.getNombre(), resultado.getMenu().get(0).getNombre());
    }
}
