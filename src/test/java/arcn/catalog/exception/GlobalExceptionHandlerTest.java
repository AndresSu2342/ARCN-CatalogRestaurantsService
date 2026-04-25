package arcn.catalog.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias para GlobalExceptionHandler.
 */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Test
    void testHandleRestauranteNoEncontrado() {
        // Arrange
        RestauranteNoEncontradoException exception = new RestauranteNoEncontradoException("Restaurante no encontrado");

        WebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleRestauranteNoEncontrado(exception, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Recurso no encontrado", response.getBody().getMensaje());
        assertEquals("Restaurante no encontrado", response.getBody().getDetalle());
        assertNotNull(response.getBody().getTimestamp());
        assertTrue(response.getBody().getRuta().contains("/"));

    }

    @Test
    void testHandleIllegalArgument() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException("Datos inválidos");

        WebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgument(exception, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Solicitud inválida", response.getBody().getMensaje());
        assertTrue(response.getBody().getRuta().contains("/"));

    }

    @Test
    void testHandleGlobalException() {
        // Arrange
        Exception exception = new Exception("Error inesperado");
        WebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(exception, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Error interno del servidor", response.getBody().getMensaje());
        assertTrue(response.getBody().getRuta().contains("/"));

    }

    @Test
    void testErrorResponseBuilder() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        // Act
        ErrorResponse error = ErrorResponse.builder()
                .status(400)
                .mensaje("Error de validación")
                .detalle("Campo requerido")
                .timestamp(now)
                .ruta("/api/test")
                .build();

        // Assert
        assertNotNull(error);
        assertEquals(400, error.getStatus());
        assertEquals("Error de validación", error.getMensaje());
        assertEquals("Campo requerido", error.getDetalle());
        assertEquals(now, error.getTimestamp());
        assertEquals("/api/test", error.getRuta());
    }

    @Test
    void testHandleGlobalException_messageNull() {
        // Arrange
        Exception exception = new Exception(); // SIN mensaje
        WebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(exception, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Datos inválidos", response.getBody().getDetalle());
    }

    @Test
    void shouldSetDefaultRouteWhenEmpty() {
        Exception ex = new Exception("error");

        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri="); // vacío

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(ex, request);

        assertEquals("/", response.getBody().getRuta());
    }

    @Test
    void shouldReturnRouteWhenNotEmpty() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/api/test");

        ResponseEntity<ErrorResponse> response = handler.handleGlobalException(new Exception("error"), request);

        assertEquals("/api/test", response.getBody().getRuta());
    }

    @Test
    void shouldBuildValidationErrorDetails() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError error1 = new FieldError("obj", "nombre", "requerido");
        FieldError error2 = new FieldError("obj", "direccion", "invalida");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(error1, error2));

        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/test");

        ResponseEntity<ErrorResponse> response = handler.handleValidationExceptions(ex, request);

        assertTrue(response.getBody().getDetalle().contains("nombre"));
        assertTrue(response.getBody().getDetalle().contains("direccion"));
    }

    @Test
    void shouldHandleIllegalArgumentWithEmptyRoute() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=");

        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(new IllegalArgumentException("bad"),
                request);

        assertEquals("/", response.getBody().getRuta());
    }

}
