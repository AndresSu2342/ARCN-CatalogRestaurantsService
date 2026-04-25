package arcn.catalog.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para DTOs (cubriendo métodos generados por Lombok).
 */
class DtoTest {

    @Test
    void testRestauranteDTOLombokMethods() {
        // Arrange
        RestauranteDTO dto1 = RestauranteDTO.builder().id("1").nombre("A").build();
        RestauranteDTO dto2 = RestauranteDTO.builder().id("1").nombre("A").build();
        RestauranteDTO dto3 = RestauranteDTO.builder().id("2").nombre("B").build();

        // Act & Assert
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
        assertNotNull(dto1.toString());
        
        // Test Setters/Getters not covered by builder
        RestauranteDTO dto4 = new RestauranteDTO();
        dto4.setId("4");
        assertEquals("4", dto4.getId());
    }

    @Test
    void testProductoDTOLombokMethods() {
        // Arrange
        ProductoDTO dto1 = ProductoDTO.builder().id("1").nombre("A").build();
        ProductoDTO dto2 = ProductoDTO.builder().id("1").nombre("A").build();
        ProductoDTO dto3 = ProductoDTO.builder().id("2").nombre("B").build();

        // Act & Assert
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
        assertNotNull(dto1.toString());
        
        // Test Setters/Getters not covered by builder
        ProductoDTO dto4 = new ProductoDTO();
        dto4.setId("4");
        assertEquals("4", dto4.getId());
    }

    @Test
    void testBusquedaRequestDTOLombokMethods() {
        // Arrange
        BusquedaRequestDTO dto1 = BusquedaRequestDTO.builder().radioKm(5.0).build();
        BusquedaRequestDTO dto2 = BusquedaRequestDTO.builder().radioKm(5.0).build();
        BusquedaRequestDTO dto3 = BusquedaRequestDTO.builder().radioKm(10.0).build();

        // Act & Assert
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
        assertNotNull(dto1.toString());
        
        // Test Setters/Getters not covered by builder
        BusquedaRequestDTO dto4 = new BusquedaRequestDTO();
        dto4.setRadioKm(4.0);
        assertEquals(4.0, dto4.getRadioKm());
    }
}
