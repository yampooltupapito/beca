package pe.edu.sibeca18.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Utilitario para el manejo de fechas, cálculo de edad a fecha de corte y
 * formato de números/puntajes bajo el locale es-PE (Tema 1).
 */
public final class FormatoUtil {

    public static final Locale LOCALE_PE = Locale.forLanguageTag("es-PE");
    public static final DateTimeFormatter FORMATO_FECHA_PE = DateTimeFormatter.ofPattern("dd/MM/yyyy", LOCALE_PE);
    public static final DateTimeFormatter FORMATO_FECHA_HORA_PE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", LOCALE_PE);
    public static final DateTimeFormatter FORMATO_FECHA_HORA_MS_PE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS", LOCALE_PE);
    public static final LocalDate FECHA_CORTE_DEFECTO = LocalDate.of(2026, 10, 9);

    private FormatoUtil() {
    }

    /**
     * Calcula la edad cumplida en años entre la fecha de nacimiento y la fecha de corte parametrizable (RN-02).
     *
     * @param fechaNacimiento fecha de nacimiento del postulante
     * @param fechaCorte fecha de corte parametrizada en el sistema
     * @return edad en años cumplidos
     */
    public static int calcularEdad(LocalDate fechaNacimiento, LocalDate fechaCorte) {
        if (fechaNacimiento == null) {
            return 0;
        }
        LocalDate corteEfectivo = (fechaCorte != null) ? fechaCorte : FECHA_CORTE_DEFECTO;
        if (fechaNacimiento.isAfter(corteEfectivo)) {
            return -1;
        }
        return Period.between(fechaNacimiento, corteEfectivo).getYears();
    }

    /**
     * Formatea una fecha LocalDate al formato peruano dd/MM/yyyy.
     */
    public static String formatearFecha(LocalDate fecha) {
        if (fecha == null) {
            return "-";
        }
        return fecha.format(FORMATO_FECHA_PE);
    }

    /**
     * Formatea un LocalDateTime al formato peruano dd/MM/yyyy HH:mm:ss.
     */
    public static String formatearFechaHora(LocalDateTime fechaHora) {
        if (fechaHora == null) {
            return "-";
        }
        return fechaHora.format(FORMATO_FECHA_HoraConPrecision(fechaHora));
    }

    private static DateTimeFormatter FORMATO_FECHA_HoraConPrecision(LocalDateTime fechaHora) {
        if (fechaHora.getNano() > 0) {
            return FORMATO_FECHA_HORA_MS_PE;
        }
        return FORMATO_FECHA_HORA_PE;
    }

    /**
     * Formatea un puntaje numérico o decimal usando el locale es-PE (2 decimales).
     */
    public static String formatearPuntaje(BigDecimal puntaje) {
        if (puntaje == null) {
            return "Pendiente";
        }
        NumberFormat nf = NumberFormat.getNumberInstance(LOCALE_PE);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        return nf.format(puntaje) + " pts";
    }

    /**
     * Formatea un puntaje double usando el locale es-PE.
     */
    public static String formatearNumero(double valor) {
        NumberFormat nf = NumberFormat.getNumberInstance(LOCALE_PE);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        return nf.format(valor);
    }

    /**
     * Parsea una cadena yyyy-MM-dd a LocalDate de forma segura.
     */
    public static LocalDate parsearFechaIso(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(texto.trim());
        } catch (DateTimeParseException ex) {
            return null;
        }
    }
}
