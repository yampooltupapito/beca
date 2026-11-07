package pe.edu.sibeca18.exception;

/**
 * Excepción personalizada para representar incumplimientos de las Reglas de Negocio
 * (RN-01 a RN-14) del proceso Beca 18 - Convocatoria 2027.
 */
public class ReglaNegocioException extends Exception {

    private final String codigoRegla;

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
        this.codigoRegla = extraerCodigo(mensaje);
    }

    public ReglaNegocioException(String codigoRegla, String mensaje) {
        super(codigoRegla + ": " + mensaje);
        this.codigoRegla = codigoRegla;
    }

    public ReglaNegocioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.codigoRegla = extraerCodigo(mensaje);
    }

    public String getCodigoRegla() {
        return codigoRegla;
    }

    private String extraerCodigo(String msg) {
        if (msg != null && msg.matches("^RN-\\d{2}:.*")) {
            return msg.substring(0, 5);
        }
        return "RN-GEN";
    }
}
