package pe.edu.sibeca18.model;

import java.io.Serializable;
import java.time.LocalDate;
import pe.edu.sibeca18.util.FormatoUtil;

/**
 * Entidad Etapa del Cronograma de la Convocatoria 2027 (Sección 5.1, RF-12 y RN-13).
 */
public class Etapa implements Serializable {

    private Integer idEtapa;
    private int orden;
    private String codigo;
    private String nombre;
    private String fechaReferencia;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean publicada;
    private String descripcion;

    public Etapa() {
    }

    public Integer getIdEtapa() {
        return idEtapa;
    }

    public void setIdEtapa(Integer idEtapa) {
        this.idEtapa = idEtapa;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFechaReferencia() {
        return fechaReferencia;
    }

    public void setFechaReferencia(String fechaReferencia) {
        this.fechaReferencia = fechaReferencia;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public boolean isPublicada() {
        return publicada;
    }

    public void setPublicada(boolean publicada) {
        this.publicada = publicada;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Indica si una fecha determinada se encuentra dentro del rango vigente de la etapa.
     */
    public boolean estaVigenteEn(LocalDate fecha) {
        if (fechaInicio == null || fechaFin == null || fecha == null) {
            return false;
        }
        return !fecha.isBefore(fechaInicio) && !fecha.isAfter(fechaFin);
    }

    public boolean isVigenteHoy() {
        return estaVigenteEn(LocalDate.now());
    }

    public String getFechaInicioFormateada() {
        return FormatoUtil.formatearFecha(fechaInicio);
    }

    public String getFechaFinFormateada() {
        return FormatoUtil.formatearFecha(fechaFin);
    }

    public String getRangoFechasFormateado() {
        if (fechaInicio != null && fechaInicio.equals(fechaFin)) {
            return getFechaInicioFormateada();
        }
        return getFechaInicioFormateada() + " al " + getFechaFinFormateada();
    }
}
