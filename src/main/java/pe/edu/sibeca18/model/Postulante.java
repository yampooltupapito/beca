package pe.edu.sibeca18.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import pe.edu.sibeca18.util.FormatoUtil;

/**
 * Entidad Postulante (Beneficiario de Beca 18 - Sección 8, RF-03..RF-11).
 */
public class Postulante implements Serializable {

    private Integer idPostulante;
    private Integer idUsuario;
    private String username;
    private String dni;
    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private int edadCorte;
    private String nacionalidad;
    private String correo;
    private String celular;
    private String region;
    private String colegio;
    private String situacionEstudios; // CURSANDO_5TO o EGRESADO
    private int anioEgreso;
    private boolean tercioSuperior;
    private String clasificacionSocioeconomica; // POBREZA_EXTREMA, POBREZA, VULNERABLE, NO_POBRE
    private boolean discapacidadAcreditada;
    private Integer idModalidad;
    private Modalidad modalidad;
    private String estado;
    private String observacionRevision;
    private BigDecimal puntajeEnp;
    private LocalDateTime fechaInscripcion;

    public Postulante() {
        this.nacionalidad = "PERUANA";
        this.estado = "INSCRITO";
    }

    public Integer getIdPostulante() {
        return idPostulante;
    }

    public void setIdPostulante(Integer idPostulante) {
        this.idPostulante = idPostulante;
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

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public int getEdadCorte() {
        return edadCorte;
    }

    public void setEdadCorte(int edadCorte) {
        this.edadCorte = edadCorte;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getColegio() {
        return colegio;
    }

    public void setColegio(String colegio) {
        this.colegio = colegio;
    }

    public String getSituacionEstudios() {
        return situacionEstudios;
    }

    public void setSituacionEstudios(String situacionEstudios) {
        this.situacionEstudios = situacionEstudios;
    }

    public int getAnioEgreso() {
        return anioEgreso;
    }

    public void setAnioEgreso(int anioEgreso) {
        this.anioEgreso = anioEgreso;
    }

    public boolean isTercioSuperior() {
        return tercioSuperior;
    }

    public void setTercioSuperior(boolean tercioSuperior) {
        this.tercioSuperior = tercioSuperior;
    }

    public String getClasificacionSocioeconomica() {
        return clasificacionSocioeconomica;
    }

    public void setClasificacionSocioeconomica(String clasificacionSocioeconomica) {
        this.clasificacionSocioeconomica = clasificacionSocioeconomica;
    }

    public boolean isDiscapacidadAcreditada() {
        return discapacidadAcreditada;
    }

    public void setDiscapacidadAcreditada(boolean discapacidadAcreditada) {
        this.discapacidadAcreditada = discapacidadAcreditada;
    }

    public Integer getIdModalidad() {
        return idModalidad;
    }

    public void setIdModalidad(Integer idModalidad) {
        this.idModalidad = idModalidad;
    }

    public Modalidad getModalidad() {
        return modalidad;
    }

    public void setModalidad(Modalidad modalidad) {
        this.modalidad = modalidad;
        if (modalidad != null && modalidad.getIdModalidad() != null) {
            this.idModalidad = modalidad.getIdModalidad();
        }
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservacionRevision() {
        return observacionRevision;
    }

    public void setObservacionRevision(String observacionRevision) {
        this.observacionRevision = observacionRevision;
    }

    public BigDecimal getPuntajeEnp() {
        return puntajeEnp;
    }

    public void setPuntajeEnp(BigDecimal puntajeEnp) {
        this.puntajeEnp = puntajeEnp;
    }

    public LocalDateTime getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(LocalDateTime fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    // Métodos auxiliares para Java Expression Language (EL) en vistas JSP
    public String getNombreCompleto() {
        return ((apellidos != null ? apellidos : "") + ", " + (nombres != null ? nombres : "")).trim();
    }

    public String getModalidadNombre() {
        return (modalidad != null && modalidad.getNombre() != null) ? modalidad.getNombre() : "-";
    }

    public String getFechaNacimientoFormateada() {
        return FormatoUtil.formatearFecha(fechaNacimiento);
    }

    public String getFechaNacimientoIso() {
        return fechaNacimiento != null ? fechaNacimiento.toString() : "";
    }

    public String getPuntajeEnpFormateado() {
        return FormatoUtil.formatearPuntaje(puntajeEnp);
    }

    public String getFechaInscripcionFormateada() {
        return FormatoUtil.formatearFechaHora(fechaInscripcion);
    }

    public String getClasificacionLegible() {
        if (clasificacionSocioeconomica == null) {
            return "-";
        }
        return switch (clasificacionSocioeconomica) {
            case "POBREZA_EXTREMA" -> "Pobreza Extrema";
            case "POBREZA" -> "Pobreza";
            case "VULNERABLE" -> "Vulnerable / Especial";
            case "NO_POBRE" -> "No Pobre";
            default -> clasificacionSocioeconomica;
        };
    }

    public String getSituacionLegible() {
        if ("CURSANDO_5TO".equals(situacionEstudios)) {
            return "Cursando 5.° de Secundaria (" + anioEgreso + ")";
        }
        return "Egresado de Secundaria (" + anioEgreso + ")";
    }

    /**
     * Determina en qué número de etapa (1 a 8) se ubica actualmente el postulante para la línea de tiempo (RF-06).
     */
    public int getEtapaActualProceso() {
        if (estado == null) {
            return 1;
        }
        return switch (estado) {
            case "INSCRITO" -> 1;
            case "EN REVISION" -> 2;
            case "APTO", "NO APTO" -> 3;
            case "PRESELECCIONADO", "NO PRESELECCIONADO" -> 5;
            case "POSTULO A SELECCION" -> 6;
            case "SELECCIONADO", "NO SELECCIONADO" -> 8;
            default -> 1;
        };
    }
}
