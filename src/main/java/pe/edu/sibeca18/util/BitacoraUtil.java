package pe.edu.sibeca18.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Gestor de Bitácora del Sistema (RF-16, Tema 3: Flujos de Entrada/Salida).
 * Registra en un archivo de texto plano (con fecha y hora en formato es-PE)
 * y de forma complementaria en la tabla bitacora_evento:
 * - Inicios de sesión (exitosos y fallidos)
 * - Cambios de estado de postulantes
 * - Ejecución del proceso de selección y reversiones
 */
public final class BitacoraUtil {

    private static final Object LOCK = new Object();
    private static volatile String rutaArchivoBitacora;

    private BitacoraUtil() {
    }

    public static synchronized void configurarRutaArchivo(String ruta) {
        rutaArchivoBitacora = ruta;
    }

    public static File obtenerArchivoBitacora() {
        String ruta = rutaArchivoBitacora;
        if (ruta == null || ruta.isBlank()) {
            String userHome = System.getProperty("user.dir", ".");
            File carpeta = new File(userHome, "logs");
            if (!carpeta.exists()) {
                carpeta.mkdirs();
            }
            return new File(carpeta, "bitacora_sibeca18.txt");
        }
        File archivo = new File(ruta);
        if (archivo.getParentFile() != null && !archivo.getParentFile().exists()) {
            archivo.getParentFile().mkdirs();
        }
        return archivo;
    }

    /**
     * Registra un evento en el archivo de texto de bitácora usando flujos de E/S y StringBuilder,
     * e intenta respaldarlo en la tabla bitacora_evento si el DataSource está activo.
     */
    public static void registrarEvento(String tipoEvento, String usuario, String detalle) {
        String fechaFormateada = FormatoUtil.formatearFechaHora(LocalDateTime.now());
        String usuarioLimpio = (usuario == null || usuario.isBlank()) ? "ANONIMO" : usuario.trim();
        String tipoLimpio = (tipoEvento == null || tipoEvento.isBlank()) ? "INFO" : tipoEvento.trim().toUpperCase();

        StringBuilder linea = new StringBuilder(160);
        linea.append("[").append(fechaFormateada).append("] ")
             .append("[").append(tipoLimpio).append("] ")
             .append("[Usuario: ").append(usuarioLimpio).append("] - ")
             .append(detalle != null ? detalle : "");

        synchronized (LOCK) {
            File archivo = obtenerArchivoBitacora();
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(archivo, true), StandardCharsets.UTF_8))) {
                writer.write(linea.toString());
                writer.newLine();
                writer.flush();
            } catch (IOException ex) {
                System.err.println("No se pudo escribir en archivo de bitácora: " + ex.getMessage());
            }
        }

        // Respaldo opcional en base de datos PostgreSQL
        String sql = "INSERT INTO bitacora_evento (fecha_hora, tipo_evento, usuario, detalle) VALUES (CURRENT_TIMESTAMP, ?, ?, ?)";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tipoLimpio);
            ps.setString(2, usuarioLimpio);
            ps.setString(3, detalle != null ? detalle : "");
            ps.executeUpdate();
        } catch (Exception ignored) {
            // Si ocurre durante una prueba sin DB o fallo de conexión, el archivo TXT sigue garantizado
        }
    }

    /**
     * Lee las líneas registradas en el archivo de bitácora mediante BufferedReader (Tema 3: E/S).
     */
    public static List<String> leerLineasArchivo(int maxLineas) {
        List<String> lineas = new ArrayList<>();
        synchronized (LOCK) {
            File archivo = obtenerArchivoBitacora();
            if (!archivo.exists()) {
                return lineas;
            }
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(archivo), StandardCharsets.UTF_8))) {
                String linea;
                while ((linea = reader.readLine()) != null) {
                    if (!linea.isBlank()) {
                        lineas.add(linea);
                    }
                }
            } catch (IOException ex) {
                lineas.add("[ERROR LECTURA BITÁCORA]: " + ex.getMessage());
            }
        }
        Collections.reverse(lineas);
        if (maxLineas > 0 && lineas.size() > maxLineas) {
            return new ArrayList<>(lineas.subList(0, maxLineas));
        }
        return lineas;
    }

    /**
     * Exporta todo el contenido del archivo de bitácora como texto plano.
     */
    public static String exportarContenidoCompleto() {
        List<String> lineas = leerLineasArchivo(0);
        Collections.reverse(lineas);
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================================================\n");
        sb.append(" SIBECA-18 | BITÁCORA OFICIAL DE EVENTOS DEL SISTEMA (CONVOCATORIA 2027)\n");
        sb.append(" Archivo físico: ").append(obtenerArchivoBitacora().getAbsolutePath()).append("\n");
        sb.append("====================================================================================\n");
        for (String l : lineas) {
            sb.append(l).append("\n");
        }
        return sb.toString();
    }
}
