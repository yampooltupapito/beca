package pe.edu.sibeca18.dao;

import java.util.List;
import pe.edu.sibeca18.model.Modalidad;

/**
 * Interfaz DAO para la entidad Modalidad (Sección 8: listar, buscarPorId).
 */
public interface ModalidadDAO {

    List<Modalidad> listar();

    Modalidad buscarPorId(int idModalidad);
}
