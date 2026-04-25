package arcn.catalog.controller;

import arcn.catalog.dto.BusquedaRequestDTO;
import arcn.catalog.dto.ProductoDTO;
import arcn.catalog.dto.RestauranteDTO;
import arcn.catalog.model.Producto;
import arcn.catalog.service.MapperService;
import arcn.catalog.service.RestauranteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias para RestauranteController.
 */
@ExtendWith(MockitoExtension.class)
class RestauranteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RestauranteService restauranteService;

    @Mock
    private MapperService mapperService;

    @InjectMocks
    private RestauranteController restauranteController;

    private ObjectMapper objectMapper;
    private RestauranteDTO restauranteDTO;
    private ProductoDTO productoDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(restauranteController)
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

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

        productoDTO = ProductoDTO.builder()
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
    void testBuscarRestaurantesCercanos() throws Exception {
        BusquedaRequestDTO busqueda = BusquedaRequestDTO.builder()
                .latitudUsuario(40.7150)
                .longitudUsuario(-74.0030)
                .radioKm(5.0)
                .hora(LocalTime.of(12, 0))
                .categoria("Italiana")
                .build();

        RestauranteDTO resultadoDTO = restauranteDTO;
        resultadoDTO.setDistanciaKm(0.85);

        when(restauranteService.buscarRestaurantesCercanos(any()))
                .thenReturn(List.of(resultadoDTO));

        mockMvc.perform(post("/api/v1/restaurantes/buscar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(busqueda)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Pizzería Roma"))
                .andExpect(jsonPath("$[0].distanciaKm").value(0.85));

        verify(restauranteService, times(1)).buscarRestaurantesCercanos(any());
    }

    @Test
    void testObtenerTodos() throws Exception {
        when(restauranteService.obtenerTodosLosRestaurantes())
                .thenReturn(List.of(restauranteDTO));

        mockMvc.perform(get("/api/v1/restaurantes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Pizzería Roma"));

        verify(restauranteService, times(1)).obtenerTodosLosRestaurantes();
    }

    @Test
    void testObtenerPorId() throws Exception {
        when(restauranteService.obtenerRestaurantePorId("1"))
                .thenReturn(Optional.of(restauranteDTO));

        mockMvc.perform(get("/api/v1/restaurantes/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Pizzería Roma"));

        verify(restauranteService, times(1)).obtenerRestaurantePorId("1");
    }

    @Test
    void testObtenerPorIdNoEncontrado() throws Exception {
        when(restauranteService.obtenerRestaurantePorId("inexistente"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/restaurantes/inexistente")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(restauranteService, times(1)).obtenerRestaurantePorId("inexistente");
    }

    @Test
    void testCrearRestaurante() throws Exception {
        when(restauranteService.crearRestaurante(any(RestauranteDTO.class)))
                .thenReturn(restauranteDTO);

        mockMvc.perform(post("/api/v1/restaurantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restauranteDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Pizzería Roma"));

        verify(restauranteService, times(1)).crearRestaurante(any(RestauranteDTO.class));
    }

    @Test
    void testCrearRestauranteDatosInvalidos() throws Exception {
        RestauranteDTO inválido = RestauranteDTO.builder().nombre("").build();

        mockMvc.perform(post("/api/v1/restaurantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inválido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testActualizarRestaurante() throws Exception {
        RestauranteDTO actualizado = restauranteDTO;
        actualizado.setNombre("Pizzería Roma Actualizada");

        when(restauranteService.actualizarRestaurante(eq("1"), any(RestauranteDTO.class)))
                .thenReturn(Optional.of(actualizado));

        mockMvc.perform(put("/api/v1/restaurantes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restauranteDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Pizzería Roma Actualizada"));

        verify(restauranteService, times(1)).actualizarRestaurante(eq("1"), any(RestauranteDTO.class));
    }

    @Test
    void testActualizarRestauranteNoEncontrado() throws Exception {
        when(restauranteService.actualizarRestaurante(eq("inexistente"), any()))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/restaurantes/inexistente")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restauranteDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDesactivarRestaurante() throws Exception {
        when(restauranteService.desactivarRestaurante("1"))
                .thenReturn(true);

        mockMvc.perform(delete("/api/v1/restaurantes/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(restauranteService, times(1)).desactivarRestaurante("1");
    }

    @Test
    void testDesactivarRestauranteNoEncontrado() throws Exception {
        when(restauranteService.desactivarRestaurante("inexistente"))
                .thenReturn(false);

        mockMvc.perform(delete("/api/v1/restaurantes/inexistente")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testBuscarPorNombre() throws Exception {
        when(restauranteService.buscarPorNombre("Roma"))
                .thenReturn(List.of(restauranteDTO));

        mockMvc.perform(get("/api/v1/restaurantes/nombre/Roma")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Pizzería Roma"));

        verify(restauranteService, times(1)).buscarPorNombre("Roma");
    }

    @Test
    void testBuscarPorCategoria() throws Exception {
        when(restauranteService.buscarPorCategoria("Italiana"))
                .thenReturn(List.of(restauranteDTO));

        mockMvc.perform(get("/api/v1/restaurantes/categoria/Italiana")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Pizzería Roma"));

        verify(restauranteService, times(1)).buscarPorCategoria("Italiana");
    }

    @Test
    void testObtenerMenu() throws Exception {
        Producto producto = Producto.builder()
                .id("p1")
                .nombre("Pizza Margherita")
                .precio(BigDecimal.valueOf(10.99))
                .build();

        when(restauranteService.obtenerMenuDelRestaurante("1"))
                .thenReturn(List.of(producto));

        mockMvc.perform(get("/api/v1/restaurantes/1/menu")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Pizza Margherita"));

        verify(restauranteService, times(1)).obtenerMenuDelRestaurante("1");
    }

    @Test
    void testAgregarProductoAlMenu() throws Exception {
        when(restauranteService.agregarProductoAlMenu(eq("1"), any(Producto.class)))
                .thenReturn(Optional.of(restauranteDTO));

        mockMvc.perform(post("/api/v1/restaurantes/1/menu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Pizzería Roma"));

        verify(restauranteService, times(1)).agregarProductoAlMenu(eq("1"), any(Producto.class));
    }

    @Test
    void testAgregarProductoAlMenuNoEncontrado() throws Exception {
        when(restauranteService.agregarProductoAlMenu(eq("inexistente"), any()))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/v1/restaurantes/inexistente/menu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productoDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCrearRestauranteIllegalArgument() throws Exception {
        when(restauranteService.crearRestaurante(any()))
                .thenThrow(new IllegalArgumentException("Datos inválidos"));

        mockMvc.perform(post("/api/v1/restaurantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restauranteDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testBuscarRestaurantesCercanosConValoresNull() throws Exception {
        BusquedaRequestDTO busqueda = BusquedaRequestDTO.builder()
                .latitudUsuario(40.0)
                .longitudUsuario(-74.0)
                .radioKm(null)
                .hora(null)
                .build();

        when(restauranteService.buscarRestaurantesCercanos(any()))
                .thenReturn(List.of(restauranteDTO));

        mockMvc.perform(post("/api/v1/restaurantes/buscar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(busqueda)))
                .andExpect(status().isOk());

        verify(restauranteService).buscarRestaurantesCercanos(any());
    }

    @Test
    void testObtenerMenuVacio() throws Exception {
        when(restauranteService.obtenerMenuDelRestaurante("1"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/restaurantes/1/menu"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void testObtenerMenuCamposNull() throws Exception {
        Producto producto = Producto.builder()
                .id("p1")
                .nombre(null)
                .precio(null)
                .build();

        when(restauranteService.obtenerMenuDelRestaurante("1"))
                .thenReturn(List.of(producto));

        mockMvc.perform(get("/api/v1/restaurantes/1/menu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("p1"));
    }

    @Test
    void testAgregarProductoInvalido() throws Exception {
        ProductoDTO invalido = ProductoDTO.builder()
                .nombre("") // inválido
                .build();

        mockMvc.perform(post("/api/v1/restaurantes/1/menu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }
}
