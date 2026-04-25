package arcn.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * DTO para transferencia de datos de Productos en la API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoDTO {
    
    private String id;
    
    @NotBlank(message = "El nombre del producto es requerido")
    private String nombre;
    
    private String descripcion;
    
    @Positive(message = "El precio debe ser mayor a 0")
    private BigDecimal precio;
    
    private boolean disponible;
    private String categoria;
    private Integer tiempoPreparacion;
    private String imagen;
}
