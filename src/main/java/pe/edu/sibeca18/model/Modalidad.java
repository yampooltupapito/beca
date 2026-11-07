package pe.edu.sibeca18.model;

import java.io.Serializable;

/**
 * Entidad Modalidad de Postulación a Beca 18 (9 modalidades, límites de edad RN-02 y puntajes mínimos RN-09).
 */
public class Modalidad implements Serializable {

    private Integer idModalidad;
    private String codigo;
    private String nombre;
    private String tipo; // ORDINARIA o ESPECIAL
    private Integer edadMaxima; // null si no tiene límite; de lo contrario la edad debe ser < edadMaxima
    private int puntajeMinimoEnp; // 84 Ordinaria, 66 Especial
    private String descripcion;

    public Modalidad() {
    }

    public Modalidad(Integer idModalidad, String codigo, String nombre, String tipo, Integer edadMaxima, int puntajeMinimoEnp) {
        this.idModalidad = idModalidad;
        this.codigo = codigo;
        this.nombre = nombre;
        this.tipo = tipo;
        this.edadMaxima = edadMaxima;
        this.puntajeMinimoEnp = puntajeMinimoEnp;
    }

    public Integer getIdModalidad() {
        return idModalidad;
    }

    public void setIdModalidad(Integer idModalidad) {
        this.idModalidad = idModalidad;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getEdadMaxima() {
        return edadMaxima;
    }

    public void setEdadMaxima(Integer edadMaxima) {
        this.edadMaxima = edadMaxima;
    }

    public int getPuntajeMinimoEnp() {
        return puntajeMinimoEnp;
    }

    public void setPuntajeMinimoEnp(int puntajeMinimoEnp) {
        this.puntajeMinimoEnp = puntajeMinimoEnp;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean esOrdinaria() {
        return "ORDINARIA".equalsIgnoreCase(this.tipo) || "Ordinaria".equalsIgnoreCase(this.nombre);
    }

    public String getEdadMaximaTexto() {
        if (edadMaxima == null) {
            return "Sin límite de edad";
        }
        return "Menor de " + edadMaxima + " años (hasta " + (edadMaxima - 1) + " cumplidos)";
    }
}
