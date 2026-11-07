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
import pe.edu.sibeca18.model.Postulante;
import pe.edu.sibeca18.service.PostulanteService;
import pe.edu.sibeca18.util.FormatoUtil;
import pe.edu.sibeca18.util.ValidadorUtil;

/**
 * Controlador MVC para la inscripción pública de postulantes (RF-03, RF-04, CP-05 a CP-09).
 * Reutiliza la vista /WEB-INF/views/postulante/formulario-inscripcion.jsp junto con la edición.
 */
@WebServlet(name = "InscripcionPublicaServlet", urlPatterns = {"/inscripcion"})
public class InscripcionPublicaServlet extends HttpServlet {

    private final PostulanteService postulanteService = new PostulanteService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        cargarCatalogosFormulario(req);
        req.setAttribute("modoEdicion", false);
        req.setAttribute("postulanteForm", new Postulante());
        req.getRequestDispatcher("/WEB-INF/views/postulante/formulario-inscripcion.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        Postulante p = construirPostulanteDesdeRequest(req);
        String passwordPlano = req.getParameter("password");
        boolean simularFalloCp09 = "true".equalsIgnoreCase(req.getParameter("simularFalloTransaccion"));

        try {
            postulanteService.registrarInscripcion(p, passwordPlano, simularFalloCp09);

            // Redirección después de escritura para evitar reenvío del formulario (PRG)
            String mensajeExito = "Inscripción registrada exitosamente en estado INSCRITO. "
                    + "Inicie sesión con su usuario (" + p.getDni() + ") y la contraseña creada.";
            resp.sendRedirect(req.getContextPath() + "/auth?accion=login&exito="
                    + URLEncoder.encode(mensajeExito, StandardCharsets.UTF_8));

        } catch (ReglaNegocioException ex) {
            cargarCatalogosFormulario(req);
            req.setAttribute("modoEdicion", false);
            req.setAttribute("postulanteForm", p);
            req.setAttribute("error", ex.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/postulante/formulario-inscripcion.jsp").forward(req, resp);
        } catch (Exception ex) {
            cargarCatalogosFormulario(req);
            req.setAttribute("modoEdicion", false);
            req.setAttribute("postulanteForm", p);
            req.setAttribute("error", "No se pudo procesar el registro de inscripción. Verifique los datos e intente nuevamente.");
            req.getRequestDispatcher("/WEB-INF/views/postulante/formulario-inscripcion.jsp").forward(req, resp);
        }
    }

    private void cargarCatalogosFormulario(HttpServletRequest req) {
        req.setAttribute("modalidades", postulanteService.listarModalidades());
        req.setAttribute("fechaCorte", postulanteService.obtenerFechaCorteEdad());
        req.setAttribute("fechaCorteFormateada", FormatoUtil.formatearFecha(postulanteService.obtenerFechaCorteEdad()));
    }

    public static Postulante construirPostulanteDesdeRequest(HttpServletRequest req) {
        Postulante p = new Postulante();
        String idPostStr = req.getParameter("idPostulante");
        if (idPostStr != null && !idPostStr.isBlank()) {
            try {
                p.setIdPostulante(Integer.parseInt(idPostStr.trim()));
            } catch (NumberFormatException ignored) {
            }
        }
        p.setDni(ValidadorUtil.limpiarTexto(req.getParameter("dni")));
        p.setUsername(p.getDni());
        p.setNombres(ValidadorUtil.limpiarTexto(req.getParameter("nombres")));
        p.setApellidos(ValidadorUtil.limpiarTexto(req.getParameter("apellidos")));
        p.setFechaNacimiento(FormatoUtil.parsearFechaIso(req.getParameter("fechaNacimiento")));
        p.setNacionalidad(ValidadorUtil.limpiarTexto(req.getParameter("nacionalidad")));
        p.setCorreo(ValidadorUtil.limpiarTexto(req.getParameter("correo")));
        p.setCelular(ValidadorUtil.limpiarTexto(req.getParameter("celular")));
        p.setRegion(ValidadorUtil.limpiarTexto(req.getParameter("region")));
        p.setColegio(ValidadorUtil.limpiarTexto(req.getParameter("colegio")));
        p.setSituacionEstudios(ValidadorUtil.limpiarTexto(req.getParameter("situacionEstudios")));

        try {
            p.setAnioEgreso(Integer.parseInt(req.getParameter("anioEgreso")));
        } catch (NumberFormatException ex) {
            p.setAnioEgreso(2026);
        }

        p.setTercioSuperior("true".equalsIgnoreCase(req.getParameter("tercioSuperior"))
                || "1".equals(req.getParameter("tercioSuperior"))
                || "on".equalsIgnoreCase(req.getParameter("tercioSuperior")));

        p.setClasificacionSocioeconomica(ValidadorUtil.limpiarTexto(req.getParameter("clasificacionSocioeconomica")));

        p.setDiscapacidadAcreditada("true".equalsIgnoreCase(req.getParameter("discapacidadAcreditada"))
                || "1".equals(req.getParameter("discapacidadAcreditada"))
                || "on".equalsIgnoreCase(req.getParameter("discapacidadAcreditada")));

        try {
            p.setIdModalidad(Integer.parseInt(req.getParameter("idModalidad")));
        } catch (NumberFormatException ex) {
            p.setIdModalidad(0);
        }
        return p;
    }
}
