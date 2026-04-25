package arcn.catalog.exception;

/**
 * Excepción base del dominio - Restaurante no encontrado.
 */
public class RestauranteNoEncontradoException extends RuntimeException {
    public RestauranteNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
