package pe.edu.sibeca18.util;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Utilitario de validaciones con Expresiones Regulares, String y StringBuilder (Tema 4).
 */
public final class ValidadorUtil {

    // Expresiones regulares compiladas (RF-04, Tema 4)
    private static final Pattern PATRON_DNI = Pattern.compile("^\\d{8}$");
    private static final Pattern PATRON_CELULAR_PERU = Pattern.compile("^9\\d{8}$");
    private static final Pattern PATRON_CORREO = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,10}$");
    private static final Pattern PATRON_USERNAME = Pattern.compile("^[A-Za-z0-9._-]{4,50}$");
    private static final Pattern PATRON_SOLO_LETRAS = Pattern.compile("^[A-Za-zÁÉÍÓÚáéíóúÑñÜü\\s'-]{2,100}$");

    private ValidadorUtil() {
    }

    public static boolean esDniValido(String dni) {
        return dni != null && PATRON_DNI.matcher(dni.trim()).matches();
    }

    public static boolean esCelularPeruanoValido(String celular) {
        return celular != null && PATRON_CELULAR_PERU.matcher(celular.trim()).matches();
    }

    public static boolean esCorreoValido(String correo) {
        return correo != null && PATRON_CORREO.matcher(correo.trim()).matches();
    }

    public static boolean esUsernameValido(String username) {
        return username != null && PATRON_USERNAME.matcher(username.trim()).matches();
    }

    public static boolean esNombreValido(String texto) {
        return texto != null && PATRON_SOLO_LETRAS.matcher(texto.trim()).matches();
    }

    public static boolean esVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    public static String limpiarTexto(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.trim().replaceAll("\\s+", " ");
    }

    /**
     * Construye un mensaje consolidado de errores utilizando StringBuilder (Tema 4).
     */
    public static String consolidarMensajes(List<String> errores) {
        if (errores == null || errores.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < errores.size(); i++) {
            if (i > 0) {
                sb.append(" | ");
            }
            sb.append(errores.get(i));
        }
        return sb.toString();
    }
}
