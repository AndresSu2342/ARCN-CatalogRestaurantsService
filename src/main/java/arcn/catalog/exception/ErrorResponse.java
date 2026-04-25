package arcn.catalog.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Modelo de respuesta de error estandarizada para la API.
 */
@Data
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private int status;
    private String mensaje;
    private String detalle;
    private LocalDateTime timestamp;
    private String ruta;
}
