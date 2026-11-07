package pe.edu.sibeca18.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import pe.edu.sibeca18.util.FormatoUtil;

/**
 * Entidad Postulación a Selección (RF-08, RF-14, RN-10, RN-11, RN-12).
 * Implementa Comparable<PostulacionSeleccion> para el ordenamiento de mérito del Ranking:
 * 1. Mayor a menor puntaje del ENP (descendente)
 * 2. En caso de empate, gana la postulación registrada primero (fechaHora ascendente)
 * 3. Como criterio determinístico adicional, menor idPostulacion.
 */
public class PostulacionSeleccion implements Serializable, Comparable<PostulacionSeleccion> {

    private Integer idPostulacion;
    private Integer idPostulante;
    private Integer idPrograma;
    private int momento; // 1 o 2
    private LocalDateTime fechaHora;
    private Integer ordenMerito;
    private String resultado; // PENDIENTE, SELECCIONADO, NO SELECCIONADO

    // Atributos asociados del postulante y programa para reportes y ranking
    private String dniPostulante;
    private String nombrePostulante;
    private String modalidadNombre;
    private BigDecimal puntajeEnp;
    private String estadoPostulante;
    private String institucionNombre;
    private String programaNombre;
    private String tipoGestionPrograma;
    private String regionPrograma;
    private int vacantesPrograma;

    public PostulacionSeleccion() {
        this.momento = 1;
        this.resultado = "PENDIENTE";
    }

    @Override
    public int compareTo(PostulacionSeleccion otra) {
        if (otra == null) {
            return -1;
        }
        BigDecimal p1 = this.puntajeEnp != null ? this.puntajeEnp : BigDecimal.ZERO;
        BigDecimal p2 = otra.puntajeEnp != null ? otra.puntajeEnp : BigDecimal.ZERO;
        // 1. Orden descendente por puntaje ENP (RN-12)
        int cmpPuntaje = p2.compareTo(p1);
        if (cmpPuntaje != 0) {
            return cmpPuntaje;
        }
        // 2. Desempate por fecha y hora de postulación ascendente: gana quien postuló primero (RN-12)
        if (this.fechaHora != null && otra.fechaHora != null) {
            int cmpFecha = this.fechaHora.compareTo(otra.fechaHora);
            if (cmpFecha != 0) {
                return cmpFecha;
            }
        }
        // 3. Desempate determinístico por idPostulacion
        if (this.idPostulacion != null && otra.idPostulacion != null) {
            return this.idPostulacion.compareTo(otra.idPostulacion);
        }
        return 0;
    }

    public Integer getIdPostulacion() {
        return idPostulacion;
    }

    public void setIdPostulacion(Integer idPostulacion) {
        this.idPostulacion = idPostulacion;
    }

    public Integer getIdPostulante() {
        return idPostulante;
    }

    public void setIdPostulante(Integer idPostulante) {
        this.idPostulante = idPostulante;
    }

    public Integer getIdPrograma() {
        return idPrograma;
    }

    public void setIdPrograma(Integer idPrograma) {
        this.idPrograma = idPrograma;
    }

    public int getMomento() {
        return momento;
    }

    public void setMomento(int momento) {
        this.momento = momento;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Integer getOrdenMerito() {
        return ordenMerito;
    }

    public void setOrdenMerito(Integer ordenMerito) {
        this.ordenMerito = ordenMerito;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getDniPostulante() {
        return dniPostulante;
    }

    public void setDniPostulante(String dniPostulante) {
        this.dniPostulante = dniPostulante;
    }

    public String getNombrePostulante() {
        return nombrePostulante;
    }

    public void setNombrePostulante(String nombrePostulante) {
        this.nombrePostulante = nombrePostulante;
    }

    public String getModalidadNombre() {
        return modalidadNombre;
    }

    public void setModalidadNombre(String modalidadNombre) {
        this.modalidadNombre = modalidadNombre;
    }

    public BigDecimal getPuntajeEnp() {
        return puntajeEnp;
    }

    public void setPuntajeEnp(BigDecimal puntajeEnp) {
        this.puntajeEnp = puntajeEnp;
    }

    public String getEstadoPostulante() {
        return estadoPostulante;
    }

    public void setEstadoPostulante(String estadoPostulante) {
        this.estadoPostulante = estadoPostulante;
    }

    public String getInstitucionNombre() {
        return institucionNombre;
    }

    public void setInstitucionNombre(String institucionNombre) {
        this.institucionNombre = institucionNombre;
    }

    public String getProgramaNombre() {
        return programaNombre;
    }

    public void setProgramaNombre(String programaNombre) {
        this.programaNombre = programaNombre;
    }

    public String getTipoGestionPrograma() {
        return tipoGestionPrograma;
    }

    public void setTipoGestionPrograma(String tipoGestionPrograma) {
        this.tipoGestionPrograma = tipoGestionPrograma;
    }

    public String getRegionPrograma() {
        return regionPrograma;
    }

    public void setRegionPrograma(String regionPrograma) {
        this.regionPrograma = regionPrograma;
    }

    public int getVacantesPrograma() {
        return vacantesPrograma;
    }

    public void setVacantesPrograma(int vacantesPrograma) {
        this.vacantesPrograma = vacantesPrograma;
    }

    public String getPuntajeEnpFormateado() {
        return FormatoUtil.formatearPuntaje(puntajeEnp);
    }

    public String getFechaHoraFormateada() {
        return FormatoUtil.formatearFechaHora(fechaHora);
    }
}
