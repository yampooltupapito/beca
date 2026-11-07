package pe.edu.sibeca18.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import pe.edu.sibeca18.exception.ReglaNegocioException;
import pe.edu.sibeca18.model.Etapa;
import pe.edu.sibeca18.model.Usuario;
import pe.edu.sibeca18.service.CronogramaService;
import pe.edu.sibeca18.util.FormatoUtil;

/**
 * Controlador MVC para el mantenimiento del Cronograma parametrizable, publicación de resultados
 * y configuración de la Fecha de Corte de Edad (RF-12, RN-02, RN-13, CP-18).
 */
@WebServlet(name = "AdminCronogramaServlet", urlPatterns = {"/admin/cronograma"})
public class AdminCronogramaServlet extends HttpServlet {

    private final CronogramaService cronogramaService = new CronogramaService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("menuActivo", "cronograma");
        if (req.getParameter("exito") != null) {
            req.setAttribute("exito", req.getParameter("exito"));
        }
        if (req.getParameter("error") != null) {
            req.setAttribute("error", req.getParameter("error"));
        }

        cargarDatosCronograma(req);
        req.getRequestDispatcher("/WEB-INF/views/admin/cronograma.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Usuario admin = (Usuario) req.getSession().getAttribute("usuarioAutenticado");
        String adminUser = admin != null ? admin.getUsername() : "admin";
        String accion = req.getParameter("accion");

        try {
            if ("actualizarFechaCorte".equalsIgnoreCase(accion)) {
                LocalDate nuevaCorte = FormatoUtil.parsearFechaIso(req.getParameter("fechaCorteEdad"));
                cronogramaService.actualizarFechaCorteEdad(nuevaCorte, adminUser);
                String msg = URLEncoder.encode("Fecha de corte para cálculo de edad (RN-02) actualizada a "
                        + FormatoUtil.formatearFecha(nuevaCorte) + ".", StandardCharsets.UTF_8);
                resp.sendRedirect(req.getContextPath() + "/admin/cronograma?exito=" + msg);
                return;
            }

            if ("togglePublicacion".equalsIgnoreCase(accion)) {
                int idEtapa = Integer.parseInt(req.getParameter("idEtapa"));
                Etapa etapa = cronogramaService.buscarPorId(idEtapa);
                if (etapa != null) {
                    etapa.setPublicada(!etapa.isPublicada());
                    cronogramaService.actualizarEtapa(etapa, adminUser);
                    String estadoPub = etapa.isPublicada() ? "PUBLICADA" : "NO PUBLICADA";
                    String msg = URLEncoder.encode("La etapa '" + etapa.getNombre() + "' ahora se encuentra " + estadoPub + ".", StandardCharsets.UTF_8);
                    resp.sendRedirect(req.getContextPath() + "/admin/cronograma?exito=" + msg);
                    return;
                }
            }

            if ("actualizarEtapa".equalsIgnoreCase(accion)) {
                int idEtapa = Integer.parseInt(req.getParameter("idEtapa"));
                Etapa actual = cronogramaService.buscarPorId(idEtapa);
                if (actual == null) {
                    throw new ReglaNegocioException("La etapa seleccionada no existe.");
                }
                actual.setNombre(req.getParameter("nombre"));
                actual.setFechaInicio(FormatoUtil.parsearFechaIso(req.getParameter("fechaInicio")));
                actual.setFechaFin(FormatoUtil.parsearFechaIso(req.getParameter("fechaFin")));
                actual.setPublicada("true".equalsIgnoreCase(req.getParameter("publicada"))
                        || "1".equals(req.getParameter("publicada"))
                        || "on".equalsIgnoreCase(req.getParameter("publicada")));
                if (req.getParameter("descripcion") != null) {
                    actual.setDescripcion(req.getParameter("descripcion").trim());
                }

                cronogramaService.actualizarEtapa(actual, adminUser);
                String msg = URLEncoder.encode("Etapa #" + actual.getOrden() + " actualizada correctamente.", StandardCharsets.UTF_8);
                resp.sendRedirect(req.getContextPath() + "/admin/cronograma?exito=" + msg);
                return;
            }

            resp.sendRedirect(req.getContextPath() + "/admin/cronograma");

        } catch (ReglaNegocioException ex) {
            req.setAttribute("menuActivo", "cronograma");
            req.setAttribute("error", ex.getMessage());
            cargarDatosCronograma(req);
            req.getRequestDispatcher("/WEB-INF/views/admin/cronograma.jsp").forward(req, resp);
        }
    }

    private void cargarDatosCronograma(HttpServletRequest req) {
        LocalDate fechaCorte = cronogramaService.obtenerFechaCorteEdad();
        req.setAttribute("etapas", cronogramaService.listarEtapas());
        req.setAttribute("fechaCorteIso", fechaCorte.toString());
        req.setAttribute("fechaCorteFormateada", FormatoUtil.formatearFecha(fechaCorte));
    }
}
