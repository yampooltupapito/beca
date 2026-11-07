package pe.edu.sibeca18.service;

import pe.edu.sibeca18.dao.UsuarioDAO;
import pe.edu.sibeca18.dao.impl.UsuarioDAOImpl;
import pe.edu.sibeca18.exception.ReglaNegocioException;
import pe.edu.sibeca18.model.Usuario;
import pe.edu.sibeca18.util.BitacoraUtil;
import pe.edu.sibeca18.util.ValidadorUtil;

/**
 * Servicio de autenticación y seguridad por roles (RF-01, RF-02, RF-16).
 */
public class AuthService {

    private final UsuarioDAO usuarioDAO;

    public AuthService() {
        this.usuarioDAO = new UsuarioDAOImpl();
    }

    public AuthService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * Autentica un usuario contra MySQL a través de la capa DAO y registra el evento en la bitácora.
     */
    public Usuario autenticar(String username, String passwordPlano) throws ReglaNegocioException {
        if (ValidadorUtil.esVacio(username) || ValidadorUtil.esVacio(passwordPlano)) {
            BitacoraUtil.registrarEvento("LOGIN_FALLIDO", username, "Intento de inicio de sesión con campos vacíos.");
            throw new ReglaNegocioException("Debe ingresar su nombre de usuario y contraseña.");
        }

        Usuario usuario = usuarioDAO.autenticar(username.trim(), passwordPlano);
        if (usuario == null) {
            BitacoraUtil.registrarEvento("LOGIN_FALLIDO", username.trim(),
                    "Credenciales inválidas o cuenta inactiva.");
            throw new ReglaNegocioException("Usuario o contraseña incorrectos. Verifique sus credenciales.");
        }

        BitacoraUtil.registrarEvento("LOGIN_EXITOSO", usuario.getUsername(),
                "Inicio de sesión exitoso con rol " + usuario.getRol() + ".");
        return usuario;
    }
}
