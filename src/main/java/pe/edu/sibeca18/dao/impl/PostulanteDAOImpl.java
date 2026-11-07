package pe.edu.sibeca18.dao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import pe.edu.sibeca18.dao.PostulanteDAO;
import pe.edu.sibeca18.exception.AccesoDatosException;
import pe.edu.sibeca18.model.Modalidad;
import pe.edu.sibeca18.model.Postulante;
import pe.edu.sibeca18.util.ConexionDB;

/**
 * Implementación JDBC de PostulanteDAO usando exclusivamente PreparedStatement y try-with-resources.
 */
public class PostulanteDAOImpl implements PostulanteDAO {

    private static final String SELECT_BASE =
            "SELECT p.id_postulante, p.id_usuario, u.username, p.dni, p.nombres, p.apellidos, "
            + "p.fecha_nacimiento, p.edad_corte, p.nacionalidad, p.correo, p.celular, p.region, "
            + "p.colegio, p.situacion_estudios, p.anio_egreso, p.tercio_superior, "
            + "p.clasificacion_socioeconomica, p.discapacidad_acreditada, p.id_modalidad, "
            + "p.estado, p.observacion_revision, p.puntaje_enp, p.fecha_inscripcion, "
            + "m.codigo AS mod_codigo, m.nombre AS mod_nombre, m.tipo AS mod_tipo, "
            + "m.edad_maxima AS mod_edad_maxima, m.puntaje_minimo_enp AS mod_puntaje_minimo "
            + "FROM postulante p "
            + "INNER JOIN usuario u ON p.id_usuario = u.id_usuario "
            + "INNER JOIN modalidad m ON p.id_modalidad = m.id_modalidad ";

    private static final String SQL_INSERTAR =
            "INSERT INTO postulante (id_usuario, dni, nombres, apellidos, fecha_nacimiento, edad_corte, "
            + "nacionalidad, correo, celular, region, colegio, situacion_estudios, anio_egreso, "
            + "tercio_superior, clasificacion_socioeconomica, discapacidad_acreditada, id_modalidad, estado) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE postulante SET nombres = ?, apellidos = ?, fecha_nacimiento = ?, edad_corte = ?, "
            + "nacionalidad = ?, correo = ?, celular = ?, region = ?, colegio = ?, "
            + "situacion_estudios = ?, anio_egreso = ?, tercio_superior = ?, "
            + "clasificacion_socioeconomica = ?, discapacidad_acreditada = ?, id_modalidad = ? "
            + "WHERE id_postulante = ?";

