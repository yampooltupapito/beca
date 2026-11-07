package pe.edu.sibeca18.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import pe.edu.sibeca18.util.FormatoUtil;

/**
 * Entidad para representar registros de auditoría de la bitácora (RF-16).
 */
public class BitacoraEvento implements Serializable {

    private Long idEvento;
    private LocalDateTime fechaHora;
    private String tipoEvento;
    private String usuario;
    private String detalle;

    public BitacoraEvento() {
    }

    public BitacoraEvento(Long idEvento, LocalDateTime fechaHora, String tipoEvento, String usuario, String detalle) {
        this.idEvento = idEvento;
        this.fechaHora = fechaHora;
        this.tipoEvento = tipoEvento;
        this.usuario = usuario;
        this.detalle = detalle;
    }

    public Long getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(Long idEvento) {
        this.idEvento = idEvento;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getFechaHoraFormateada() {
        return FormatoUtil.formatearFechaHora(fechaHora);
    }
}
