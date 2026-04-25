package arcn.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.List;

/**
 * DTO para transferencia de datos de Restaurantes en la API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestauranteDTO {
    
    private String id;
    
    @NotBlank(message = "El nombre del restaurante es requerido")
    private String nombre;
    
    private String descripcion;
    
    @NotBlank(message = "La dirección es requerida")
    private String direccion;
    
    @NotNull(message = "La latitud es requerida")
    private Double latitud;
    
    @NotNull(message = "La longitud es requerida")
    private Double longitud;
    
    private String telefono;
    private String email;
    
    @NotNull(message = "La hora de apertura es requerida")
    private LocalTime horaApertura;
    
    @NotNull(message = "La hora de cierre es requerida")
    private LocalTime horaCierre;
    
    private Double calificacion;
    private Integer numeroResenas;
    private String imagen;
    private List<String> categorias;
    private List<ProductoDTO> menu;
    private boolean activo;
    private Double distanciaKm; // Campo calculado en búsquedas
}
