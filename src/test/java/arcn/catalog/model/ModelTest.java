package arcn.catalog.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para las entidades del modelo de dominio.
 */
class ModelTest {
    
    private Restaurante restaurante;
    private Producto producto;
    private BusquedaCriterios criterios;
    
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
                .activo(true)
                .categorias(new ArrayList<>(List.of("Italiana", "Pizza")))
                .menu(new ArrayList<>())
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
        
        criterios = BusquedaCriterios.builder()
                .latitudUsuario(40.7150)
                .longitudUsuario(-74.0030)
                .radioKm(5.0)
                .hora(LocalTime.of(12, 0))
                .categoria("Italiana")
                .build();
    }
    
    @Test
    void testRestauranteValido() {
        // Act & Assert
        assertTrue(restaurante.esValido());
    }
    
    @Test
    void testRestauranteInvalidoSinNombre() {
        // Arrange
        restaurante.setNombre("");
        
        // Act & Assert
        assertFalse(restaurante.esValido());
    }
    
    @Test
    void testRestauranteInvalidoSinDireccion() {
        // Arrange
        restaurante.setDireccion(null);
        
        // Act & Assert
        assertFalse(restaurante.esValido());
    }
    
    @Test
    void testRestauranteInvalidoSinCoordenadas() {
        // Arrange
        restaurante.setLatitud(null);
        
        // Act & Assert
        assertFalse(restaurante.esValido());
    }
    
    @Test
    void testRestauranteInvalidoSinHorarios() {
        // Arrange
        restaurante.setHoraApertura(null);
        
        // Act & Assert
        assertFalse(restaurante.esValido());
    }
    
    @Test
    void testEstaAbiertoEnHora() {
        // Arrange - Restaurante abierto de 11:00 a 23:00
        
        // Act & Assert
        assertTrue(restaurante.estaAbierto(LocalTime.of(12, 0))); // Dentro del horario
        assertTrue(restaurante.estaAbierto(LocalTime.of(11, 0))); // Hora de apertura
        assertTrue(restaurante.estaAbierto(LocalTime.of(23, 0))); // Hora de cierre
        assertFalse(restaurante.estaAbierto(LocalTime.of(10, 59))); // Antes de apertura
        assertFalse(restaurante.estaAbierto(LocalTime.of(23, 1))); // Después de cierre
    }
    
    @Test
    void testCalcularDistancia() {
        // Arrange - Restaurante en (40.7128, -74.0060), Usuario en (40.7150, -74.0030)
        // Distancia esperada aproximadamente 0.8-1.0 km
        
        // Act
        Double distancia = restaurante.calcularDistancia(40.7150, -74.0030);
        
        // Assert
        assertNotNull(distancia);
        assertTrue(distancia > 0);
        assertTrue(distancia < 2.0); // Menos de 2 km
    }
    
    @Test
    void testCalcularDistanciaEnMismoPunto() {
        // Act
        Double distancia = restaurante.calcularDistancia(40.7128, -74.0060);
        
        // Assert
        assertNotNull(distancia);
        assertEquals(0.0, distancia, 0.001); // Debe ser casi 0
    }
    
    @Test
    void testInicializarMenu() {
        // Arrange
        restaurante.setMenu(null);
        
        // Act
        restaurante.inicializarMenu();
        
        // Assert
        assertNotNull(restaurante.getMenu());
        assertTrue(restaurante.getMenu().isEmpty());
    }
    
    @Test
    void testInicializarMenuYaInicializado() {
        // Arrange
        restaurante.inicializarMenu();
        int tamanioInicial = restaurante.getMenu().size();
        
        // Act
        restaurante.inicializarMenu();
        
        // Assert
        assertEquals(tamanioInicial, restaurante.getMenu().size());
    }
    
    @Test
    void testAgregarProductoAlMenu() {
        // Arrange
        restaurante.setMenu(new ArrayList<>());
        
        // Act
        restaurante.agregarProductoAlMenu(producto);
        
        // Assert
        assertEquals(1, restaurante.getMenu().size());
        assertEquals("Pizza Margherita", restaurante.getMenu().get(0).getNombre());
    }
    
    @Test
    void testAgregarProductoInvalidoAlMenu() {
        // Arrange
        restaurante.setMenu(new ArrayList<>());
        Producto inválido = Producto.builder()
                .nombre("")
                .precio(BigDecimal.valueOf(10.0))
                .build();
        
        // Act
        restaurante.agregarProductoAlMenu(inválido);
        
        // Assert
        assertTrue(restaurante.getMenu().isEmpty()); // No se agregó
    }
    
    @Test
    void testAgregarProductoNuloAlMenu() {
        // Arrange
        restaurante.setMenu(new ArrayList<>());
        
        // Act
        restaurante.agregarProductoAlMenu(null);
        
        // Assert
        assertTrue(restaurante.getMenu().isEmpty());
    }
    
    @Test
    void testProductoValido() {
        // Act & Assert
        assertTrue(producto.esValido());
    }
    
    @Test
    void testProductoInvalidoSinNombre() {
        // Arrange
        producto.setNombre("");
        
        // Act & Assert
        assertFalse(producto.esValido());
    }
    
    @Test
    void testProductoInvalidoSinPrecio() {
        // Arrange
        producto.setPrecio(null);
        
        // Act & Assert
        assertFalse(producto.esValido());
    }
    
    @Test
    void testProductoInvalidoPrecioNegativo() {
        // Arrange
        producto.setPrecio(BigDecimal.valueOf(-5.0));
        
        // Act & Assert
        assertFalse(producto.esValido());
    }
    
    @Test
    void testProductoInvalidoPrecioCero() {
        // Arrange
        producto.setPrecio(BigDecimal.ZERO);
        
        // Act & Assert
        assertFalse(producto.esValido());
    }
    
    @Test
    void testBusquedaCriteriosValido() {
        // Act & Assert
        assertTrue(criterios.esValido());
    }
    
    @Test
    void testBusquedaCriteriosInvalidoSinLatitud() {
        // Arrange
        criterios.setLatitudUsuario(null);
        
        // Act & Assert
        assertFalse(criterios.esValido());
    }
    
    @Test
    void testBusquedaCriteriosInvalidoSinLongitud() {
        // Arrange
        criterios.setLongitudUsuario(null);
        
        // Act & Assert
        assertFalse(criterios.esValido());
    }
    
    @Test
    void testBusquedaCriteriosInvalidoRadioNegativo() {
        // Arrange
        criterios.setRadioKm(-1.0);
        
        // Act & Assert
        assertFalse(criterios.esValido());
    }
    
    @Test
    void testBusquedaCriteriosInvalidoRadioCero() {
        // Arrange
        criterios.setRadioKm(0.0);
        
        // Act & Assert
        assertFalse(criterios.esValido());
    }
    
    @Test
    void testBusquedaCriteriosInvalidoSinHora() {
        // Arrange
        criterios.setHora(null);
        
        // Act & Assert
        assertFalse(criterios.esValido());
    }
    
    @Test
    void testRestauranteBuilder() {
        // Act
        Restaurante construido = Restaurante.builder()
                .id("2")
                .nombre("Test")
                .descripcion("Test desc")
                .direccion("Test dir")
                .latitud(10.0)
                .longitud(20.0)
                .telefono("123")
                .email("test@test.com")
                .horaApertura(LocalTime.of(10, 0))
                .horaCierre(LocalTime.of(22, 0))
                .calificacion(3.5)
                .numeroResenas(50)
                .imagen("http://img")
                .categorias(List.of("Test"))
                .menu(new ArrayList<>())
                .activo(true)
                .build();
        
        // Assert
        assertNotNull(construido);
        assertEquals("2", construido.getId());
        assertEquals("Test", construido.getNombre());
        assertTrue(construido.isActivo());
    }
    
    @Test
    void testProductoBuilder() {
        // Act
        Producto construido = Producto.builder()
                .id("p2")
                .nombre("Test Producto")
                .descripcion("Test desc")
                .precio(BigDecimal.valueOf(15.5))
                .disponible(true)
                .categoria("Test")
                .tiempoPreparacion(25)
                .imagen("http://img")
                .build();
        
        // Assert
        assertNotNull(construido);
        assertEquals("p2", construido.getId());
        assertEquals("Test Producto", construido.getNombre());
        assertEquals(BigDecimal.valueOf(15.5), construido.getPrecio());
    }
    
    @Test
    void testBusquedaCriteriosBuilder() {
        // Act
        BusquedaCriterios construido = BusquedaCriterios.builder()
                .latitudUsuario(50.0)
                .longitudUsuario(60.0)
                .radioKm(10.0)
                .hora(LocalTime.of(14, 30))
                .categoria("Test")
                .calificacionMinima(3.5)
                .build();
        
        // Assert
        assertNotNull(construido);
        assertEquals(50.0, construido.getLatitudUsuario());
        assertEquals(60.0, construido.getLongitudUsuario());
        assertEquals(10.0, construido.getRadioKm());
    }
}
