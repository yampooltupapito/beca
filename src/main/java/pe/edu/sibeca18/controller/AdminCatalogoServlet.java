package pe.edu.sibeca18.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import pe.edu.sibeca18.exception.ReglaNegocioException;
import pe.edu.sibeca18.model.Institucion;
import pe.edu.sibeca18.model.ProgramaEstudio;
import pe.edu.sibeca18.model.Usuario;
import pe.edu.sibeca18.service.CatalogoService;
import pe.edu.sibeca18.util.ValidadorUtil;

/**
 * Controlador MVC para el Mantenimiento CRUD de Instituciones y Programas de Estudios (RF-13, RN-11).
 */
@WebServlet(name = "AdminCatalogoServlet", urlPatterns = {"/admin/programas"})
public class AdminCatalogoServlet extends HttpServlet {

    private final CatalogoService catalogoService = new CatalogoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("menuActivo", "programas");
        if (req.getParameter("exito") != null) {
            req.setAttribute("exito", req.getParameter("exito"));
        }
        if (req.getParameter("error") != null) {
            req.setAttribute("error", req.getParameter("error"));
        }

        String editarProgId = req.getParameter("editarPrograma");
        if (editarProgId != null && !editarProgId.isBlank()) {
            try {
                ProgramaEstudio progEditar = catalogoService.buscarProgramaPorId(Integer.parseInt(editarProgId));
                req.setAttribute("programaForm", progEditar);
            } catch (NumberFormatException ignored) {
            }
        }

        String editarInstId = req.getParameter("editarInstitucion");
        if (editarInstId != null && !editarInstId.isBlank()) {
            try {
                Institucion instEditar = catalogoService.buscarInstitucionPorId(Integer.parseInt(editarInstId));
                req.setAttribute("institucionForm", instEditar);
            } catch (NumberFormatException ignored) {
            }
        }

        cargarListados(req);
        req.getRequestDispatcher("/WEB-INF/views/admin/programas.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Usuario admin = (Usuario) req.getSession().getAttribute("usuarioAutenticado");
        String adminUser = admin != null ? admin.getUsername() : "admin";
        String accion = req.getParameter("accion");

        try {
            switch (accion != null ? accion : "") {
                case "guardarPrograma" -> {
                    ProgramaEstudio p = new ProgramaEstudio();
                    String idStr = req.getParameter("idPrograma");
                    if (idStr != null && !idStr.isBlank()) {
                        p.setIdPrograma(Integer.parseInt(idStr.trim()));
                    }
                    String idInstStr = req.getParameter("idInstitucion");
                    if (idInstStr != null && !idInstStr.isBlank()) {
                        p.setIdInstitucion(Integer.parseInt(idInstStr.trim()));
                    }
                    p.setInstitucion(ValidadorUtil.limpiarTexto(req.getParameter("institucion")));
                    p.setProgramaEstudios(ValidadorUtil.limpiarTexto(req.getParameter("programaEstudios")));
                    p.setTipoGestion(ValidadorUtil.limpiarTexto(req.getParameter("tipoGestion")));
                    p.setRegion(ValidadorUtil.limpiarTexto(req.getParameter("region")));
                    try {
                        p.setVacantes(Integer.parseInt(req.getParameter("vacantes")));
                    } catch (NumberFormatException ex) {
                        p.setVacantes(0);
                    }
                    p.setActivo(true);

                    catalogoService.guardarPrograma(p, adminUser);
                    String msg = URLEncoder.encode("Programa de estudios guardado correctamente.", StandardCharsets.UTF_8);
                    resp.sendRedirect(req.getContextPath() + "/admin/programas?exito=" + msg);
                }
                case "eliminarPrograma" -> {
                    int idProg = Integer.parseInt(req.getParameter("idPrograma"));
                    catalogoService.eliminarPrograma(idProg, adminUser);
                    String msg = URLEncoder.encode("Programa de estudios eliminado correctamente.", StandardCharsets.UTF_8);
                    resp.sendRedirect(req.getContextPath() + "/admin/programas?exito=" + msg);
                }
                case "guardarInstitucion" -> {
                    Institucion inst = new Institucion();
                    String idInst = req.getParameter("idInstitucion");
                    if (idInst != null && !idInst.isBlank()) {
                        inst.setIdInstitucion(Integer.parseInt(idInst.trim()));
                    }
                    inst.setNombre(ValidadorUtil.limpiarTexto(req.getParameter("nombreInstitucion")));
                    inst.setTipoGestion(ValidadorUtil.limpiarTexto(req.getParameter("tipoGestionInstitucion")));
                    inst.setRegion(ValidadorUtil.limpiarTexto(req.getParameter("regionInstitucion")));
                    inst.setElegible(true);

                    catalogoService.guardarInstitucion(inst, adminUser);
                    String msg = URLEncoder.encode("Institución de Educación Superior guardada correctamente.", StandardCharsets.UTF_8);
                    resp.sendRedirect(req.getContextPath() + "/admin/programas?exito=" + msg);
                }
                case "eliminarInstitucion" -> {
                    int idInst = Integer.parseInt(req.getParameter("idInstitucion"));
                    catalogoService.eliminarInstitucion(idInst, adminUser);
                    String msg = URLEncoder.encode("Institución eliminada correctamente.", StandardCharsets.UTF_8);
                    resp.sendRedirect(req.getContextPath() + "/admin/programas?exito=" + msg);
                }
                default -> resp.sendRedirect(req.getContextPath() + "/admin/programas");
            }
        } catch (ReglaNegocioException ex) {
            req.setAttribute("menuActivo", "programas");
            req.setAttribute("error", ex.getMessage());
            cargarListados(req);
            req.getRequestDispatcher("/WEB-INF/views/admin/programas.jsp").forward(req, resp);
        } catch (Exception ex) {
            String err = URLEncoder.encode("No se pudo completar la operación: " + ex.getMessage(), StandardCharsets.UTF_8);
            resp.sendRedirect(req.getContextPath() + "/admin/programas?error=" + err);
        }
    }

    private void cargarListados(HttpServletRequest req) {
        req.setAttribute("instituciones", catalogoService.listarInstituciones());
        req.setAttribute("programas", catalogoService.listarProgramas());
    }
}
