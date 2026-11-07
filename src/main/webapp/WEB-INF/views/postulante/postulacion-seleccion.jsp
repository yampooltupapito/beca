<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="pe.edu.sibeca18.model.PostulacionSeleccion"%>
<%@page import="pe.edu.sibeca18.model.Postulante"%>
<%@page import="pe.edu.sibeca18.model.ProgramaEstudio"%>
<%
    Postulante p = (Postulante) request.getAttribute("postulante");
    PostulacionSeleccion postActual = (PostulacionSeleccion) request.getAttribute("postulacionActual");
    Boolean modoEdicionObj = (Boolean) request.getAttribute("modoEdicionPostulacion");
    boolean modoEdicion = modoEdicionObj != null && modoEdicionObj;
    List<ProgramaEstudio> programas = (List<ProgramaEstudio>) request.getAttribute("programasElegibles");
    Integer momentoVigenteObj = (Integer) request.getAttribute("momentoVigente");
    int momentoVigente = momentoVigenteObj != null ? momentoVigenteObj : 0;

    boolean puedePostularPorEstado = p != null && ("PRESELECCIONADO".equals(p.getEstado()) || "POSTULO A SELECCION".equals(p.getEstado()));
%>
<!DOCTYPE html>
<html lang="es-PE">
    <head>
        <title><%= modoEdicion ? "Editar Mi Postulación a Selección" : "Registrar Postulación a Selección" %> | SIBECA-18</title>
        <jsp:include page="/WEB-INF/views/shared/head.jsp" />
    </head>
    <body>
        <jsp:include page="/WEB-INF/views/shared/navbar.jsp" />

        <div class="container my-4">
            <div class="d-flex flex-wrap justify-content-between align-items-center mb-3">
                <div>
                    <span class="badge bg-primary mb-1">
                        <%= modoEdicion ? "RF-08 · MODO EDICIÓN / RETIRO DE POSTULACIÓN" : "RF-08 · REGISTRO DE POSTULACIÓN A SELECCIÓN" %>
                    </span>
                    <h3 class="fw-bold mb-0">
                        <%= modoEdicion ? "Modificar o Retirar Mi Postulación a Programa de Estudios" : "Elección de Institución y Programa de Estudios (Etapa de Selección)" %>
                    </h3>
                    <p class="text-muted small mb-0">
                        Se reutiliza esta misma vista JSP para registrar y editar la postulación dentro del momento vigente (RN-10 y RN-11).
                    </p>
                </div>
                <div>
                    <span class="badge <%= momentoVigente > 0 ? "bg-success" : "bg-danger" %> px-3 py-2">
                        <%= momentoVigente > 0 ? ("Momento Vigente: Momento " + momentoVigente) : "Fuera de Fecha de Postulación" %>
                    </span>
                </div>
            </div>

            <% if (request.getAttribute("exito") != null) { %>
            <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
                <i class="bi bi-check-circle-fill me-2"></i>${exito}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <% } %>

            <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
                <i class="bi bi-x-octagon-fill me-2"></i><strong>Regla de Negocio Validada:</strong> ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <% } %>

            <div class="row g-4">
                <!-- Columna de estado y postulación vigente -->
                <div class="col-lg-5">
                    <div class="card border-0 shadow-sm rounded-4 h-100">
                        <div class="card-body p-4">
                            <h5 class="fw-bold mb-3"><i class="bi bi-person-check-fill text-primary me-2"></i>Mi Condición para Postular</h5>
                            <div class="p-3 rounded-3 bg-light border mb-3 small">
                                <div class="mb-1"><strong>Postulante:</strong> ${postulante.nombreCompleto}</div>
                                <div class="mb-1"><strong>Modalidad:</strong> ${postulante.modalidadNombre}</div>
                                <div class="mb-1"><strong>Puntaje ENP:</strong> <span class="fw-bold text-primary">${postulante.puntajeEnpFormateado}</span></div>
                                <div><strong>Estado Actual:</strong> <span class="badge bg-primary">${postulante.estado}</span></div>
                            </div>

                            <% if (!puedePostularPorEstado) { %>
                            <div class="alert alert-warning small">
                                <i class="bi bi-exclamation-triangle-fill me-1"></i>
                                <strong>Aviso RN-10 (CP-14):</strong> Su estado actual es <strong>${postulante.estado}</strong>.
                                Solo los postulantes con condición de <strong>PRESELECCIONADO</strong> pueden registrar una postulación a la selección.
                                Puede enviar el formulario a la derecha para comprobar el bloqueo del servidor (CP-14).
                            </div>
                            <% } %>

                            <% if (postActual != null) { %>
                            <div class="border border-primary rounded-3 p-3 bg-primary bg-opacity-10 mt-3">
                                <div class="fw-bold text-primary mb-2">
                                    <i class="bi bi-PatchCheckFill me-1"></i>Postulación Registrada Actualmente
                                </div>
                                <div class="small mb-1"><strong>Institución:</strong> ${postulacionActual.institucionNombre}</div>
                                <div class="small mb-1"><strong>Programa:</strong> ${postulacionActual.programaNombre}</div>
                                <div class="small mb-1"><strong>Región / Gestión:</strong> ${postulacionActual.regionPrograma} (${postulacionActual.tipoGestionPrograma})</div>
                                <div class="small mb-1"><strong>Momento:</strong> Momento ${postulacionActual.momento}</div>
                                <div class="small mb-2"><strong>Fecha y Hora Exacta (RN-12):</strong> ${postulacionActual.fechaHoraFormateada}</div>

                                <% if (momentoVigente > 0 && "POSTULO A SELECCION".equals(p.getEstado())) { %>
                                <form action="${pageContext.request.contextPath}/postulante/panel" method="post"
                                      onsubmit="return confirm('¿Está seguro de retirar su postulación actual? Su estado volverá a PRESELECCIONADO.');">
                                    <input type="hidden" name="accion" value="retirarPostulacion">
                                    <button type="submit" class="btn btn-sm btn-outline-danger w-100 fw-semibold mt-1">
                                        <i class="bi bi-trash3 me-1"></i>Retirar Mi Postulación (RF-08)
                                    </button>
                                </form>
                                <% } %>
                            </div>
                            <% } %>
                        </div>
                    </div>
                </div>

                <!-- Columna de formulario reutilizado para Registrar y Editar Postulación -->
                <div class="col-lg-7">
                    <div class="card border-0 shadow-sm rounded-4 h-100">
                        <div class="card-body p-4">
                            <h5 class="fw-bold mb-3">
                                <i class="bi bi-building-add text-primary me-2"></i>
                                <%= modoEdicion ? "Cambiar Programa de Estudios Elegido" : "Seleccionar Institución y Programa Elegible" %>
                            </h5>

                            <form action="${pageContext.request.contextPath}/postulante/panel" method="post">
                                <input type="hidden" name="accion" value="guardarPostulacion">

                                <div class="mb-3">
                                    <label for="idPrograma" class="form-label fw-semibold">
                                        Catálogo de Instituciones y Programas con Vacantes (RN-11) <span class="text-danger">*</span>
                                    </label>
                                    <select class="form-select form-select-lg" id="idPrograma" name="idPrograma" required>
                                        <option value="0">-- Seleccione una institución y programa de estudios --</option>
                                        <% if (programas != null) {
                                            for (ProgramaEstudio prog : programas) {
                                                boolean sel = (postActual != null && postActual.getIdPrograma().equals(prog.getIdPrograma()));
                                        %>
                                        <option value="<%= prog.getIdPrograma() %>" <%= sel ? "selected" : "" %>>
                                            <%= prog.getInstitucion() %> — <%= prog.getProgramaEstudios() %>
                                            [<%= prog.getTipoGestion() %> | <%= prog.getRegion() %> | Vacantes: <%= prog.getVacantes() %>]
                                        </option>
                                        <%  }
                                           } %>
                                    </select>
                                    <div class="form-text">
                                        En caso de empate en puntaje ENP, el sistema prioriza la fecha y hora exacta en que se registró la postulación (RN-12).
                                    </div>
                                </div>

                                <div class="table-responsive mb-3">
                                    <table class="table table-sm table-hover table-sibeca align-middle mb-0">
                                        <thead>
                                            <tr>
                                                <th>Institución (IES)</th>
                                                <th>Programa de Estudios</th>
                                                <th>Gestión</th>
                                                <th>Región</th>
                                                <th class="text-center">Vacantes</th>
                                                <th class="text-center">Postulantes</th>
                                            </tr>
                                        </thead>
                                        <tbody class="small">
                                            <% if (programas != null) {
                                                for (ProgramaEstudio prog : programas) { %>
                                            <tr>
                                                <td class="fw-semibold"><%= prog.getInstitucion() %></td>
                                                <td><%= prog.getProgramaEstudios() %></td>
                                                <td>
                                                    <span class="badge <%= "PUBLICA".equals(prog.getTipoGestion()) ? "bg-info text-dark" : "bg-secondary" %>">
                                                        <%= prog.getTipoGestion() %>
                                                    </span>
                                                </td>
                                                <td><%= prog.getRegion() %></td>
                                                <td class="text-center fw-bold text-primary"><%= prog.getVacantes() %></td>
                                                <td class="text-center"><%= prog.getTotalPostulantes() %></td>
                                            </tr>
                                            <%  }
                                               } %>
                                        </tbody>
                                    </table>
                                </div>

                                <div class="d-flex justify-content-end gap-2">
                                    <button type="submit" class="btn btn-primary px-4 fw-semibold">
                                        <i class="bi bi-send-check me-1"></i>
                                        <%= modoEdicion ? "Actualizar Mi Postulación" : "Confirmar Postulación a Selección" %>
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <jsp:include page="/WEB-INF/views/shared/footer.jsp" />
    </body>
</html>
