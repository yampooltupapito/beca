package pe.edu.sibeca18.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import pe.edu.sibeca18.exception.ReglaNegocioException;
import pe.edu.sibeca18.model.Postulante;
import pe.edu.sibeca18.model.Usuario;
import pe.edu.sibeca18.service.PostulanteService;
import pe.edu.sibeca18.service.SeleccionService;

/**
 * Controlador MVC de Gestión de Postulantes, Revisión de Inscripción y Registro de Puntaje ENP
 * (RF-09, RF-10, RF-11, RN-07, RN-08, RN-09, CP-11, CP-12, CP-13).
 */
@WebServlet(name = "AdminPostulantesServlet", urlPatterns = {"/admin/postulantes"})
public class AdminPostulantesServlet extends HttpServlet {

    private final PostulanteService postulanteService = new PostulanteService();
    private final SeleccionService seleccionService = new SeleccionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("menuActivo", "postulantes");

        if (req.getParameter("exito") != null) {
            req.setAttribute("exito", req.getParameter("exito"));
        }
        if (req.getParameter("error") != null) {
            req.setAttribute("error", req.getParameter("error"));
        }

        String accion = req.getParameter("accion");
        if ("detalle".equalsIgnoreCase(accion)) {
            int idPostulante = parsearEntero(req.getParameter("id"));
            Postulante p = postulanteService.buscarPorId(idPostulante);
            if (p == null) {
                resp.sendRedirect(req.getContextPath() + "/admin/postulantes?error="
                        + URLEncoder.encode("El postulante solicitado no existe.", StandardCharsets.UTF_8));
                return;
            }
            req.setAttribute("postulante", p);
            req.setAttribute("postulacion", seleccionService.buscarPorPostulante(idPostulante));
            req.getRequestDispatcher("/WEB-INF/views/admin/postulante-detalle.jsp").forward(req, resp);
            return;
        }

        Integer idModalidad = parsearEnteroNullable(req.getParameter("idModalidad"));
        String estado = req.getParameter("estado");
        String dni = req.getParameter("dni");

        req.setAttribute("modalidades", postulanteService.listarModalidades());
        req.setAttribute("postulantes", postulanteService.listarConFiltros(idModalidad, estado, dni));
        req.setAttribute("filtroModalidad", idModalidad);
        req.setAttribute("filtroEstado", estado != null ? estado : "TODOS");
        req.setAttribute("filtroDni", dni != null ? dni : "");

        req.getRequestDispatcher("/WEB-INF/views/admin/postulantes.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Usuario admin = (Usuario) req.getSession().getAttribute("usuarioAutenticado");
        String adminUsername = admin != null ? admin.getUsername() : "admin";

        String accion = req.getParameter("accion");
        int idPostulante = parsearEntero(req.getParameter("idPostulante"));

        try {
            switch (accion != null ? accion : "") {
                case "iniciarRevision" -> {
                    postulanteService.iniciarRevision(idPostulante, adminUsername);
                    String msg = URLEncoder.encode("El estado del postulante cambió a EN REVISIÓN.", StandardCharsets.UTF_8);
                    resp.sendRedirect(req.getContextPath() + "/admin/postulantes?accion=detalle&id=" + idPostulante + "&exito=" + msg);
                }
                case "dictaminarRevision" -> {
                    String resultado = req.getParameter("resultadoRevision");
                    String observacion = req.getParameter("observacionRevision");
                    postulanteService.dictaminarRevision(idPostulante, resultado, observacion, adminUsername);
                    String msg = URLEncoder.encode("Revisión registrada exitosamente. Nuevo estado: " + resultado + ".", StandardCharsets.UTF_8);
                    resp.sendRedirect(req.getContextPath() + "/admin/postulantes?accion=detalle&id=" + idPostulante + "&exito=" + msg);
                }
                case "registrarPuntajeEnp" -> {
                    String puntajeStr = req.getParameter("puntajeEnp");
                    BigDecimal puntaje = null;
                    if (puntajeStr != null && !puntajeStr.isBlank()) {
                        try {
                            puntaje = new BigDecimal(puntajeStr.trim().replace(",", "."));
                        } catch (NumberFormatException ex) {
                            throw new ReglaNegocioException("RN-09: El formato del puntaje ingresado no es numérico.");
                        }
                    }
                    String estadoResultante = postulanteService.registrarPuntajeEnp(idPostulante, puntaje, adminUsername);
                    String msg = URLEncoder.encode("Puntaje ENP registrado correctamente. Condición determinada automáticamente: "
                            + estadoResultante + ".", StandardCharsets.UTF_8);
                    resp.sendRedirect(req.getContextPath() + "/admin/postulantes?accion=detalle&id=" + idPostulante + "&exito=" + msg);
                }
                case "eliminar" -> {
                    postulanteService.eliminarInscripcion(idPostulante, adminUsername);
                    String msg = URLEncoder.encode("Inscripción en estado INSCRITO eliminada correctamente.", StandardCharsets.UTF_8);
                    resp.sendRedirect(req.getContextPath() + "/admin/postulantes?exito=" + msg);
                }
                default -> resp.sendRedirect(req.getContextPath() + "/admin/postulantes");
            }
        } catch (ReglaNegocioException ex) {
            if ("eliminar".equalsIgnoreCase(accion)) {
                String err = URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
                resp.sendRedirect(req.getContextPath() + "/admin/postulantes?error=" + err);
            } else {
                // Forward en la misma petición mostrando el error de regla de negocio en el detalle
                Postulante p = postulanteService.buscarPorId(idPostulante);
                req.setAttribute("menuActivo", "postulantes");
                req.setAttribute("postulante", p);
                req.setAttribute("postulacion", seleccionService.buscarPorPostulante(idPostulante));
                req.setAttribute("error", ex.getMessage());
                req.getRequestDispatcher("/WEB-INF/views/admin/postulante-detalle.jsp").forward(req, resp);
            }
        }
    }

    private int parsearEntero(String val) {
        if (val == null || val.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private Integer parsearEnteroNullable(String val) {
        int v = parsearEntero(val);
        return v > 0 ? v : null;
    }
}
