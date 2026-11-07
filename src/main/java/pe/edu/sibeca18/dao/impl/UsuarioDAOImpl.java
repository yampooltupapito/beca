package pe.edu.sibeca18.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import pe.edu.sibeca18.dao.UsuarioDAO;
import pe.edu.sibeca18.exception.AccesoDatosException;
import pe.edu.sibeca18.model.Usuario;
import pe.edu.sibeca18.util.ConexionDB;
import pe.edu.sibeca18.util.PasswordUtil;

/**
 * Implementación JDBC de UsuarioDAO usando exclusivamente PreparedStatement, ResultSet y try-with-resources.
 */
public class UsuarioDAOImpl implements UsuarioDAO {

    private static final String SQL_BUSCAR_POR_USERNAME =
            "SELECT id_usuario, username, password_hash, rol, activo, fecha_creacion "
            + "FROM usuario WHERE username = ? AND activo = 1";

    private static final String SQL_BUSCAR_POR_ID =
            "SELECT id_usuario, username, password_hash, rol, activo, fecha_creacion "
            + "FROM usuario WHERE id_usuario = ?";

    private static final String SQL_INSERTAR =
            "INSERT INTO usuario (username, password_hash, rol, activo) VALUES (?, ?, ?, ?)";

    @Override
    public Usuario autenticar(String username, String passwordPlano) {
        if (username == null || passwordPlano == null) {
            return null;
        }
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_BUSCAR_POR_USERNAME)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = mapearUsuario(rs);
                    if (PasswordUtil.verificarPassword(passwordPlano, usuario.getPasswordHash())) {
                        return usuario;
                    }
                }
            }
            return null;
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al autenticar el usuario en la base de datos.", ex);
        }
    }

    @Override
    public int insertar(Usuario usuario) {
        try (Connection conn = ConexionDB.obtenerConexion()) {
            return insertar(conn, usuario);
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al insertar el usuario.", ex);
        }
    }

    @Override
    public int insertar(Connection conn, Usuario usuario) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getUsername().trim());
            ps.setString(2, usuario.getPasswordHash());
            ps.setString(3, usuario.getRol());
            ps.setInt(4, usuario.isActivo() ? 1 : 0);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    usuario.setIdUsuario(idGenerado);
                    return idGenerado;
                }
            }
        }
        throw new SQLException("No se pudo obtener el ID autogenerado para el usuario.");
    }

    @Override
    public Usuario buscarPorId(int idUsuario) {
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_BUSCAR_POR_ID)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
            return null;
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al buscar usuario por ID.", ex);
        }
    }

    @Override
    public Usuario buscarPorUsername(String username) {
        if (username == null) {
            return null;
        }
        String sql = "SELECT id_usuario, username, password_hash, rol, activo, fecha_creacion FROM usuario WHERE username = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
            return null;
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al buscar usuario por nombre de usuario.", ex);
        }
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRol(rs.getString("rol"));
        u.setActivo(rs.getInt("activo") == 1);
        Timestamp ts = rs.getTimestamp("fecha_creacion");
        if (ts != null) {
            u.setFechaCreacion(ts.toLocalDateTime());
        }
        return u;
    }
}
