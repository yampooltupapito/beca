<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="pe.edu.sibeca18.model.Modalidad"%>
<%@page import="pe.edu.sibeca18.model.Postulante"%>
<%
    List<Postulante> postulantes = (List<Postulante>) request.getAttribute("postulantes");
    List<Modalidad> modalidades = (List<Modalidad>) request.getAttribute("modalidades");
    Integer filtroModalidad = (Integer) request.getAttribute("filtroModalidad");
    String filtroEstado = (String) request.getAttribute("filtroEstado");
    String[] estados = {
        "TODOS", "INSCRITO", "EN REVISION", "APTO", "NO APTO",
        "PRESELECCIONADO", "NO PRESELECCIONADO", "POSTULO A SELECCION",
        "SELECCIONADO", "NO SELECCIONADO"
    };
%>
<!DOCTYPE html>
<html lang="es-PE">
    <head>
        <title>Gestión de Postulantes | SIBECA-18</title>
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
                            <span class="badge bg-primary mb-1">RF-09 · RF-10 · RF-11 · GESTIÓN Y EVALUACIÓN DE POSTULANTES</span>
                            <h3 class="fw-bold mb-0">Padrón de Postulantes - Convocatoria 2027</h3>
                        </div>
                        <div>
                            <a href="${pageContext.request.contextPath}/inscripcion" class="btn btn-outline-primary btn-sm">
                                <i class="bi bi-person-plus me-1"></i>Ir a Formulario de Inscripción
                            </a>
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

                    <!-- Barra de filtros por Modalidad, Estado y búsqueda por DNI (RF-09) -->
                    <div class="card border-0 shadow-sm rounded-4 mb-4">
                        <div class="card-body p-3">
                            <form action="${pageContext.request.contextPath}/admin/postulantes" method="get" class="row g-2 align-items-end">
                                <div class="col-md-4">
                                    <label class="form-label small fw-semibold mb-1">Filtrar por Modalidad</label>
                                    <select name="idModalidad" class="form-select form-select-sm">
                                        <option value="0">-- Todas las modalidades (9) --</option>
                                        <% if (modalidades != null) {
                                            for (Modalidad m : modalidades) {
                                                boolean sel = (filtroModalidad != null && filtroModalidad.equals(m.getIdModalidad()));
                                        %>
                                        <option value="<%= m.getIdModalidad() %>" <%= sel ? "selected" : "" %>><%= m.getNombre() %></option>
                                        <%  }
                                           } %>
                                    </select>
                                </div>

                                <div class="col-md-3">
                                    <label class="form-label small fw-semibold mb-1">Filtrar por Estado</label>
                                    <select name="estado" class="form-select form-select-sm">
                                        <% for (String est : estados) {
                                            boolean sel = est.equalsIgnoreCase(filtroEstado);
                                        %>
                                        <option value="<%= est %>" <%= sel ? "selected" : "" %>><%= est %></option>
                                        <% } %>
                                    </select>
                                </div>

                                <div class="col-md-3">
                                    <label class="form-label small fw-semibold mb-1">Buscar por DNI</label>
                                    <input type="text" name="dni" class="form-control form-control-sm"
                                           value="${filtroDni}" placeholder="Ingrese DNI (8 dígitos)" maxlength="8">
                                </div>

                                <div class="col-md-2 d-flex gap-2">
                                    <button type="submit" class="btn btn-primary btn-sm w-100 fw-semibold">
                                        <i class="bi bi-search me-1"></i>Filtrar
                                    </button>
                                    <a href="${pageContext.request.contextPath}/admin/postulantes" class="btn btn-outline-secondary btn-sm">
                                        Limpiar
                                    </a>
                                </div>
                            </form>
                        </div>
                    </div>

                    <!-- Tabla de Postulantes -->
                    <div class="card border-0 shadow-sm rounded-4">
                        <div class="card-body p-0">
                            <div class="table-responsive">
                                <table class="table table-hover table-sibeca align-middle mb-0">
                                    <thead>
                                        <tr>
                                            <th class="ps-3">N.°</th>
                                            <th>DNI</th>
                                            <th>Apellidos y Nombres</th>
                                            <th>Edad Corte</th>
                                            <th>Modalidad</th>
                                            <th class="text-center">Puntaje ENP</th>
                                            <th>Estado Actual</th>
                                            <th class="text-end pe-3">Acciones</th>
                                        </tr>
                                    </thead>
                                    <tbody class="small">
                                        <% if (postulantes == null || postulantes.isEmpty()) { %>
                                        <tr>
                                            <td colspan="8" class="text-center py-4 text-muted">
                                                No se encontraron postulantes con los criterios de búsqueda seleccionados.
                                            </td>
                                        </tr>
                                        <% } else {
                                            for (Postulante p : postulantes) {
                                                String badgeCls = switch (p.getEstado()) {
                                                    case "INSCRITO" -> "estado-inscrito";
                                                    case "EN REVISION" -> "estado-revision";
                                                    case "APTO" -> "estado-apto";
                                                    case "NO APTO" -> "estado-no-apto";
                                                    case "PRESELECCIONADO" -> "estado-preseleccionado";
                                                    case "NO PRESELECCIONADO" -> "estado-no-preseleccionado";
                                                    case "POSTULO A SELECCION" -> "estado-postulo";
                                                    case "SELECCIONADO" -> "estado-seleccionado";
                                                    case "NO SELECCIONADO" -> "estado-no-seleccionado";
                                                    default -> "estado-inscrito";
                                                };
                                        %>
                                        <tr>
                                            <td class="ps-3 fw-bold"><%= p.getIdPostulante() %></td>
                                            <td class="font-monospace fw-semibold"><%= p.getDni() %></td>
                                            <td>
                                                <div class="fw-semibold text-dark"><%= p.getNombreCompleto() %></div>
                                                <div class="text-muted" style="font-size: 0.75rem;"><%= p.getRegion() %> · <%= p.getColegio() %></div>
                                            </td>
                                            <td><%= p.getEdadCorte() %> años</td>
                                            <td>
                                                <span class="fw-semibold"><%= p.getModalidadNombre() %></span>
                                                <div class="text-muted" style="font-size: 0.72rem;">Mín. ENP: <%= p.getModalidad().getPuntajeMinimoEnp() %></div>
                                            </td>
                                            <td class="text-center fw-bold text-primary">
                                                <%= p.getPuntajeEnpFormateado() %>
                                            </td>
                                            <td>
                                                <span class="badge-estado <%= badgeCls %>"><%= p.getEstado() %></span>
                                            </td>
                                            <td class="text-end pe-3">
                                                <div class="d-inline-flex gap-1">
                                                    <a href="${pageContext.request.contextPath}/admin/postulantes?accion=detalle&id=<%= p.getIdPostulante() %>"
                                                       class="btn btn-sm btn-primary">
                                                        <i class="bi bi-eye me-1"></i>Evaluar / Detalle
                                                    </a>
                                                    <% if ("INSCRITO".equals(p.getEstado())) { %>
                                                    <form action="${pageContext.request.contextPath}/admin/postulantes" method="post" class="d-inline"
                                                          onsubmit="return confirm('¿Confirma eliminar la inscripción en estado INSCRITO del DNI <%= p.getDni() %> (RN-07)?');">
                                                        <input type="hidden" name="accion" value="eliminar">
                                                        <input type="hidden" name="idPostulante" value="<%= p.getIdPostulante() %>">
                                                        <button type="submit" class="btn btn-sm btn-outline-danger" title="Eliminar inscripción en estado INSCRITO">
                                                            <i class="bi bi-trash3"></i>
                                                        </button>
                                                    </form>
                                                    <% } %>
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
                </main>
            </div>
        </div>

        <jsp:include page="/WEB-INF/views/shared/footer.jsp" />
    </body>
</html>
