package arcn.catalog.service;

import arcn.catalog.dto.RestauranteDTO;
import arcn.catalog.model.BusquedaCriterios;
import arcn.catalog.model.Producto;
import arcn.catalog.model.Restaurante;
import arcn.catalog.repository.RestauranteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para el servicio de Restaurantes.
 */
@ExtendWith(MockitoExtension.class)
class RestauranteServiceImplTest {
    
    @Mock
    private RestauranteRepository restauranteRepository;
    
    @Mock
    private MapperService mapperService;
    
    @InjectMocks
    private RestauranteServiceImpl restauranteService;
    
    private Restaurante restaurante;
    private RestauranteDTO restauranteDTO;
    private Producto producto;
    
    @BeforeEach
    void setUp() {
        // Inicializar datos de prueba
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
                .activo(true)
                .categorias(List.of("Italiana", "Pizza"))
                .menu(List.of())
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
                .activo(true)
                .categorias(List.of("Italiana", "Pizza"))
                .build();
        
        producto = Producto.builder()
                .id("p1")
                .nombre("Pizza Margherita")
                .descripcion("Pizza clásica")
                .precio(BigDecimal.valueOf(10.99))
                .disponible(true)
                .categoria("Pizza")
                .tiempoPreparacion(20)
                .build();
    }
    
    @Test
    void testBuscarRestaurantesCercanos() {
        // Arrange
        BusquedaCriterios criterios = BusquedaCriterios.builder()
                .latitudUsuario(40.7180)
                .longitudUsuario(-74.0020)
                .radioKm(5.0)
                .hora(LocalTime.of(12, 0))
                .categoria("Italiana")
                .build();
        
        when(restauranteRepository.findAbiertosEnHora(any(LocalTime.class)))
                .thenReturn(List.of(restaurante));
        when(mapperService.convertirADTO(restaurante))
                .thenReturn(restauranteDTO);
        
        // Act
        List<RestauranteDTO> resultado = restauranteService.buscarRestaurantesCercanos(criterios);
        
        // Assert
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(restauranteRepository, times(1)).findAbiertosEnHora(criterios.getHora());
    }
    
    @Test
    void testCrearRestaurante() {
        // Arrange
        when(mapperService.convertirAEntidad(restauranteDTO)).thenReturn(restaurante);
        when(restauranteRepository.save(any(Restaurante.class))).thenReturn(restaurante);
        when(mapperService.convertirADTO(restaurante)).thenReturn(restauranteDTO);
        
        // Act
        RestauranteDTO resultado = restauranteService.crearRestaurante(restauranteDTO);
        
        // Assert
        assertNotNull(resultado);
        assertEquals("Pizzería Roma", resultado.getNombre());
        verify(restauranteRepository, times(1)).save(any(Restaurante.class));
    }
    
    @Test
    void testObtenerRestaurantePorId() {
        // Arrange
        when(restauranteRepository.findById("1")).thenReturn(Optional.of(restaurante));
        when(mapperService.convertirADTO(restaurante)).thenReturn(restauranteDTO);
        
        // Act
        Optional<RestauranteDTO> resultado = restauranteService.obtenerRestaurantePorId("1");
        
        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Pizzería Roma", resultado.get().getNombre());
        verify(restauranteRepository, times(1)).findById("1");
    }
    
    @Test
    void testDesactivarRestaurante() {
        // Arrange
        when(restauranteRepository.findById("1")).thenReturn(Optional.of(restaurante));
        when(restauranteRepository.save(any(Restaurante.class))).thenReturn(restaurante);
        
        // Act
        boolean resultado = restauranteService.desactivarRestaurante("1");
        
        // Assert
        assertTrue(resultado);
        verify(restauranteRepository, times(1)).findById("1");
        verify(restauranteRepository, times(1)).save(any(Restaurante.class));
    }
    
    @Test
    void testAgregarProductoAlMenu() {
        // Arrange
        restaurante.inicializarMenu();
        when(restauranteRepository.findById("1")).thenReturn(Optional.of(restaurante));
        when(restauranteRepository.save(any(Restaurante.class))).thenReturn(restaurante);
        when(mapperService.convertirADTO(restaurante)).thenReturn(restauranteDTO);
        
        // Act
        Optional<RestauranteDTO> resultado = restauranteService.agregarProductoAlMenu("1", producto);
        
        // Assert
        assertTrue(resultado.isPresent());
        verify(restauranteRepository, times(1)).findById("1");
    }
    
    @Test
    void testCalcularDistancia() {
        // Arrange - Restaurante en (40.7128, -74.0060), Usuario en (40.7180, -74.0020)
        // Distancia esperada aproximadamente 0.8 km
        
        // Act
        Double distancia = restaurante.calcularDistancia(40.7180, -74.0020);
        
        // Assert
        assertNotNull(distancia);
        assertTrue(distancia > 0);
        assertTrue(distancia < 1.5); // Menos de 1.5 km
    }
    
    @Test
    void testValidarRestaurante() {
        // Act & Assert
        assertTrue(restaurante.esValido());
        
        // Test con restaurante inválido
        Restaurante inválido = Restaurante.builder()
                .nombre("")
                .build();
        assertFalse(inválido.esValido());
    }
}
