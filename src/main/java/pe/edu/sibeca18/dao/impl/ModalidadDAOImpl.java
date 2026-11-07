package pe.edu.sibeca18.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pe.edu.sibeca18.dao.ModalidadDAO;
import pe.edu.sibeca18.exception.AccesoDatosException;
import pe.edu.sibeca18.model.Modalidad;
import pe.edu.sibeca18.util.ConexionDB;

/**
 * Implementación JDBC de ModalidadDAO usando PreparedStatement y try-with-resources.
 */
public class ModalidadDAOImpl implements ModalidadDAO {

    private static final String SQL_LISTAR =
            "SELECT id_modalidad, codigo, nombre, tipo, edad_maxima, puntaje_minimo_enp, descripcion "
            + "FROM modalidad ORDER BY id_modalidad ASC";

    private static final String SQL_BUSCAR_POR_ID =
            "SELECT id_modalidad, codigo, nombre, tipo, edad_maxima, puntaje_minimo_enp, descripcion "
            + "FROM modalidad WHERE id_modalidad = ?";

    @Override
    public List<Modalidad> listar() {
        List<Modalidad> lista = new ArrayList<>();
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearModalidad(rs));
            }
            return lista;
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al listar las modalidades de Beca 18.", ex);
        }
    }

    @Override
    public Modalidad buscarPorId(int idModalidad) {
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_BUSCAR_POR_ID)) {
            ps.setInt(1, idModalidad);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearModalidad(rs);
                }
            }
            return null;
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al buscar la modalidad por ID.", ex);
        }
    }

    private Modalidad mapearModalidad(ResultSet rs) throws SQLException {
        Modalidad m = new Modalidad();
        m.setIdModalidad(rs.getInt("id_modalidad"));
        m.setCodigo(rs.getString("codigo"));
        m.setNombre(rs.getString("nombre"));
        m.setTipo(rs.getString("tipo"));
        int edadMax = rs.getInt("edad_maxima");
        if (rs.wasNull()) {
            m.setEdadMaxima(null);
        } else {
            m.setEdadMaxima(edadMax);
        }
        m.setPuntajeMinimoEnp(rs.getInt("puntaje_minimo_enp"));
        m.setDescripcion(rs.getString("descripcion"));
        return m;
    }
}
