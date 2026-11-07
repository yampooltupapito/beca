package pe.edu.sibeca18.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import pe.edu.sibeca18.dao.ProgramaDAO;
import pe.edu.sibeca18.exception.AccesoDatosException;
import pe.edu.sibeca18.model.ProgramaEstudio;
import pe.edu.sibeca18.util.ConexionDB;

/**
 * Implementación JDBC de ProgramaDAO (Sección 8: listar, insertar, actualizar, eliminar, buscarPorId).
 */
public class ProgramaDAOImpl implements ProgramaDAO {

    private static final String SQL_LISTAR =
            "SELECT pe.id_programa, pe.id_institucion, pe.institucion, pe.programa_estudios, "
            + "pe.tipo_gestion, pe.region, pe.vacantes, pe.activo, "
            + "COUNT(ps.id_postulacion) AS total_postulantes "
            + "FROM programa_estudio pe "
            + "LEFT JOIN postulacion_seleccion ps ON pe.id_programa = ps.id_programa "
            + "GROUP BY pe.id_programa, pe.id_institucion, pe.institucion, pe.programa_estudios, "
            + "pe.tipo_gestion, pe.region, pe.vacantes, pe.activo "
            + "ORDER BY pe.id_programa ASC";

    private static final String SQL_BUSCAR_POR_ID =
            "SELECT pe.id_programa, pe.id_institucion, pe.institucion, pe.programa_estudios, "
            + "pe.tipo_gestion, pe.region, pe.vacantes, pe.activo, "
            + "(SELECT COUNT(*) FROM postulacion_seleccion ps WHERE ps.id_programa = pe.id_programa) AS total_postulantes "
            + "FROM programa_estudio pe WHERE pe.id_programa = ?";

    private static final String SQL_INSERTAR =
            "INSERT INTO programa_estudio (id_institucion, institucion, programa_estudios, tipo_gestion, region, vacantes, activo) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE programa_estudio SET id_institucion = ?, institucion = ?, programa_estudios = ?, "
            + "tipo_gestion = ?, region = ?, vacantes = ?, activo = ? WHERE id_programa = ?";

    private static final String SQL_ELIMINAR =
            "DELETE FROM programa_estudio WHERE id_programa = ?";

    @Override
    public List<ProgramaEstudio> listar() {
        List<ProgramaEstudio> lista = new ArrayList<>();
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearPrograma(rs));
            }
            return lista;
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al listar programas de estudios.", ex);
        }
    }

    @Override
    public int insertar(ProgramaEstudio p) {
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
            if (p.getIdInstitucion() != null && p.getIdInstitucion() > 0) {
                ps.setInt(1, p.getIdInstitucion());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, p.getInstitucion().trim());
            ps.setString(3, p.getProgramaEstudios().trim());
            ps.setString(4, p.getTipoGestion());
            ps.setString(5, p.getRegion().trim());
            ps.setInt(6, p.getVacantes());
            ps.setInt(7, p.isActivo() ? 1 : 0);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    p.setIdPrograma(id);
                    return id;
                }
            }
            return 0;
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al registrar el programa de estudios.", ex);
        }
    }

    @Override
    public boolean actualizar(ProgramaEstudio p) {
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_ACTUALIZAR)) {
            if (p.getIdInstitucion() != null && p.getIdInstitucion() > 0) {
                ps.setInt(1, p.getIdInstitucion());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, p.getInstitucion().trim());
            ps.setString(3, p.getProgramaEstudios().trim());
            ps.setString(4, p.getTipoGestion());
            ps.setString(5, p.getRegion().trim());
            ps.setInt(6, p.getVacantes());
            ps.setInt(7, p.isActivo() ? 1 : 0);
            ps.setInt(8, p.getIdPrograma());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al actualizar el programa de estudios.", ex);
        }
    }

    @Override
    public boolean eliminar(int idPrograma) {
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_ELIMINAR)) {
            ps.setInt(1, idPrograma);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new AccesoDatosException("No se puede eliminar el programa porque cuenta con postulaciones registradas.", ex);
        }
    }

    @Override
    public ProgramaEstudio buscarPorId(int idPrograma) {
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_BUSCAR_POR_ID)) {
            ps.setInt(1, idPrograma);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearPrograma(rs);
                }
            }
            return null;
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al buscar programa por ID.", ex);
        }
    }

    private ProgramaEstudio mapearPrograma(ResultSet rs) throws SQLException {
        ProgramaEstudio p = new ProgramaEstudio();
        p.setIdPrograma(rs.getInt("id_programa"));
        int idInst = rs.getInt("id_institucion");
        if (!rs.wasNull()) {
            p.setIdInstitucion(idInst);
        }
        p.setInstitucion(rs.getString("institucion"));
        p.setProgramaEstudios(rs.getString("programa_estudios"));
        p.setTipoGestion(rs.getString("tipo_gestion"));
        p.setRegion(rs.getString("region"));
        p.setVacantes(rs.getInt("vacantes"));
        p.setActivo(rs.getInt("activo") == 1);
        p.setTotalPostulantes(rs.getInt("total_postulantes"));
        return p;
    }
}
