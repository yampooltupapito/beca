package pe.edu.sibeca18.model;

import java.io.Serializable;

/**
 * Entidad Institución de Educación Superior Elegible (RF-13, RN-11).
 */
public class Institucion implements Serializable {

    private Integer idInstitucion;
    private String nombre;
    private String tipoGestion; // PUBLICA o PRIVADA
    private String region;
    private boolean elegible;

    public Institucion() {
        this.elegible = true;
    }

    public Institucion(Integer idInstitucion, String nombre, String tipoGestion, String region, boolean elegible) {
        this.idInstitucion = idInstitucion;
        this.nombre = nombre;
        this.tipoGestion = tipoGestion;
        this.region = region;
        this.elegible = elegible;
    }

    public Integer getIdInstitucion() {
        return idInstitucion;
    }

    public void setIdInstitucion(Integer idInstitucion) {
        this.idInstitucion = idInstitucion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public boolean isElegible() {
        return elegible;
    }

    public void setElegible(boolean elegible) {
        this.elegible = elegible;
    }
}
