package pe.edu.sibeca18.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import pe.edu.sibeca18.util.FormatoUtil;

/**
 * Entidad Usuario (Autenticación y control de roles: ADMINISTRADOR, POSTULANTE).
 */
public class Usuario implements Serializable {

    private Integer idUsuario;
    private String username;
    private String passwordHash;
    private String rol;
    private boolean activo;
    private LocalDateTime fechaCreacion;

    public Usuario() {
        this.activo = true;
    }

    public Usuario(Integer idUsuario, String username, String passwordHash, String rol, boolean activo) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.activo = activo;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public boolean esAdministrador() {
        return "ADMINISTRADOR".equalsIgnoreCase(this.rol);
    }

    public boolean esPostulante() {
        return "POSTULANTE".equalsIgnoreCase(this.rol);
    }

    public String getFechaCreacionFormateada() {
        return FormatoUtil.formatearFechaHora(fechaCreacion);
    }
}
