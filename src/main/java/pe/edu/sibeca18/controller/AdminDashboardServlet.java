package pe.edu.sibeca18.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import pe.edu.sibeca18.service.CatalogoService;
import pe.edu.sibeca18.service.CronogramaService;
import pe.edu.sibeca18.service.PostulanteService;
import pe.edu.sibeca18.service.SeleccionService;
import pe.edu.sibeca18.util.BitacoraUtil;
import pe.edu.sibeca18.util.FormatoUtil;

/**
 * Controlador MVC para el Dashboard Principal del Administrador.
 */
@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardServlet extends HttpServlet {

    private final PostulanteService postulanteService = new PostulanteService();
    private final CronogramaService cronogramaService = new CronogramaService();
    private final CatalogoService catalogoService = new CatalogoService();
    private final SeleccionService seleccionService = new SeleccionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("estadisticas", postulanteService.obtenerEstadisticasPorEstado());
        req.setAttribute("etapas", cronogramaService.listarEtapas());
        req.setAttribute("programas", catalogoService.listarProgramas());
        req.setAttribute("modalidades", postulanteService.listarModalidades());
        req.setAttribute("seleccionadosCount", seleccionService.listarSeleccionados().size());
        req.setAttribute("fechaCorteFormateada", FormatoUtil.formatearFecha(cronogramaService.obtenerFechaCorteEdad()));
        req.setAttribute("ultimosEventos", BitacoraUtil.leerLineasArchivo(8));
        req.setAttribute("menuActivo", "dashboard");

        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
    }
}
