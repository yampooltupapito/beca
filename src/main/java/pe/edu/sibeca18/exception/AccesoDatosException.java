package pe.edu.sibeca18.exception;

/**
 * Excepción personalizada para encapsular errores técnicos de acceso a datos JDBC/DataSource
 * sin exponer detalles internos de SQL al usuario final.
 */
public class AccesoDatosException extends RuntimeException {

    public AccesoDatosException(String mensaje) {
        super(mensaje);
    }

    public AccesoDatosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
