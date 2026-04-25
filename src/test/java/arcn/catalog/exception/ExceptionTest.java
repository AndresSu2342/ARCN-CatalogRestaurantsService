package arcn.catalog.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Pruebas unitarias para excepciones personalizadas.
 */
class ExceptionTest {
    
    @Test
    void testRestauranteNoEncontradoException() {
        // Arrange
        String mensaje = "Restaurante con ID 123 no encontrado";
        
        // Act
        RestauranteNoEncontradoException exception = 
                new RestauranteNoEncontradoException(mensaje);
        
        // Assert
        assertNotNull(exception);
        assertEquals(mensaje, exception.getMessage());
    }
    
    @Test
    void testRestauranteNoEncontradoExceptionCausedBy() {
        // Arrange
        String mensaje = "Restaurante no existe";
        Throwable cause = new RuntimeException("Original error");
        
        // Act
        RestauranteNoEncontradoException exception = 
                new RestauranteNoEncontradoException(mensaje);
        exception.initCause(cause);
        
        // Assert
        assertNotNull(exception);
        assertEquals(mensaje, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
    
    @Test
    void testErrorResponse() {
        // Arrange
        ErrorResponse error = new ErrorResponse(
                404,
                "No encontrado",
                "El recurso solicitado no existe",
                null,
                "/api/test"
        );
        
        // Act & Assert
        assertNotNull(error);
        assertEquals(404, error.getStatus());
        assertEquals("No encontrado", error.getMensaje());
        assertEquals("El recurso solicitado no existe", error.getDetalle());
        assertEquals("/api/test", error.getRuta());
    }
}
