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
import java.util.List;
import pe.edu.sibeca18.exception.ReglaNegocioException;
import pe.edu.sibeca18.model.Etapa;
import pe.edu.sibeca18.model.PostulacionSeleccion;
import pe.edu.sibeca18.model.Postulante;
import pe.edu.sibeca18.model.Usuario;
import pe.edu.sibeca18.service.CatalogoService;
import pe.edu.sibeca18.service.CronogramaService;
import pe.edu.sibeca18.service.PostulanteService;
import pe.edu.sibeca18.service.SeleccionService;
import pe.edu.sibeca18.util.FormatoUtil;

/**
 * Controlador MVC del Módulo del Postulante (Beneficiario):
 * - RF-05: Edición de inscripción reutilizando el mismo JSP de registro (con RN-07 / CP-10)
 * - RF-06: Mi estado y línea de tiempo del proceso
 * - RF-07: Mis resultados condicionados a publicación de etapas (RN-13 / CP-18)
 * - RF-08: Postulación a selección reutilizando el mismo JSP para registrar y editar (RN-10, RN-11 / CP-14)
 */
@WebServlet(name = "PostulanteServlet", urlPatterns = {"/postulante/panel"})
public class PostulanteServlet extends HttpServlet {

    private final PostulanteService postulanteService = new PostulanteService();
    private final CronogramaService cronogramaService = new CronogramaService();
    private final CatalogoService catalogoService = new CatalogoService();
    private final SeleccionService seleccionService = new SeleccionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute("usuarioAutenticado");
        Postulante postulante = postulanteService.buscarPorIdUsuario(usuario.getIdUsuario());
        session.setAttribute("postulanteActual", postulante);

        if (req.getParameter("exito") != null) {
            req.setAttribute("exito", req.getParameter("exito"));
        }
        if (req.getParameter("error") != null) {
            req.setAttribute("error", req.getParameter("error"));
        }

        String vista = req.getParameter("vista");
        if (vista == null || vista.isBlank()) {
            vista = "estado";
        }

        List<Etapa> etapas = cronogramaService.listarEtapas();
        req.setAttribute("postulante", postulante);
        req.setAttribute("etapas", etapas);
        req.setAttribute("fechaCorteFormateada", FormatoUtil.formatearFecha(postulanteService.obtenerFechaCorteEdad()));

