package pe.edu.sibeca18.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import pe.edu.sibeca18.model.Usuario;
import pe.edu.sibeca18.util.BitacoraUtil;

/**
 * Filtro de seguridad para control de sesiones HTTP y autorización por roles (RF-02, RN-14, CP-03, CP-04, CP-17).
 * Protege todas las rutas de /admin/* y /postulante/*.
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = {"/admin/*", "/postulante/*"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        req.setCharacterEncoding("UTF-8");
        res.setCharacterEncoding("UTF-8");

        // Impedir caché del navegador en páginas protegidas para cumplir CP-17 al cerrar sesión
        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setDateHeader("Expires", 0);

        HttpSession session = req.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuarioAutenticado") : null;

        String contextPath = req.getContextPath();
        String path = req.getRequestURI().substring(contextPath.length());

        // CP-03 / CP-17: Sin sesión activa -> redirigir al inicio de sesión
        if (usuario == null) {
            BitacoraUtil.registrarEvento("ACCESO_SIN_SESION", "ANONIMO",
                    "Intento de acceso directo sin sesión a ruta protegida: " + path);
            String msg = URLEncoder.encode("Debe iniciar sesión para acceder a la página solicitada.", StandardCharsets.UTF_8);
            res.sendRedirect(contextPath + "/auth?accion=login&error=" + msg);
            return;
        }

        // CP-04 / RN-14: Verificar correspondencia estricta de rol según la ruta
        if (path.startsWith("/admin") && !usuario.esAdministrador()) {
            BitacoraUtil.registrarEvento("ACCESO_DENEGADO", usuario.getUsername(),
                    "Usuario con rol " + usuario.getRol() + " intentó acceder a ruta administrativa: " + path);
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            req.setAttribute("mensajeDenegado",
                    "Acceso denegado (RN-14): Usted ha iniciado sesión con el rol " + usuario.getRol()
                    + " y no tiene autorización para ingresar al Módulo de Administración.");
            req.getRequestDispatcher("/WEB-INF/views/shared/acceso-denegado.jsp").forward(req, res);
            return;
        }

        if (path.startsWith("/postulante") && !usuario.esPostulante()) {
            BitacoraUtil.registrarEvento("ACCESO_DENEGADO", usuario.getUsername(),
                    "Usuario con rol " + usuario.getRol() + " intentó acceder a ruta de postulante: " + path);
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            req.setAttribute("mensajeDenegado",
                    "Acceso denegado (RN-14): Esta sección pertenece exclusivamente al Módulo del Postulante.");
            req.getRequestDispatcher("/WEB-INF/views/shared/acceso-denegado.jsp").forward(req, res);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
