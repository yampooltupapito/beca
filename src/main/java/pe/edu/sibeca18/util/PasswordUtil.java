package pe.edu.sibeca18.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilitario para el hash y verificación segura de contraseñas mediante SHA-256.
 * Garantiza que ninguna clave se almacene en texto plano en la base de datos.
 */
public final class PasswordUtil {

    private PasswordUtil() {
    }

    /**
     * Genera el hash SHA-256 en formato hexadecimal minúscula (compatible con SHA2(str, 256) de MySQL 8).
     */
    public static String hashPassword(String passwordPlano) {
        if (passwordPlano == null) {
            throw new IllegalArgumentException("La contraseña no puede ser nula.");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(passwordPlano.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Algoritmo SHA-256 no disponible en la JVM.", ex);
        }
    }

    /**
     * Verifica si una contraseña en texto plano coincide con el hash almacenado.
     */
    public static boolean verificarPassword(String passwordPlano, String hashAlmacenado) {
        if (passwordPlano == null || hashAlmacenado == null) {
            return false;
        }
        String hashCalculado = hashPassword(passwordPlano);
        return MessageDigest.isEqual(
                hashCalculado.toLowerCase().getBytes(StandardCharsets.UTF_8),
                hashAlmacenado.trim().toLowerCase().getBytes(StandardCharsets.UTF_8)
        );
    }
}
