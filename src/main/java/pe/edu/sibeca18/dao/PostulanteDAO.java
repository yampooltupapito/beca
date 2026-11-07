package pe.edu.sibeca18.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import pe.edu.sibeca18.model.Postulante;

/**
 * Interfaz DAO para la entidad Postulante
 * (Sección 8: listar, insertar, actualizar, eliminar, buscarPorId, buscarPorDni).
 */
public interface PostulanteDAO {

    List<Postulante> listar();

    List<Postulante> listarConFiltros(Integer idModalidad, String estado, String dni);

    int insertar(Postulante postulante);

    int insertar(Connection conn, Postulante postulante) throws SQLException;

    boolean actualizar(Postulante postulante);

    boolean eliminar(int idPostulante);

    Postulante buscarPorId(int idPostulante);

    Postulante buscarPorDni(String dni);

    Postulante buscarPorIdUsuario(int idUsuario);

    boolean actualizarEstadoRevision(int idPostulante, String nuevoEstado, String observacion);

    boolean registrarPuntajeEnp(int idPostulante, BigDecimal puntajeEnp, String nuevoEstado);

    boolean actualizarEstado(Connection conn, int idPostulante, String nuevoEstado) throws SQLException;
}
