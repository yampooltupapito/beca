package pe.edu.sibeca18.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import pe.edu.sibeca18.exception.ReglaNegocioException;
import pe.edu.sibeca18.model.Postulante;
import pe.edu.sibeca18.model.Usuario;
import pe.edu.sibeca18.service.AuthService;
import pe.edu.sibeca18.service.PostulanteService;
import pe.edu.sibeca18.util.BitacoraUtil;

/**
 * Controlador MVC para inicio y cierre de sesión con HttpSession (RF-01, RF-02, CP-01, CP-02, CP-17).
 */
@WebServlet(name = "AuthServlet", urlPatterns = {"/auth"})
public class AuthServlet extends HttpServlet {

    private final AuthService authService = new AuthService();
    private final PostulanteService postulanteService = new PostulanteService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accion = req.getParameter("accion");
        if ("logout".equalsIgnoreCase(accion)) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                Usuario u = (Usuario) session.getAttribute("usuarioAutenticado");
                if (u != null) {
                    BitacoraUtil.registrarEvento("LOGOUT", u.getUsername(), "Cierre de sesión e invalidación de HttpSession.");
                }
                session.invalidate();
            }
            String msg = URLEncoder.encode("Ha cerrado su sesión correctamente.", StandardCharsets.UTF_8);
            resp.sendRedirect(req.getContextPath() + "/auth?accion=login&exito=" + msg);
            return;
        }

        // Si ya tiene sesión activa y pide el login, redirigir a su panel correspondiente
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("usuarioAutenticado") != null) {
            Usuario actual = (Usuario) session.getAttribute("usuarioAutenticado");
            if (actual.esAdministrador()) {
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            } else {
                resp.sendRedirect(req.getContextPath() + "/postulante/panel?vista=estado");
            }
            return;
        }

        if (req.getParameter("error") != null) {
            req.setAttribute("error", req.getParameter("error"));
        }
        if (req.getParameter("exito") != null) {
            req.setAttribute("exito", req.getParameter("exito"));
        }

        req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        try {
            Usuario usuario = authService.autenticar(username, password);

            // Credenciales válidas: crear sesión HTTP y almacenar usuario (RF-01, CP-01)
            HttpSession session = req.getSession(true);
            session.setAttribute("usuarioAutenticado", usuario);

            if (usuario.esPostulante()) {
                Postulante postulante = postulanteService.buscarPorIdUsuario(usuario.getIdUsuario());
                session.setAttribute("postulanteActual", postulante);
                resp.sendRedirect(req.getContextPath() + "/postulante/panel?vista=estado");
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            }

        } catch (ReglaNegocioException ex) {
            // CP-02: Si las credenciales son inválidas, se muestra mensaje y NO se crea sesión
            req.setAttribute("error", ex.getMessage());
            req.setAttribute("usernamePrevio", username);
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
        } catch (Exception ex) {
            req.setAttribute("error", "Ocurrió un error al verificar sus credenciales. Verifique la conexión a la base de datos.");
            req.setAttribute("usernamePrevio", username);
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
        }
    }
}
