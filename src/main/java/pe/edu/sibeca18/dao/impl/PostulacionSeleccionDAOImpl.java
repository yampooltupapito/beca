package pe.edu.sibeca18.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import pe.edu.sibeca18.dao.PostulacionSeleccionDAO;
import pe.edu.sibeca18.exception.AccesoDatosException;
import pe.edu.sibeca18.model.PostulacionSeleccion;
import pe.edu.sibeca18.util.ConexionDB;

/**
 * Implementación JDBC de PostulacionSeleccionDAO
 * (Sección 8: insertar, actualizar, eliminar, listar por programa, buscar por postulante).
 */
public class PostulacionSeleccionDAOImpl implements PostulacionSeleccionDAO {

    private static final String SELECT_BASE =
            "SELECT ps.id_postulacion, ps.id_postulante, ps.id_programa, ps.momento, "
            + "ps.fecha_hora, ps.orden_merito, ps.resultado, "
            + "p.dni AS post_dni, CONCAT(p.apellidos, ', ', p.nombres) AS post_nombre, "
            + "p.puntaje_enp AS post_puntaje, p.estado AS post_estado, "
            + "m.nombre AS mod_nombre, "
            + "pe.institucion AS prog_institucion, pe.programa_estudios AS prog_nombre, "
            + "pe.tipo_gestion AS prog_gestion, pe.region AS prog_region, pe.vacantes AS prog_vacantes "
            + "FROM postulacion_seleccion ps "
            + "INNER JOIN postulante p ON ps.id_postulante = p.id_postulante "
            + "INNER JOIN modalidad m ON p.id_modalidad = m.id_modalidad "
            + "INNER JOIN programa_estudio pe ON ps.id_programa = pe.id_programa ";

    private static final String SQL_INSERTAR =
            "INSERT INTO postulacion_seleccion (id_postulante, id_programa, momento, fecha_hora, resultado) "
            + "VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE postulacion_seleccion SET id_programa = ?, momento = ?, fecha_hora = ?, "
            + "orden_merito = NULL, resultado = 'PENDIENTE' WHERE id_postulacion = ?";

    private static final String SQL_ELIMINAR =
            "DELETE FROM postulacion_seleccion WHERE id_postulacion = ?";

    @Override
    public int insertar(PostulacionSeleccion postulacion) {
        try (Connection conn = ConexionDB.obtenerConexion()) {
            return insertar(conn, postulacion);
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al registrar la postulación a selección.", ex);
        }
    }

    @Override
    public int insertar(Connection conn, PostulacionSeleccion psel) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, psel.getIdPostulante());
            ps.setInt(2, psel.getIdPrograma());
            ps.setInt(3, psel.getMomento());
            LocalDateTime fh = psel.getFechaHora() != null ? psel.getFechaHora() : LocalDateTime.now();
            ps.setTimestamp(4, Timestamp.valueOf(fh));
            ps.setString(5, psel.getResultado() != null ? psel.getResultado() : "PENDIENTE");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    psel.setIdPostulacion(id);
                    return id;
                }
            }
        }
        throw new SQLException("No se pudo obtener el ID autogenerado para la postulación.");
    }

    @Override
    public boolean actualizar(PostulacionSeleccion psel) {
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_ACTUALIZAR)) {
            ps.setInt(1, psel.getIdPrograma());
            ps.setInt(2, psel.getMomento());
            LocalDateTime fh = psel.getFechaHora() != null ? psel.getFechaHora() : LocalDateTime.now();
            ps.setTimestamp(3, Timestamp.valueOf(fh));
            ps.setInt(4, psel.getIdPostulacion());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al actualizar la postulación a selección.", ex);
        }
    }

    @Override
    public boolean eliminar(int idPostulacion) {
        try (Connection conn = ConexionDB.obtenerConexion()) {
            return eliminar(conn, idPostulacion);
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al eliminar la postulación a selección.", ex);
        }
    }

    @Override
    public boolean eliminar(Connection conn, int idPostulacion) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_ELIMINAR)) {
            ps.setInt(1, idPostulacion);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<PostulacionSeleccion> listarPorPrograma(int idPrograma) {
        List<PostulacionSeleccion> lista = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE ps.id_programa = ? ORDER BY p.puntaje_enp DESC, ps.fecha_hora ASC";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPrograma);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearPostulacion(rs));
                }
            }
            return lista;
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al listar postulaciones por programa.", ex);
        }
    }

    @Override
    public List<PostulacionSeleccion> listarTodas() {
        List<PostulacionSeleccion> lista = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY ps.id_programa ASC, p.puntaje_enp DESC, ps.fecha_hora ASC";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearPostulacion(rs));
            }
            return lista;
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al listar todas las postulaciones.", ex);
        }
    }

    @Override
    public PostulacionSeleccion buscarPorPostulante(int idPostulante) {
        String sql = SELECT_BASE + "WHERE ps.id_postulante = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPostulante);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearPostulacion(rs);
                }
            }
            return null;
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al buscar postulación por postulante.", ex);
        }
    }

    @Override
    public PostulacionSeleccion buscarPorId(int idPostulacion) {
        String sql = SELECT_BASE + "WHERE ps.id_postulacion = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPostulacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearPostulacion(rs);
                }
            }
            return null;
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al buscar postulación por ID.", ex);
        }
    }

    @Override
    public boolean actualizarResultadoSeleccion(Connection conn, int idPostulacion, int ordenMerito, String resultado) throws SQLException {
        String sql = "UPDATE postulacion_seleccion SET orden_merito = ?, resultado = ? WHERE id_postulacion = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ordenMerito);
            ps.setString(2, resultado);
            ps.setInt(3, idPostulacion);
            return ps.executeUpdate() > 0;
        }
    }

    private PostulacionSeleccion mapearPostulacion(ResultSet rs) throws SQLException {
        PostulacionSeleccion ps = new PostulacionSeleccion();
        ps.setIdPostulacion(rs.getInt("id_postulacion"));
        ps.setIdPostulante(rs.getInt("id_postulante"));
        ps.setIdPrograma(rs.getInt("id_programa"));
        ps.setMomento(rs.getInt("momento"));
        Timestamp ts = rs.getTimestamp("fecha_hora");
        if (ts != null) {
            ps.setFechaHora(ts.toLocalDateTime());
        }
        int merito = rs.getInt("orden_merito");
        if (!rs.wasNull()) {
            ps.setOrdenMerito(merito);
        }
        ps.setResultado(rs.getString("resultado"));
        ps.setDniPostulante(rs.getString("post_dni"));
        ps.setNombrePostulante(rs.getString("post_nombre"));
        ps.setPuntajeEnp(rs.getBigDecimal("post_puntaje"));
        ps.setEstadoPostulante(rs.getString("post_estado"));
        ps.setModalidadNombre(rs.getString("mod_nombre"));
        ps.setInstitucionNombre(rs.getString("prog_institucion"));
        ps.setProgramaNombre(rs.getString("prog_nombre"));
        ps.setTipoGestionPrograma(rs.getString("prog_gestion"));
        ps.setRegionPrograma(rs.getString("prog_region"));
        ps.setVacantesPrograma(rs.getInt("prog_vacantes"));
        return ps;
    }
}