    @Override
    public List<Postulante> listar() {
        List<Postulante> lista = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY p.id_postulante ASC";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearPostulante(rs));
            }
            return lista;
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al listar postulantes.", ex);
        }
    }

    @Override
    public List<Postulante> listarConFiltros(Integer idModalidad, String estado, String dni) {
        StringBuilder sql = new StringBuilder(SELECT_BASE);
        sql.append("WHERE 1=1 ");
        List<Object> parametros = new ArrayList<>();

        if (idModalidad != null && idModalidad > 0) {
            sql.append("AND p.id_modalidad = ? ");
            parametros.add(idModalidad);
        }
        if (estado != null && !estado.isBlank() && !"TODOS".equalsIgnoreCase(estado)) {
            sql.append("AND p.estado = ? ");
            parametros.add(estado.trim());
        }
        if (dni != null && !dni.isBlank()) {
            sql.append("AND p.dni LIKE ? ");
            parametros.add("%" + dni.trim() + "%");
        }
        sql.append("ORDER BY p.id_postulante ASC");

        List<Postulante> lista = new ArrayList<>();
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearPostulante(rs));
                }
            }
            return lista;
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al filtrar postulantes.", ex);
        }
    }

    @Override
    public int insertar(Postulante postulante) {
        try (Connection conn = ConexionDB.obtenerConexion()) {
            return insertar(conn, postulante);
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al insertar el postulante.", ex);
        }
    }

    @Override
    public int insertar(Connection conn, Postulante p) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getIdUsuario());
            ps.setString(2, p.getDni().trim());
            ps.setString(3, p.getNombres().trim());
            ps.setString(4, p.getApellidos().trim());
            ps.setDate(5, Date.valueOf(p.getFechaNacimiento()));
            ps.setInt(6, p.getEdadCorte());
            ps.setString(7, p.getNacionalidad().trim().toUpperCase());
            ps.setString(8, p.getCorreo().trim());
            ps.setString(9, p.getCelular().trim());
            ps.setString(10, p.getRegion().trim());
            ps.setString(11, p.getColegio().trim());
            ps.setString(12, p.getSituacionEstudios());
            ps.setInt(13, p.getAnioEgreso());
            ps.setInt(14, p.isTercioSuperior() ? 1 : 0);
            ps.setString(15, p.getClasificacionSocioeconomica());
            ps.setInt(16, p.isDiscapacidadAcreditada() ? 1 : 0);
            ps.setInt(17, p.getIdModalidad());
            ps.setString(18, p.getEstado() != null ? p.getEstado() : "INSCRITO");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    p.setIdPostulante(idGenerado);
                    return idGenerado;
                }
            }
        }
        throw new SQLException("No se pudo obtener el ID autogenerado para el postulante.");
    }

    @Override
    public boolean actualizar(Postulante p) {
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_ACTUALIZAR)) {
            ps.setString(1, p.getNombres().trim());
            ps.setString(2, p.getApellidos().trim());
            ps.setDate(3, Date.valueOf(p.getFechaNacimiento()));
            ps.setInt(4, p.getEdadCorte());
            ps.setString(5, p.getNacionalidad().trim().toUpperCase());
            ps.setString(6, p.getCorreo().trim());
            ps.setString(7, p.getCelular().trim());
            ps.setString(8, p.getRegion().trim());
            ps.setString(9, p.getColegio().trim());
            ps.setString(10, p.getSituacionEstudios());
            ps.setInt(11, p.getAnioEgreso());
            ps.setInt(12, p.isTercioSuperior() ? 1 : 0);
            ps.setString(13, p.getClasificacionSocioeconomica());
            ps.setInt(14, p.isDiscapacidadAcreditada() ? 1 : 0);
            ps.setInt(15, p.getIdModalidad());
            ps.setInt(16, p.getIdPostulante());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al actualizar los datos del postulante.", ex);
        }
    }

    @Override
    public boolean eliminar(int idPostulante) {
        String sqlBuscarUsuario = "SELECT id_usuario FROM postulante WHERE id_postulante = ?";
        String sqlEliminarPostulante = "DELETE FROM postulante WHERE id_postulante = ?";
        String sqlEliminarUsuario = "DELETE FROM usuario WHERE id_usuario = ?";

        try (Connection conn = ConexionDB.obtenerConexion()) {
            conn.setAutoCommit(false);
            try {
                Integer idUsuario = null;
                try (PreparedStatement psBuscar = conn.prepareStatement(sqlBuscarUsuario)) {
                    psBuscar.setInt(1, idPostulante);
                    try (ResultSet rs = psBuscar.executeQuery()) {
                        if (rs.next()) {
                            idUsuario = rs.getInt("id_usuario");
                        }
                    }
                }
                if (idUsuario == null) {
                    conn.rollback();
                    return false;
                }
                try (PreparedStatement psDelPost = conn.prepareStatement(sqlEliminarPostulante)) {
                    psDelPost.setInt(1, idPostulante);
                    psDelPost.executeUpdate();
                }
                try (PreparedStatement psDelUsu = conn.prepareStatement(sqlEliminarUsuario)) {
                    psDelUsu.setInt(1, idUsuario);
                    psDelUsu.executeUpdate();
                }
                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al eliminar la inscripción del postulante.", ex);
        }
    }

    @Override
    public Postulante buscarPorId(int idPostulante) {
        String sql = SELECT_BASE + "WHERE p.id_postulante = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPostulante);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearPostulante(rs);
                }
            }
            return null;
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al buscar postulante por ID.", ex);
        }
    }

    @Override
    public Postulante buscarPorDni(String dni) {
        if (dni == null) {
            return null;
        }
        String sql = SELECT_BASE + "WHERE p.dni = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dni.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearPostulante(rs);
                }
            }
            return null;
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al buscar postulante por DNI.", ex);
        }
    }

    @Override
    public Postulante buscarPorIdUsuario(int idUsuario) {
        String sql = SELECT_BASE + "WHERE p.id_usuario = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearPostulante(rs);
                }
            }
            return null;
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al buscar postulante por ID de usuario.", ex);
        }
    }

    @Override
    public boolean actualizarEstadoRevision(int idPostulante, String nuevoEstado, String observacion) {
        String sql = "UPDATE postulante SET estado = ?, observacion_revision = ? WHERE id_postulante = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            if (observacion == null || observacion.isBlank()) {
                ps.setNull(2, Types.VARCHAR);
            } else {
                ps.setString(2, observacion.trim());
            }
            ps.setInt(3, idPostulante);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al actualizar el estado de revisión del postulante.", ex);
        }
    }

    @Override
    public boolean registrarPuntajeEnp(int idPostulante, BigDecimal puntajeEnp, String nuevoEstado) {
        String sql = "UPDATE postulante SET puntaje_enp = ?, estado = ? WHERE id_postulante = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, puntajeEnp);
            ps.setString(2, nuevoEstado);
            ps.setInt(3, idPostulante);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al registrar el puntaje del ENP.", ex);
        }
    }

    @Override
    public boolean actualizarEstado(Connection conn, int idPostulante, String nuevoEstado) throws SQLException {
        String sql = "UPDATE postulante SET estado = ? WHERE id_postulante = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idPostulante);
            return ps.executeUpdate() > 0;
        }
    }

    private Postulante mapearPostulante(ResultSet rs) throws SQLException {
        Postulante p = new Postulante();
        p.setIdPostulante(rs.getInt("id_postulante"));
        p.setIdUsuario(rs.getInt("id_usuario"));
        p.setUsername(rs.getString("username"));
        p.setDni(rs.getString("dni"));
        p.setNombres(rs.getString("nombres"));
        p.setApellidos(rs.getString("apellidos"));
        Date fn = rs.getDate("fecha_nacimiento");
        if (fn != null) {
            p.setFechaNacimiento(fn.toLocalDate());
        }
        p.setEdadCorte(rs.getInt("edad_corte"));
        p.setNacionalidad(rs.getString("nacionalidad"));
        p.setCorreo(rs.getString("correo"));
        p.setCelular(rs.getString("celular"));
        p.setRegion(rs.getString("region"));
        p.setColegio(rs.getString("colegio"));
        p.setSituacionEstudios(rs.getString("situacion_estudios"));
        p.setAnioEgreso(rs.getInt("anio_egreso"));
        p.setTercioSuperior(rs.getInt("tercio_superior") == 1);
        p.setClasificacionSocioeconomica(rs.getString("clasificacion_socioeconomica"));
        p.setDiscapacidadAcreditada(rs.getInt("discapacidad_acreditada") == 1);
        p.setIdModalidad(rs.getInt("id_modalidad"));
        p.setEstado(rs.getString("estado"));
        p.setObservacionRevision(rs.getString("observacion_revision"));
        p.setPuntajeEnp(rs.getBigDecimal("puntaje_enp"));
        Timestamp ts = rs.getTimestamp("fecha_inscripcion");
        if (ts != null) {
            p.setFechaInscripcion(ts.toLocalDateTime());
        }

        Modalidad m = new Modalidad();
        m.setIdModalidad(rs.getInt("id_modalidad"));
        m.setCodigo(rs.getString("mod_codigo"));
        m.setNombre(rs.getString("mod_nombre"));
        m.setTipo(rs.getString("mod_tipo"));
        int edadMax = rs.getInt("mod_edad_maxima");
        if (rs.wasNull()) {
            m.setEdadMaxima(null);
        } else {
            m.setEdadMaxima(edadMax);
        }
        m.setPuntajeMinimoEnp(rs.getInt("mod_puntaje_minimo"));
        p.setModalidad(m);
        return p;
    }
}
