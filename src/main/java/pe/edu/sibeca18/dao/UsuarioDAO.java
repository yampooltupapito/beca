package pe.edu.sibeca18.dao;

import java.sql.Connection;
import java.sql.SQLException;
import pe.edu.sibeca18.model.Usuario;

/**
 * Interfaz DAO para la entidad Usuario (Sección 8: autenticar, insertar, buscarPorId).
 */
public interface UsuarioDAO {

    Usuario autenticar(String username, String passwordPlano);

    int insertar(Usuario usuario);

    int insertar(Connection conn, Usuario usuario) throws SQLException;

    Usuario buscarPorId(int idUsuario);

    Usuario buscarPorUsername(String username);
}
