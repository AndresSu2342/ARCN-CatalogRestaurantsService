package arcn.catalog.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para la API REST.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones de validación
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        String detalles = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        String ruta = request.getDescription(false).replace("uri=", "");
        if (ruta.isEmpty()) {
            ruta = "/";
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .mensaje("Error de validación")
                .detalle(detalles)
                .timestamp(LocalDateTime.now())
                .ruta(ruta)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones de restaurante no encontrado
     */
    @ExceptionHandler(RestauranteNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleRestauranteNoEncontrado(
            RestauranteNoEncontradoException ex,
            WebRequest request) {
        String ruta = request.getDescription(false).replace("uri=", "");
        if (ruta.isEmpty()) {
            ruta = "/";
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .mensaje("Recurso no encontrado")
                .detalle(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .ruta(ruta)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja excepciones genéricas
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            WebRequest request) {
        String ruta = request.getDescription(false).replace("uri=", "");
        if (ruta.isEmpty()) {
            ruta = "/";
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .mensaje("Solicitud inválida")
                .detalle(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .ruta(ruta)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja todas las otras excepciones
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            WebRequest request) {

        log.error("Error no manejado", ex);

        String detalle = (ex.getMessage() != null) ? ex.getMessage() : "Datos inválidos";
        String ruta = request.getDescription(false).replace("uri=", "");
        if (ruta.isEmpty()) {
            ruta = "/";
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .mensaje("Error interno del servidor")
                .detalle(detalle) // <-- FIX
                .timestamp(LocalDateTime.now())
                .ruta(ruta)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
