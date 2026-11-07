package pe.edu.sibeca18.dao;

import java.util.List;
import pe.edu.sibeca18.model.ProgramaEstudio;

/**
 * Interfaz DAO para la entidad Programa de Estudios
 * (Sección 8: listar, insertar, actualizar, eliminar, buscarPorId).
 */
public interface ProgramaDAO {

    List<ProgramaEstudio> listar();

    int insertar(ProgramaEstudio programa);

    boolean actualizar(ProgramaEstudio programa);

    boolean eliminar(int idPrograma);

    ProgramaEstudio buscarPorId(int idPrograma);
}
