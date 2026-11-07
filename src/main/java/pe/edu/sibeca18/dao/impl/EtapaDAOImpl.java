package pe.edu.sibeca18.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import pe.edu.sibeca18.dao.EtapaDAO;
import pe.edu.sibeca18.exception.AccesoDatosException;
import pe.edu.sibeca18.model.Etapa;
import pe.edu.sibeca18.util.ConexionDB;
import pe.edu.sibeca18.util.FormatoUtil;

/**
 * Implementación JDBC de EtapaDAO (cronograma parametrizable y fecha de corte RN-02, RF-12, RN-13).
 */
public class EtapaDAOImpl implements EtapaDAO {

    private static final String SQL_LISTAR =
            "SELECT id_etapa, orden, codigo, nombre, fecha_referencia, fecha_inicio, fecha_fin, publicada, descripcion "
            + "FROM etapa ORDER BY orden ASC";

    private static final String SQL_BUSCAR_POR_ID =
            "SELECT id_etapa, orden, codigo, nombre, fecha_referencia, fecha_inicio, fecha_fin, publicada, descripcion "
            + "FROM etapa WHERE id_etapa = ?";

    private static final String SQL_BUSCAR_POR_ORDEN =
            "SELECT id_etapa, orden, codigo, nombre, fecha_referencia, fecha_inicio, fecha_fin, publicada, descripcion "
            + "FROM etapa WHERE orden = ?";

    private static final String SQL_ACTUALIZAR =
            "UPDATE etapa SET nombre = ?, fecha_inicio = ?, fecha_fin = ?, publicada = ?, descripcion = ? "
            + "WHERE id_etapa = ?";

    @Override
    public List<Etapa> listar() {
        List<Etapa> etapas = new ArrayList<>();
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                etapas.add(mapearEtapa(rs));
            }
            return etapas;
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al listar las etapas del cronograma.", ex);
        }
    }

    @Override
    public Etapa buscarPorId(int idEtapa) {
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_BUSCAR_POR_ID)) {
            ps.setInt(1, idEtapa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearEtapa(rs);
                }
            }
            return null;
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al buscar etapa por ID.", ex);
        }
    }

    @Override
    public Etapa buscarPorOrden(int orden) {
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_BUSCAR_POR_ORDEN)) {
            ps.setInt(1, orden);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearEtapa(rs);
                }
            }
            return null;
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al buscar etapa por número de orden.", ex);
        }
    }

    @Override
    public boolean actualizar(Etapa etapa) {
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_ACTUALIZAR)) {
            ps.setString(1, etapa.getNombre());
            ps.setDate(2, Date.valueOf(etapa.getFechaInicio()));
            ps.setDate(3, Date.valueOf(etapa.getFechaFin()));
            ps.setInt(4, etapa.isPublicada() ? 1 : 0);
            ps.setString(5, etapa.getDescripcion());
            ps.setInt(6, etapa.getIdEtapa());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al actualizar la etapa del cronograma.", ex);
        }
    }

    @Override
    public LocalDate obtenerFechaCorteEdad() {
        String sql = "SELECT valor FROM parametro_sistema WHERE clave = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "FECHA_CORTE_EDAD");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LocalDate parseada = FormatoUtil.parsearFechaIso(rs.getString("valor"));
                    if (parseada != null) {
                        return parseada;
                    }
                }
            }
        } catch (SQLException ignored) {
        }
        return FormatoUtil.FECHA_CORTE_DEFECTO;
    }

    @Override
    public boolean actualizarFechaCorteEdad(LocalDate nuevaFechaCorte) {
        if (nuevaFechaCorte == null) {
            return false;
        }
        String sql = "UPDATE parametro_sistema SET valor = ? WHERE clave = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevaFechaCorte.toString());
            ps.setString(2, "FECHA_CORTE_EDAD");
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al actualizar la fecha de corte parametrizable.", ex);
        }
    }

    private Etapa mapearEtapa(ResultSet rs) throws SQLException {
        Etapa e = new Etapa();
        e.setIdEtapa(rs.getInt("id_etapa"));
        e.setOrden(rs.getInt("orden"));
        e.setCodigo(rs.getString("codigo"));
        e.setNombre(rs.getString("nombre"));
        e.setFechaReferencia(rs.getString("fecha_referencia"));
        Date fi = rs.getDate("fecha_inicio");
        if (fi != null) {
            e.setFechaInicio(fi.toLocalDate());
        }
        Date ff = rs.getDate("fecha_fin");
        if (ff != null) {
            e.setFechaFin(ff.toLocalDate());
        }
        e.setPublicada(rs.getInt("publicada") == 1);
        e.setDescripcion(rs.getString("descripcion"));
        return e;
    }
}
