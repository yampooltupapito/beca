package pe.edu.sibeca18.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import pe.edu.sibeca18.model.PostulacionSeleccion;

/**
 * Interfaz DAO para la entidad Postulación a Selección
 * (Sección 8: insertar, actualizar, eliminar, listar por programa, buscar por postulante).
 */
public interface PostulacionSeleccionDAO {

    int insertar(PostulacionSeleccion postulacion);

    int insertar(Connection conn, PostulacionSeleccion postulacion) throws SQLException;

    boolean actualizar(PostulacionSeleccion postulacion);

    boolean eliminar(int idPostulacion);

    boolean eliminar(Connection conn, int idPostulacion) throws SQLException;

    List<PostulacionSeleccion> listarPorPrograma(int idPrograma);

    List<PostulacionSeleccion> listarTodas();

    PostulacionSeleccion buscarPorPostulante(int idPostulante);

    PostulacionSeleccion buscarPorId(int idPostulacion);

    boolean actualizarResultadoSeleccion(Connection conn, int idPostulacion, int ordenMerito, String resultado) throws SQLException;
}
