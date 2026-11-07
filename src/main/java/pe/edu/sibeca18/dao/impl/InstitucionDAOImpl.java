package pe.edu.sibeca18.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import pe.edu.sibeca18.dao.InstitucionDAO;
import pe.edu.sibeca18.exception.AccesoDatosException;
import pe.edu.sibeca18.model.Institucion;
import pe.edu.sibeca18.util.ConexionDB;

/**
 * Implementación JDBC de InstitucionDAO usando PreparedStatement y try-with-resources.
 */
public class InstitucionDAOImpl implements InstitucionDAO {

    private static final String SQL_LISTAR =
            "SELECT id_institucion, nombre, tipo_gestion, region, elegible "
            + "FROM institucion ORDER BY nombre ASC";

    private static final String SQL_BUSCAR_POR_ID =
            "SELECT id_institucion, nombre, tipo_gestion, region, elegible "
            + "FROM institucion WHERE id_institucion = ?";

    private static final String SQL_INSERTAR =
            "INSERT INTO institucion (nombre, tipo_gestion, region, elegible) VALUES (?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE institucion SET nombre = ?, tipo_gestion = ?, region = ?, elegible = ? "
            + "WHERE id_institucion = ?";

    private static final String SQL_ELIMINAR =
            "DELETE FROM institucion WHERE id_institucion = ?";

    @Override
    public List<Institucion> listar() {
        List<Institucion> lista = new ArrayList<>();
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearInstitucion(rs));
            }
            return lista;
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al listar instituciones.", ex);
        }
    }

    @Override
    public int insertar(Institucion i) {
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, i.getNombre().trim());
            ps.setString(2, i.getTipoGestion());
            ps.setString(3, i.getRegion().trim());
            ps.setInt(4, i.isElegible() ? 1 : 0);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    i.setIdInstitucion(id);
                    return id;
                }
            }
            return 0;
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al insertar la institución.", ex);
        }
    }

    @Override
    public boolean actualizar(Institucion i) {
        String sqlSyncProgramas = "UPDATE programa_estudio SET institucion = ?, tipo_gestion = ?, region = ? WHERE id_institucion = ?";
        try (Connection conn = ConexionDB.obtenerConexion()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(SQL_ACTUALIZAR);
                 PreparedStatement psSync = conn.prepareStatement(sqlSyncProgramas)) {
                ps.setString(1, i.getNombre().trim());
                ps.setString(2, i.getTipoGestion());
                ps.setString(3, i.getRegion().trim());
                ps.setInt(4, i.isElegible() ? 1 : 0);
                ps.setInt(5, i.getIdInstitucion());
                int filas = ps.executeUpdate();

                psSync.setString(1, i.getNombre().trim());
                psSync.setString(2, i.getTipoGestion());
                psSync.setString(3, i.getRegion().trim());
                psSync.setInt(4, i.getIdInstitucion());
                psSync.executeUpdate();

                conn.commit();
                return filas > 0;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al actualizar la institución.", ex);
        }
    }

    @Override
    public boolean eliminar(int idInstitucion) {
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_ELIMINAR)) {
            ps.setInt(1, idInstitucion);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new AccesoDatosException("No se puede eliminar la institución porque tiene programas asociados.", ex);
        }
    }

    @Override
    public Institucion buscarPorId(int idInstitucion) {
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_BUSCAR_POR_ID)) {
            ps.setInt(1, idInstitucion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearInstitucion(rs);
                }
            }
            return null;
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al buscar institución por ID.", ex);
        }
    }

    private Institucion mapearInstitucion(ResultSet rs) throws SQLException {
        Institucion i = new Institucion();
        i.setIdInstitucion(rs.getInt("id_institucion"));
        i.setNombre(rs.getString("nombre"));
        i.setTipoGestion(rs.getString("tipo_gestion"));
        i.setRegion(rs.getString("region"));
        i.setElegible(rs.getInt("elegible") == 1);
        return i;
    }
}
