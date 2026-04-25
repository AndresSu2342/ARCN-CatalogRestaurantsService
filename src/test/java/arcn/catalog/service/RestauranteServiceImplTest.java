package arcn.catalog.service;

import arcn.catalog.dto.RestauranteDTO;
import arcn.catalog.exception.RestauranteNoEncontradoException;
import arcn.catalog.model.BusquedaCriterios;
import arcn.catalog.model.Producto;
import arcn.catalog.model.Restaurante;
import arcn.catalog.repository.RestauranteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del Servicio de Restaurantes")
class RestauranteServiceImplTest {

    @Mock
    private RestauranteRepository restauranteRepository;

    @Mock
    private MapperService mapperService;

    @InjectMocks
    private RestauranteServiceImpl restauranteService;

    private Restaurante restaurante;
    private Restaurante restaurante2;
    private RestauranteDTO restauranteDTO;
    private Producto producto;
    private BusquedaCriterios criteriosCercanos;

    @BeforeEach
    void setUp() {
        restaurante = Restaurante.builder()
                .id("1")
                .nombre("Pizzería Roma")
                .direccion("Via Roma 123")
                .latitud(40.7128)
                .longitud(-74.0060)
                .horaApertura(LocalTime.of(11, 0))
                .horaCierre(LocalTime.of(23, 0))
                .calificacion(4.5)
                .activo(true)
                .categorias(new ArrayList<>(List.of("Italiana")))
                .menu(new ArrayList<>())
                .build();

        restaurante2 = Restaurante.builder()
                .id("2")
                .nombre("Sushi Paradise")
                .direccion("Boulevard 456")
                .latitud(40.7200)
                .longitud(-74.0100)
                .horaApertura(LocalTime.of(12, 0))
                .horaCierre(LocalTime.of(22, 0))
                .calificacion(4.8)
                .activo(true)
                .categorias(new ArrayList<>(List.of("Japonesa")))
                .menu(new ArrayList<>())
                .build();

        restauranteDTO = RestauranteDTO.builder()
                .id("1")
                .nombre("Pizzería Roma")
                .direccion("Via Roma 123")
                .latitud(40.7128)
                .longitud(-74.0060)
                .horaApertura(LocalTime.of(11, 0))
                .horaCierre(LocalTime.of(23, 0))
                .calificacion(4.5)
                .categorias(List.of("Italiana"))
                .build();

        producto = Producto.builder()
                .id("p1")
                .nombre("Pizza")
                .precio(BigDecimal.TEN)
                .disponible(true)
                .build();

        criteriosCercanos = BusquedaCriterios.builder()
                .latitudUsuario(40.7180)
                .longitudUsuario(-74.0020)
                .radioKm(5.0)
                .hora(LocalTime.of(12, 0))
                .categoria("Italiana")
                .build();
    }

    // ─────────────────────────────────────────────

    @Test
    void testBuscarRestaurantesCercanos_conResultados() {
        when(restauranteRepository.findByActivo(true))
                .thenReturn(List.of(restaurante));

        when(mapperService.convertirADTO(restaurante))
                .thenReturn(restauranteDTO);

        List<RestauranteDTO> resultado = restauranteService.buscarRestaurantesCercanos(criteriosCercanos);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());

