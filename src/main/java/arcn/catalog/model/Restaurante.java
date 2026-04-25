package arcn.catalog.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Agregado Raíz del Bounded Context Catálogo de Restaurantes.
 * Representa un restaurante con su información, ubicación, horarios y menú.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "restaurantes")
public class Restaurante {
    
    @Id
    private String id;
    private String nombre;
    private String descripcion;
    private String direccion;
    private Double latitud;
    private Double longitud;
    private String telefono;
    private String email;
    private LocalTime horaApertura;
    private LocalTime horaCierre;
    private Double calificacion;
    private Integer numeroResenas;
    private String imagen;
    private List<String> categorias;
    private List<Producto> menu;
    private boolean activo;
    
    /**
     * Inicializa el menú como una lista vacía
     */
    public void inicializarMenu() {
        if (this.menu == null) {
            this.menu = new ArrayList<>();
        }
    }
    
    /**
     * Agrega un producto al menú del restaurante
     */
    public void agregarProductoAlMenu(Producto producto) {
        if (producto != null && producto.esValido()) {
            inicializarMenu();
            this.menu.add(producto);
        }
    }
    
    /**
     * Valida que el restaurante sea válido
     */
    public boolean esValido() {
        return nombre != null && !nombre.isBlank() &&
               direccion != null && !direccion.isBlank() &&
               latitud != null && longitud != null &&
               horaApertura != null && horaCierre != null;
    }
    
    /**
     * Verifica si el restaurante está abierto en cierta hora
     */
    public boolean estaAbierto(LocalTime hora) {
        return !hora.isBefore(horaApertura) && !hora.isAfter(horaCierre);
    }
    
    /**
     * Calcula la distancia en km desde una ubicación
     */
    public Double calcularDistancia(Double latitudUsuario, Double longitudUsuario) {
        final int RADIO_TIERRA_KM = 6371;
        
        Double lat1Rad = Math.toRadians(latitudUsuario);
        Double lat2Rad = Math.toRadians(this.latitud);
        Double deltaLat = Math.toRadians(this.latitud - latitudUsuario);
        Double deltaLon = Math.toRadians(this.longitud - longitudUsuario);
        
        Double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return RADIO_TIERRA_KM * c;
    }
}
