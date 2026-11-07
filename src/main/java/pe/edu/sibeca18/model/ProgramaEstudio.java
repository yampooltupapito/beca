package pe.edu.sibeca18.model;

import java.io.Serializable;

/**
 * Entidad Programa de Estudios (Institución y Programa con vacantes - RF-13, RN-11).
 */
public class ProgramaEstudio implements Serializable {

    private Integer idPrograma;
    private Integer idInstitucion;
    private String institucion;
    private String programaEstudios;
    private String tipoGestion; // PUBLICA o PRIVADA
    private String region;
    private int vacantes;
    private boolean activo;
    private int totalPostulantes;

    public ProgramaEstudio() {
        this.activo = true;
    }

    public Integer getIdPrograma() {
        return idPrograma;
    }

    public void setIdPrograma(Integer idPrograma) {
        this.idPrograma = idPrograma;
    }

    public Integer getIdInstitucion() {
        return idInstitucion;
    }

    public void setIdInstitucion(Integer idInstitucion) {
        this.idInstitucion = idInstitucion;
    }

    public String getInstitucion() {
        return institucion;
    }

    public void setInstitucion(String institucion) {
        this.institucion = institucion;
    }

    public String getProgramaEstudios() {
        return programaEstudios;
    }

    public void setProgramaEstudios(String programaEstudios) {
        this.programaEstudios = programaEstudios;
    }

    public String getTipoGestion() {
        return tipoGestion;
    }

    public void setTipoGestion(String tipoGestion) {
        this.tipoGestion = tipoGestion;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getVacantes() {
        return vacantes;
    }

    public void setVacantes(int vacantes) {
        this.vacantes = vacantes;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public int getTotalPostulantes() {
        return totalPostulantes;
    }

    public void setTotalPostulantes(int totalPostulantes) {
        this.totalPostulantes = totalPostulantes;
    }

    public String getDescripcionCompleta() {
        return institucion + " - " + programaEstudios + " (" + region + " | " + tipoGestion + " | Vacantes: " + vacantes + ")";
    }
}
