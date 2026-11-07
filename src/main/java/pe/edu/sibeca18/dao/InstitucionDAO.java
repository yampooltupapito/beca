package pe.edu.sibeca18.dao;

import java.util.List;
import pe.edu.sibeca18.model.Institucion;

/**
 * Interfaz DAO para la entidad Institución (RF-13: CRUD de instituciones elegibles).
 */
public interface InstitucionDAO {

    List<Institucion> listar();

    int insertar(Institucion institucion);

    boolean actualizar(Institucion institucion);

    boolean eliminar(int idInstitucion);

    Institucion buscarPorId(int idInstitucion);
}
