package arcn.catalog.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * Objeto Valor que encapsula los criterios de búsqueda de restaurantes.
 * Utilizado por el caso de uso: Buscar Restaurantes Cercanos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusquedaCriterios {
    
    private Double latitudUsuario;
    private Double longitudUsuario;
    private Double radioKm;
    private LocalTime hora;
    private String categoria;
    private Double calificacionMinima;
    
    /**
     * Valida que los criterios sean válidos
     */
    public boolean esValido() {
        return latitudUsuario != null && longitudUsuario != null &&
               radioKm != null && radioKm > 0 &&
               hora != null;
    }
}
