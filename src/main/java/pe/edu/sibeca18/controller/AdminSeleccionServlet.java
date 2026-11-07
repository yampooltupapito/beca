package pe.edu.sibeca18.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import pe.edu.sibeca18.exception.ReglaNegocioException;
import pe.edu.sibeca18.model.Usuario;
import pe.edu.sibeca18.service.SeleccionService;
import pe.edu.sibeca18.util.BitacoraUtil;

/**
 * Controlador MVC para Ejecución de la Selección, Ranking por Programa, Reporte de Seleccionados,
 * Exportación TXT y Bitácora de Eventos (RF-14, RF-15, RF-16, RN-12, CP-15, CP-16 y Reto Opcional 7.4).
 */
@WebServlet(name = "AdminSeleccionServlet", urlPatterns = {"/admin/seleccion"})
public class AdminSeleccionServlet extends HttpServlet {

    private final SeleccionService seleccionService = new SeleccionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accion = req.getParameter("accion");

        // RF-15: Exportación del reporte de seleccionados y ranking a archivo de texto (.txt)
        if ("exportarReporteTxt".equalsIgnoreCase(accion)) {
            Usuario admin = (Usuario) req.getSession().getAttribute("usuarioAutenticado");
            BitacoraUtil.registrarEvento("EXPORTACION_TXT", admin != null ? admin.getUsername() : "admin",
                    "Exportación del reporte oficial de seleccionados a archivo de texto.");
            resp.setContentType("text/plain; charset=UTF-8");
            resp.setHeader("Content-Disposition", "attachment; filename=\"reporte_seleccionados_sibeca18_2027.txt\"");
            try (PrintWriter out = resp.getWriter()) {
                out.print(seleccionService.generarReporteSeleccionadosTxt());
                out.flush();
            }
            return;
        }

        // RF-16: Descarga directa del archivo de texto de la Bitácora del Sistema
        if ("exportarBitacoraTxt".equalsIgnoreCase(accion)) {
            resp.setContentType("text/plain; charset=UTF-8");
            resp.setHeader("Content-Disposition", "attachment; filename=\"bitacora_sibeca18.txt\"");
            try (PrintWriter out = resp.getWriter()) {
                out.print(BitacoraUtil.exportarContenidoCompleto());
                out.flush();
            }
            return;
        }

        // Vista dedicada de la Bitácora de Eventos (RF-16)
        if ("bitacora".equalsIgnoreCase(accion)) {
            req.setAttribute("menuActivo", "bitacora");
            req.setAttribute("lineasBitacora", BitacoraUtil.leerLineasArchivo(200));
            req.setAttribute("rutaArchivoBitacora", BitacoraUtil.obtenerArchivoBitacora().getAbsolutePath());
            req.getRequestDispatcher("/WEB-INF/views/admin/bitacora.jsp").forward(req, resp);
            return;
        }

        if (req.getParameter("exito") != null) {
            req.setAttribute("exito", req.getParameter("exito"));
        }
        if (req.getParameter("error") != null) {
            req.setAttribute("error", req.getParameter("error"));
        }

        cargarAtributosRankingYReporte(req);
        req.getRequestDispatcher("/WEB-INF/views/admin/seleccion.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Usuario admin = (Usuario) req.getSession().getAttribute("usuarioAutenticado");
        String adminUser = admin != null ? admin.getUsername() : "admin";
        String accion = req.getParameter("accion");

        if ("ejecutarSeleccionHilo".equalsIgnoreCase(accion)) {
            seleccionService.ejecutarSeleccionEnSegundoPlano(adminUser);
            String msg = URLEncoder.encode("Se inició la generación del ranking y selección en segundo plano mediante hilos (Reto 7.4).", StandardCharsets.UTF_8);
            resp.sendRedirect(req.getContextPath() + "/admin/seleccion?exito=" + msg);
            return;
        }

        if ("ejecutarSeleccion".equalsIgnoreCase(accion)) {
            boolean simularErrorCp16 = "true".equalsIgnoreCase(req.getParameter("simularErrorControlado"));
            try {
                Map<String, Integer> resumen = seleccionService.ejecutarSeleccionTransaccional(simularErrorCp16, adminUser);
                String msg = "Proceso de Selección Final ejecutado con éxito (COMMIT). Postulaciones evaluadas: "
                        + resumen.get("EVALUADOS") + " | SELECCIONADOS: " + resumen.get("SELECCIONADOS")
                        + " | NO SELECCIONADOS: " + resumen.get("NO_SELECCIONADOS") + ".";
                resp.sendRedirect(req.getContextPath() + "/admin/seleccion?exito="
                        + URLEncoder.encode(msg, StandardCharsets.UTF_8));
            } catch (ReglaNegocioException ex) {
                req.setAttribute("error", ex.getMessage());
                cargarAtributosRankingYReporte(req);
                req.getRequestDispatcher("/WEB-INF/views/admin/seleccion.jsp").forward(req, resp);
            }
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/admin/seleccion");
    }

    private void cargarAtributosRankingYReporte(HttpServletRequest req) {
        req.setAttribute("menuActivo", "seleccion");
        req.setAttribute("rankingPorPrograma", seleccionService.construirRankingPorPrograma());
        req.setAttribute("seleccionados", seleccionService.listarSeleccionados());
        req.setAttribute("totalesPorPrograma", seleccionService.calcularTotalesSeleccionadosPorPrograma());
        req.setAttribute("totalesPorModalidad", seleccionService.calcularTotalesSeleccionadosPorModalidad());
        req.setAttribute("estadoTareaHilo", seleccionService.getEstadoTareaHilo());
        req.setAttribute("mensajeTareaHilo", seleccionService.getMensajeTareaHilo());
    }
}
