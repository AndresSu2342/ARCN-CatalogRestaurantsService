package arcn.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalTime;

/**
 * DTO para la solicitud de búsqueda de restaurantes cercanos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusquedaRequestDTO {
    
    @NotNull(message = "La latitud es requerida")
    private Double latitudUsuario;
    
    @NotNull(message = "La longitud es requerida")
    private Double longitudUsuario;
    
    @Positive(message = "El radio debe ser mayor a 0")
    private Double radioKm;
    
    private LocalTime hora;
    private String categoria;
    private Double calificacionMinima;
}
