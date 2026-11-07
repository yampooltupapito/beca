<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="pe.edu.sibeca18.model.Institucion"%>
<%@page import="pe.edu.sibeca18.model.ProgramaEstudio"%>
<%
    List<Institucion> instituciones = (List<Institucion>) request.getAttribute("instituciones");
    List<ProgramaEstudio> programas = (List<ProgramaEstudio>) request.getAttribute("programas");
    ProgramaEstudio progForm = (ProgramaEstudio) request.getAttribute("programaForm");
    Institucion instForm = (Institucion) request.getAttribute("institucionForm");
    boolean editandoPrograma = (progForm != null && progForm.getIdPrograma() != null);
    boolean editandoInstitucion = (instForm != null && instForm.getIdInstitucion() != null);
%>
<!DOCTYPE html>
<html lang="es-PE">
    <head>
        <title>Instituciones y Programas | SIBECA-18</title>
        <jsp:include page="/WEB-INF/views/shared/head.jsp" />
    </head>
    <body>
        <jsp:include page="/WEB-INF/views/shared/navbar.jsp" />

        <div class="container-fluid">
            <div class="row">
                <jsp:include page="/WEB-INF/views/shared/admin-sidebar.jsp" />

                <main class="col-lg-10 col-md-9 p-4">
                    <div class="d-flex flex-wrap justify-content-between align-items-center mb-3">
                        <div>
                            <span class="badge bg-primary mb-1">RF-13 · RN-11 · MANTENIMIENTO DE INSTITUCIONES Y PROGRAMAS</span>
                            <h3 class="fw-bold mb-0">Catálogo de IES Elegibles, Programas y Vacantes</h3>
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
                        <i class="bi bi-x-octagon-fill me-2"></i>${error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                    <% } %>

                    <div class="row g-4">
                        <!-- Columna izquierda: Formularios CRUD de Programa e Institución -->
                        <div class="col-lg-4">
                            <!-- Formulario de Programa de Estudios -->
                            <div class="card border-0 shadow-sm rounded-4 mb-4">
                                <div class="card-body p-4">
                                    <h5 class="fw-bold mb-3">
                                        <i class="bi bi-mortarboard-fill text-primary me-2"></i>
                                        <%= editandoPrograma ? "Editar Programa #" + progForm.getIdPrograma() : "Registrar Programa de Estudios" %>
                                    </h5>
                                    <form action="${pageContext.request.contextPath}/admin/programas" method="post">
                                        <input type="hidden" name="accion" value="guardarPrograma">
                                        <% if (editandoPrograma) { %>
                                        <input type="hidden" name="idPrograma" value="<%= progForm.getIdPrograma() %>">
                                        <% } %>

                                        <div class="mb-2">
                                            <label class="form-label small fw-semibold">Elegir Institución Registrada (Opcional)</label>
                                            <select id="selectInstitucionPrograma" name="idInstitucion" class="form-select form-select-sm">
                                                <option value="">-- Seleccionar del catálogo o escribir abajo --</option>
                                                <% if (instituciones != null) {
                                                    for (Institucion i : instituciones) {
                                                        boolean sel = editandoPrograma && progForm.getIdInstitucion() != null && progForm.getIdInstitucion().equals(i.getIdInstitucion());
                                                %>
                                                <option value="<%= i.getIdInstitucion() %>"
                                                        data-nombre="<%= i.getNombre() %>"
                                                        data-gestion="<%= i.getTipoGestion() %>"
                                                        data-region="<%= i.getRegion() %>"
                                                        <%= sel ? "selected" : "" %>>
                                                    <%= i.getNombre() %> (<%= i.getRegion() %>)
                                                </option>
                                                <%  }
                                                   } %>
                                            </select>
                                        </div>

                                        <div class="mb-2">
                                            <label class="form-label small fw-semibold">Nombre de la Institución (IES) <span class="text-danger">*</span></label>
                                            <input type="text" id="institucionTexto" name="institucion" class="form-control form-control-sm"
                                                   value="<%= editandoPrograma ? progForm.getInstitucion() : "" %>"
                                                   placeholder="Ej. Universidad Nacional de Ingeniería" required>
                                        </div>

                                        <div class="mb-2">
                                            <label class="form-label small fw-semibold">Programa de Estudios (Carrera) <span class="text-danger">*</span></label>
                                            <input type="text" name="programaEstudios" class="form-control form-control-sm"
                                                   value="<%= editandoPrograma ? progForm.getProgramaEstudios() : "" %>"
                                                   placeholder="Ej. Ingeniería de Sistemas" required>
                                        </div>

                                        <div class="row g-2 mb-3">
                                            <div class="col-sm-4">
                                                <label class="form-label small fw-semibold">Gestión <span class="text-danger">*</span></label>
                                                <select id="tipoGestionPrograma" name="tipoGestion" class="form-select form-select-sm" required>
                                                    <option value="PUBLICA" <%= (editandoPrograma && "PUBLICA".equals(progForm.getTipoGestion())) ? "selected" : "" %>>PÚBLICA</option>
                                                    <option value="PRIVADA" <%= (editandoPrograma && "PRIVADA".equals(progForm.getTipoGestion())) ? "selected" : "" %>>PRIVADA</option>
                                                </select>
                                            </div>
                                            <div class="col-sm-4">
                                                <label class="form-label small fw-semibold">Región <span class="text-danger">*</span></label>
                                                <input type="text" id="regionPrograma" name="region" class="form-control form-control-sm"
                                                       value="<%= editandoPrograma ? progForm.getRegion() : "Lima" %>" required>
                                            </div>
                                            <div class="col-sm-4">
                                                <label class="form-label small fw-semibold">Vacantes <span class="text-danger">*</span></label>
                                                <input type="number" name="vacantes" min="1" max="500" class="form-control form-control-sm"
                                                       value="<%= editandoPrograma ? progForm.getVacantes() : 2 %>" required>
                                            </div>
                                        </div>

                                        <div class="d-flex justify-content-end gap-2">
                                            <% if (editandoPrograma) { %>
                                            <a href="${pageContext.request.contextPath}/admin/programas" class="btn btn-sm btn-light border">Cancelar</a>
                                            <% } %>
                                            <button type="submit" class="btn btn-sm btn-primary fw-semibold">
                                                <i class="bi bi-save me-1"></i><%= editandoPrograma ? "Actualizar Programa" : "Agregar Programa" %>
                                            </button>
                                        </div>
                                    </form>
                                </div>
                            </div>

                            <!-- Formulario de Institución -->
                            <div class="card border-0 shadow-sm rounded-4">
                                <div class="card-body p-4">
                                    <h6 class="fw-bold mb-3">
                                        <i class="bi bi-building text-primary me-2"></i>
                                        <%= editandoInstitucion ? "Editar Institución #" + instForm.getIdInstitucion() : "Registrar Nueva Institución (IES)" %>
                                    </h6>
                                    <form action="${pageContext.request.contextPath}/admin/programas" method="post">
                                        <input type="hidden" name="accion" value="guardarInstitucion">
                                        <% if (editandoInstitucion) { %>
                                        <input type="hidden" name="idInstitucion" value="<%= instForm.getIdInstitucion() %>">
                                        <% } %>
                                        <div class="mb-2">
                                            <input type="text" name="nombreInstitucion" class="form-control form-control-sm"
                                                   value="<%= editandoInstitucion ? instForm.getNombre() : "" %>"
                                                   placeholder="Nombre de la Institución" required>
                                        </div>
                                        <div class="row g-2 mb-2">
                                            <div class="col-6">
                                                <select name="tipoGestionInstitucion" class="form-select form-select-sm">
                                                    <option value="PUBLICA" <%= (editandoInstitucion && "PUBLICA".equals(instForm.getTipoGestion())) ? "selected" : "" %>>PÚBLICA</option>
                                                    <option value="PRIVADA" <%= (editandoInstitucion && "PRIVADA".equals(instForm.getTipoGestion())) ? "selected" : "" %>>PRIVADA</option>
                                                </select>
                                            </div>
                                            <div class="col-6">
                                                <input type="text" name="regionInstitucion" class="form-control form-control-sm"
                                                       value="<%= editandoInstitucion ? instForm.getRegion() : "" %>"
                                                       placeholder="Región" required>
                                            </div>
                                        </div>
                                        <div class="d-flex justify-content-end gap-2">
                                            <% if (editandoInstitucion) { %>
                                            <a href="${pageContext.request.contextPath}/admin/programas" class="btn btn-sm btn-light border">Cancelar</a>
                                            <% } %>
                                            <button type="submit" class="btn btn-sm btn-outline-primary fw-semibold">
                                                <%= editandoInstitucion ? "Actualizar IES" : "Guardar IES" %>
                                            </button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>

                        <!-- Columna derecha: Listado de Programas e Instituciones -->
                        <div class="col-lg-8">
                            <div class="card border-0 shadow-sm rounded-4 mb-4">
                                <div class="card-header bg-white border-0 pt-4 px-4 pb-2">
                                    <h5 class="fw-bold mb-0">Programas de Estudios Elegibles y Vacantes (RN-11)</h5>
                                </div>
                                <div class="card-body p-0">
                                    <div class="table-responsive">
                                        <table class="table table-hover table-sibeca align-middle mb-0">
                                            <thead>
                                                <tr>
                                                    <th class="ps-3">ID</th>
                                                    <th>Institución</th>
                                                    <th>Programa de Estudios</th>
                                                    <th>Gestión</th>
                                                    <th>Región</th>
                                                    <th class="text-center">Vacantes</th>
                                                    <th class="text-center">Postulantes</th>
                                                    <th class="text-end pe-3">Acciones</th>
                                                </tr>
                                            </thead>
                                            <tbody class="small">
                                                <% if (programas != null) {
                                                    for (ProgramaEstudio prog : programas) { %>
                                                <tr>
                                                    <td class="ps-3 fw-bold">#<%= prog.getIdPrograma() %></td>
                                                    <td class="fw-semibold"><%= prog.getInstitucion() %></td>
                                                    <td><%= prog.getProgramaEstudios() %></td>
                                                    <td>
                                                        <span class="badge <%= "PUBLICA".equals(prog.getTipoGestion()) ? "bg-info text-dark" : "bg-secondary" %>">
                                                            <%= prog.getTipoGestion() %>
                                                        </span>
                                                    </td>
                                                    <td><%= prog.getRegion() %></td>
                                                    <td class="text-center fw-bold text-primary fs-6"><%= prog.getVacantes() %></td>
                                                    <td class="text-center"><%= prog.getTotalPostulantes() %></td>
                                                    <td class="text-end pe-3">
                                                        <div class="d-inline-flex gap-1">
                                                            <a href="${pageContext.request.contextPath}/admin/programas?editarPrograma=<%= prog.getIdPrograma() %>"
                                                               class="btn btn-sm btn-outline-primary">
                                                                <i class="bi bi-pencil"></i>
                                                            </a>
                                                            <form action="${pageContext.request.contextPath}/admin/programas" method="post" class="d-inline"
                                                                  onsubmit="return confirm('¿Desea eliminar el programa <%= prog.getProgramaEstudios() %>?');">
                                                                <input type="hidden" name="accion" value="eliminarPrograma">
                                                                <input type="hidden" name="idPrograma" value="<%= prog.getIdPrograma() %>">
                                                                <button type="submit" class="btn btn-sm btn-outline-danger">
                                                                    <i class="bi bi-trash3"></i>
                                                                </button>
                                                            </form>
                                                        </div>
                                                    </td>
                                                </tr>
                                                <%  }
                                                   } %>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>

                            <div class="card border-0 shadow-sm rounded-4">
                                <div class="card-header bg-white border-0 pt-3 px-4 pb-2">
                                    <h6 class="fw-bold mb-0">Instituciones de Educación Superior (IES) Registradas</h6>
                                </div>
                                <div class="card-body p-0">
                                    <div class="table-responsive">
                                        <table class="table table-sm table-hover align-middle mb-0 small">
                                            <thead class="table-light">
                                                <tr>
                                                    <th class="ps-3">ID</th>
                                                    <th>Institución</th>
                                                    <th>Gestión</th>
                                                    <th>Región</th>
                                                    <th class="text-end pe-3">Acciones</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <% if (instituciones != null) {
                                                    for (Institucion i : instituciones) { %>
                                                <tr>
                                                    <td class="ps-3">#<%= i.getIdInstitucion() %></td>
                                                    <td class="fw-semibold"><%= i.getNombre() %></td>
                                                    <td><%= i.getTipoGestion() %></td>
                                                    <td><%= i.getRegion() %></td>
                                                    <td class="text-end pe-3">
                                                        <a href="${pageContext.request.contextPath}/admin/programas?editarInstitucion=<%= i.getIdInstitucion() %>"
                                                           class="btn btn-sm btn-link py-0">Editar</a>
                                                    </td>
                                                </tr>
                                                <%  }
                                                   } %>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </main>
            </div>
        </div>

        <jsp:include page="/WEB-INF/views/shared/footer.jsp" />
    </body>
</html>