        switch (vista) {
            case "editar" -> {
                // RN-07 / CP-10: Verificar si el estado es INSCRITO y la etapa 1 está vigente
                Etapa etapa1 = cronogramaService.buscarPorOrden(1);
                boolean etapa1Vigente = (etapa1 == null) || etapa1.isVigenteHoy();
                if (!"INSCRITO".equals(postulante.getEstado()) || !etapa1Vigente) {
                    req.setAttribute("error",
                            "RN-07: Operación no permitida. La inscripción solo puede editarse mientras su estado sea INSCRITO "
                            + "(estado actual: " + postulante.getEstado() + ") y la etapa de inscripción esté vigente.");
                    req.setAttribute("vistaActiva", "estado");
                    req.getRequestDispatcher("/WEB-INF/views/postulante/mi-estado.jsp").forward(req, resp);
                    return;
                }
                req.setAttribute("modoEdicion", true);
                req.setAttribute("postulanteForm", postulante);
                req.setAttribute("modalidades", postulanteService.listarModalidades());
                req.setAttribute("vistaActiva", "editar");
                // RF-05: Se reutiliza el mismo JSP del registro para la edición
                req.getRequestDispatcher("/WEB-INF/views/postulante/formulario-inscripcion.jsp").forward(req, resp);
            }
            case "resultados" -> {
                // RF-07 / RN-13 / CP-18: El postulante solo ve un resultado cuando el administrador marcó esa etapa como publicada
                req.setAttribute("publicadaRevision", cronogramaService.esEtapaPublicada(3));
                req.setAttribute("publicadaPreseleccion", cronogramaService.esEtapaPublicada(5));
                req.setAttribute("publicadaSeleccion", cronogramaService.esEtapaPublicada(8));
                req.setAttribute("postulacionActual", seleccionService.buscarPorPostulante(postulante.getIdPostulante()));
                req.setAttribute("vistaActiva", "resultados");
                req.getRequestDispatcher("/WEB-INF/views/postulante/mis-resultados.jsp").forward(req, resp);
            }
            case "postulacion" -> {
                // RF-08: Reutilizar el mismo JSP para registrar y editar postulación a selección
                cargarAtributosVistaPostulacion(req, postulante);
                req.getRequestDispatcher("/WEB-INF/views/postulante/postulacion-seleccion.jsp").forward(req, resp);
            }
            default -> {
                req.setAttribute("postulacionActual", seleccionService.buscarPorPostulante(postulante.getIdPostulante()));
                req.setAttribute("vistaActiva", "estado");
                req.getRequestDispatcher("/WEB-INF/views/postulante/mi-estado.jsp").forward(req, resp);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute("usuarioAutenticado");
        Postulante postulanteActual = postulanteService.buscarPorIdUsuario(usuario.getIdUsuario());

        String accion = req.getParameter("accion");

        if ("actualizarInscripcion".equalsIgnoreCase(accion)) {
            Postulante datosEditados = InscripcionPublicaServlet.construirPostulanteDesdeRequest(req);
            datosEditados.setIdPostulante(postulanteActual.getIdPostulante());
            try {
                postulanteService.actualizarInscripcion(datosEditados, usuario.getUsername());
                String msg = URLEncoder.encode("Sus datos de inscripción fueron actualizados correctamente.", StandardCharsets.UTF_8);
                resp.sendRedirect(req.getContextPath() + "/postulante/panel?vista=estado&exito=" + msg);
            } catch (ReglaNegocioException ex) {
                req.setAttribute("error", ex.getMessage());
                req.setAttribute("modoEdicion", true);
                req.setAttribute("postulanteForm", datosEditados);
                req.setAttribute("postulante", postulanteActual);
                req.setAttribute("modalidades", postulanteService.listarModalidades());
                req.setAttribute("fechaCorteFormateada", FormatoUtil.formatearFecha(postulanteService.obtenerFechaCorteEdad()));
                req.setAttribute("vistaActiva", "editar");
                req.getRequestDispatcher("/WEB-INF/views/postulante/formulario-inscripcion.jsp").forward(req, resp);
            }
            return;
        }

        if ("guardarPostulacion".equalsIgnoreCase(accion)) {
            int idPrograma = 0;
            try {
                idPrograma = Integer.parseInt(req.getParameter("idPrograma"));
            } catch (NumberFormatException ignored) {
            }
            try {
                seleccionService.registrarOActualizarPostulacion(
                        postulanteActual.getIdPostulante(), idPrograma, usuario.getUsername());
                String msg = URLEncoder.encode("Su postulación al programa de estudios fue guardada exitosamente.", StandardCharsets.UTF_8);
                resp.sendRedirect(req.getContextPath() + "/postulante/panel?vista=postulacion&exito=" + msg);
            } catch (ReglaNegocioException ex) {
                Postulante refrescado = postulanteService.buscarPorIdUsuario(usuario.getIdUsuario());
                req.setAttribute("postulante", refrescado);
                req.setAttribute("error", ex.getMessage());
                cargarAtributosVistaPostulacion(req, refrescado);
                req.getRequestDispatcher("/WEB-INF/views/postulante/postulacion-seleccion.jsp").forward(req, resp);
            }
            return;
        }

        if ("retirarPostulacion".equalsIgnoreCase(accion)) {
            try {
                seleccionService.retirarPostulacion(postulanteActual.getIdPostulante(), usuario.getUsername());
                String msg = URLEncoder.encode("Su postulación fue retirada correctamente. Puede volver a elegir un programa mientras el momento siga abierto.", StandardCharsets.UTF_8);
                resp.sendRedirect(req.getContextPath() + "/postulante/panel?vista=postulacion&exito=" + msg);
            } catch (ReglaNegocioException ex) {
                String err = URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
                resp.sendRedirect(req.getContextPath() + "/postulante/panel?vista=postulacion&error=" + err);
            }
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/postulante/panel?vista=estado");
    }

    private void cargarAtributosVistaPostulacion(HttpServletRequest req, Postulante postulante) {
        PostulacionSeleccion postulacionActual = seleccionService.buscarPorPostulante(postulante.getIdPostulante());
        req.setAttribute("postulacionActual", postulacionActual);
        req.setAttribute("modoEdicionPostulacion", postulacionActual != null);
        req.setAttribute("programasElegibles", catalogoService.listarProgramas());
        req.setAttribute("momentoVigente", cronogramaService.obtenerMomentoPostulacionVigente());
        req.setAttribute("vistaActiva", "postulacion");
    }
}
