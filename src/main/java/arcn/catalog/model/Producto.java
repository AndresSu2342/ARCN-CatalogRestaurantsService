package arcn.catalog.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

/**
 * Entidad que representa un Producto dentro del Menú de un Restaurante.
 * Forma parte del Agregado Restaurante.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "productos")
public class Producto {
    
    private String id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private boolean disponible;
    private String categoria;
    private Integer tiempoPreparacion; // en minutos
    private String imagen;
    
    /**
     * Valida que el producto sea válido
     */
    public boolean esValido() {
        return nombre != null && !nombre.isBlank() &&
               precio != null && precio.compareTo(BigDecimal.ZERO) > 0;
    }
}