        verify(restauranteRepository).findByActivo(true);
    }

    @Test
    void testBuscarRestaurantesCercanos_criteriosInvalidos() {
        BusquedaCriterios criterios = BusquedaCriterios.builder()
                .latitudUsuario(1.0)
                .longitudUsuario(1.0)
                .radioKm(-1.0)
                .build();

        assertThrows(IllegalArgumentException.class,
                () -> restauranteService.buscarRestaurantesCercanos(criterios));
    }

    @Test
    void testBuscarRestaurantesCercanos_conFiltroCategoria() {
        when(restauranteRepository.findByActivo(true))
                .thenReturn(List.of(restaurante, restaurante2));

        when(mapperService.convertirADTO(restaurante))
                .thenReturn(restauranteDTO);

        List<RestauranteDTO> resultado = restauranteService.buscarRestaurantesCercanos(criteriosCercanos);

        assertTrue(resultado.stream()
                .allMatch(r -> r.getCategorias().contains("Italiana")));
    }

    @Test
    void testBuscarRestaurantesCercanos_filtroCalificacion() {

        BusquedaCriterios criterios = BusquedaCriterios.builder()
                .latitudUsuario(40.7180)
                .longitudUsuario(-74.0020)
                .radioKm(5.0)
                .hora(LocalTime.of(12, 0))
                .calificacionMinima(4.7)
                .build();

        when(restauranteRepository.findByActivo(true))
                .thenReturn(List.of(restaurante, restaurante2));

        when(mapperService.convertirADTO(restaurante2))
                .thenReturn(RestauranteDTO.builder().id("2").calificacion(4.8).build());

        List<RestauranteDTO> resultado = restauranteService.buscarRestaurantesCercanos(criterios);

        assertFalse(resultado.isEmpty());
        assertTrue(resultado.stream().allMatch(r -> r.getCalificacion() >= 4.7));
    }

    @Test
    @DisplayName("Debe ordenar resultados por distancia ascendente")
    void testBuscarRestaurantesCercanos_ordenPorDistancia() {

        when(restauranteRepository.findByActivo(true))
                .thenReturn(List.of(restaurante, restaurante2));

        when(mapperService.convertirADTO(any(Restaurante.class)))
                .thenAnswer(invocation -> {
                    Restaurante r = invocation.getArgument(0);
                    return RestauranteDTO.builder()
                            .id(r.getId())
                            .nombre(r.getNombre())
                            .build();
                });

        List<RestauranteDTO> resultado = restauranteService.buscarRestaurantesCercanos(criteriosCercanos);

        if (resultado.size() > 1) {
            assertTrue(resultado.get(0).getDistanciaKm() <= resultado.get(1).getDistanciaKm());
        }
    }

    // ───────────────── CRUD ─────────────────

    @Test
    void testCrearRestaurante() {
        when(mapperService.convertirAEntidad(restauranteDTO)).thenReturn(restaurante);
        when(restauranteRepository.save(any())).thenReturn(restaurante);
        when(mapperService.convertirADTO(restaurante)).thenReturn(restauranteDTO);

        RestauranteDTO resultado = restauranteService.crearRestaurante(restauranteDTO);

        assertNotNull(resultado);
        verify(restauranteRepository).save(any());
    }

    @Test
    void testCrearRestaurante_datosInvalidos() {
        RestauranteDTO invalido = RestauranteDTO.builder().nombre("").build();

        when(mapperService.convertirAEntidad(invalido))
                .thenReturn(Restaurante.builder().nombre("").build());

        assertThrows(IllegalArgumentException.class,
                () -> restauranteService.crearRestaurante(invalido));
    }

    @Test
    void testObtenerTodos() {
        when(restauranteRepository.findByActivo(true))
                .thenReturn(List.of(restaurante));

        when(mapperService.convertirADTO(restaurante))
                .thenReturn(restauranteDTO);

        List<RestauranteDTO> resultado = restauranteService.obtenerTodosLosRestaurantes();

        assertEquals(1, resultado.size());
    }

    @Test
    void testObtenerRestaurantePorId() {
        when(restauranteRepository.findById("1"))
                .thenReturn(Optional.of(restaurante));

        when(mapperService.convertirADTO(restaurante))
                .thenReturn(restauranteDTO);

        Optional<RestauranteDTO> resultado = restauranteService.obtenerRestaurantePorId("1");

        assertTrue(resultado.isPresent());
    }

    @Test
    void testDesactivarRestaurante() {
        when(restauranteRepository.findById("1"))
                .thenReturn(Optional.of(restaurante));

        boolean res = restauranteService.desactivarRestaurante("1");

        assertTrue(res);
    }

    @Test
    void testBuscarPorNombre() {
        when(restauranteRepository.findByNombreContainingIgnoreCase("Roma"))
                .thenReturn(List.of(restaurante));

        when(mapperService.convertirADTO(restaurante))
                .thenReturn(restauranteDTO);

        List<RestauranteDTO> resultado = restauranteService.buscarPorNombre("Roma");

        assertEquals(1, resultado.size());
    }

    @Test
    void testBuscarPorCategoria() {
        when(restauranteRepository.findActivosByCategoria("Italiana"))
                .thenReturn(List.of(restaurante));

        when(mapperService.convertirADTO(restaurante))
                .thenReturn(restauranteDTO);

        List<RestauranteDTO> resultado = restauranteService.buscarPorCategoria("Italiana");

        assertEquals(1, resultado.size());
    }

    @Test
    void testObtenerMenu() {
        restaurante.setMenu(List.of(producto));

        when(restauranteRepository.findById("1"))
                .thenReturn(Optional.of(restaurante));

        List<Producto> resultado = restauranteService.obtenerMenuDelRestaurante("1");

        assertEquals(1, resultado.size());
    }

    @Test
    void testAgregarProducto() {
        when(restauranteRepository.findById("1"))
                .thenReturn(Optional.of(restaurante));

        when(restauranteRepository.save(any()))
                .thenReturn(restaurante);

        when(mapperService.convertirADTO(restaurante))
                .thenReturn(restauranteDTO);

        Optional<RestauranteDTO> resultado = restauranteService.agregarProductoAlMenu("1", producto);

        assertTrue(resultado.isPresent());
    }

    @Test
    void testBuscarRestaurantesCercanos_sinResultados_lanzaExcepcion() {

        // Mock: NO hay restaurantes activos
        when(restauranteRepository.findByActivo(true))
                .thenReturn(List.of());

        BusquedaCriterios criterios = BusquedaCriterios.builder()
                .latitudUsuario(40.7180)
                .longitudUsuario(-74.0020)
                .radioKm(5.0)
                .hora(LocalTime.of(12, 0)) // importante para pasar validación
                .build();

        assertThrows(RestauranteNoEncontradoException.class,
                () -> restauranteService.buscarRestaurantesCercanos(criterios));

        verify(restauranteRepository).findByActivo(true);
    }
}