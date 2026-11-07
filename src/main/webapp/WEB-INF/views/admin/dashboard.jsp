<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="pe.edu.sibeca18.model.Etapa"%>
<%@page import="pe.edu.sibeca18.model.Modalidad"%>
<%@page import="pe.edu.sibeca18.model.ProgramaEstudio"%>
<%
    Map<String, Integer> stats = (Map<String, Integer>) request.getAttribute("estadisticas");
    List<Etapa> etapas = (List<Etapa>) request.getAttribute("etapas");
    List<Modalidad> modalidades = (List<Modalidad>) request.getAttribute("modalidades");
    List<String> ultimosEventos = (List<String>) request.getAttribute("ultimosEventos");
    int totalPostulantes = (stats != null && stats.get("TOTAL") != null) ? stats.get("TOTAL") : 0;
    int totalPreseleccionados = (stats != null) ? (stats.getOrDefault("PRESELECCIONADO", 0) + stats.getOrDefault("POSTULO A SELECCION", 0) + stats.getOrDefault("SELECCIONADO", 0) + stats.getOrDefault("NO SELECCIONADO", 0)) : 0;
    int totalPostularonSeleccion = (stats != null) ? stats.getOrDefault("POSTULO A SELECCION", 0) : 0;
    int totalSeleccionados = (stats != null) ? stats.getOrDefault("SELECCIONADO", 0) : 0;
%>
<!DOCTYPE html>
<html lang="es-PE">
    <head>
        <title>Dashboard Administrativo | SIBECA-18</title>
        <jsp:include page="/WEB-INF/views/shared/head.jsp" />
    </head>
    <body>
        <jsp:include page="/WEB-INF/views/shared/navbar.jsp" />

        <div class="container-fluid">
            <div class="row">
                <jsp:include page="/WEB-INF/views/shared/admin-sidebar.jsp" />

                <main class="col-lg-10 col-md-9 p-4">
                    <div class="d-flex flex-wrap justify-content-between align-items-center mb-4">
                        <div>
                            <span class="badge bg-primary mb-1">MÓDULO DE ADMINISTRACIÓN · PRONABEC</span>
                            <h3 class="fw-bold mb-0">Panel de Control - Convocatoria 2027</h3>
                            <p class="text-muted small mb-0">
                                Fecha de corte parametrizable para edad (RN-02): <strong>${fechaCorteFormateada}</strong>
                            </p>
                        </div>
                        <div class="d-flex gap-2">
                            <a href="${pageContext.request.contextPath}/admin/postulantes" class="btn btn-primary btn-sm fw-semibold">
                                <i class="bi bi-people me-1"></i>Gestionar Postulantes
                            </a>
                            <a href="${pageContext.request.contextPath}/admin/seleccion" class="btn btn-outline-primary btn-sm fw-semibold">
                                <i class="bi bi-trophy me-1"></i>Ejecutar Selección Final
                            </a>
                        </div>
                    </div>

                    <!-- Tarjetas estadísticas principales -->
                    <div class="row g-3 mb-4">
                        <div class="col-xl-3 col-sm-6">
                            <div class="stat-card p-3 d-flex align-items-center justify-content-between">
                                <div>
                                    <div class="text-muted small fw-semibold">Total de Postulantes</div>
                                    <div class="fs-2 fw-bold text-dark"><%= totalPostulantes %></div>
                                    <div class="small text-primary">Inscritos en la convocatoria</div>
                                </div>
                                <div class="stat-icon bg-primary bg-opacity-10 text-primary">
                                    <i class="bi bi-people-fill"></i>
                                </div>
                            </div>
                        </div>

                        <div class="col-xl-3 col-sm-6">
                            <div class="stat-card p-3 d-flex align-items-center justify-content-between">
                                <div>
                                    <div class="text-muted small fw-semibold">Superaron Puntaje ENP</div>
                                    <div class="fs-2 fw-bold text-primary"><%= totalPreseleccionados %></div>
                                    <div class="small text-muted">≥ 84 Ord. / ≥ 66 Esp. (RN-09)</div>
                                </div>
                                <div class="stat-icon bg-info bg-opacity-10 text-info">
                                    <i class="bi bi-clipboard2-check-fill"></i>
                                </div>
                            </div>
                        </div>

                        <div class="col-xl-3 col-sm-6">
                            <div class="stat-card p-3 d-flex align-items-center justify-content-between">
                                <div>
                                    <div class="text-muted small fw-semibold">Postularon a Selección</div>
                                    <div class="fs-2 fw-bold text-indigo" style="color:#4338ca;"><%= totalPostularonSeleccion %></div>
                                    <div class="small text-muted">Pendientes de ranking</div>
                                </div>
                                <div class="stat-icon bg-warning bg-opacity-10 text-warning">
                                    <i class="bi bi-building-check"></i>
                                </div>
                            </div>
                        </div>

                        <div class="col-xl-3 col-sm-6">
                            <div class="stat-card p-3 d-flex align-items-center justify-content-between">
                                <div>
                                    <div class="text-muted small fw-semibold">Becarios Seleccionados</div>
                                    <div class="fs-2 fw-bold text-success"><%= totalSeleccionados %></div>
                                    <div class="small text-success">Ganadores por orden de mérito</div>
                                </div>
                                <div class="stat-icon bg-success bg-opacity-10 text-success">
                                    <i class="bi bi-trophy-fill"></i>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="row g-4">
                        <!-- Tabla de las 9 Modalidades y sus Reglas Parametrizadas -->
                        <div class="col-lg-7">
                            <div class="card border-0 shadow-sm rounded-4 h-100">
                                <div class="card-header bg-white border-0 pt-4 px-4 pb-0">
                                    <h5 class="fw-bold mb-0"><i class="bi bi-bookmark-star-fill text-primary me-2"></i>Nueve Modalidades de Beca 18 (RN-02 y RN-09)</h5>
                                </div>
                                <div class="card-body p-4">
                                    <div class="table-responsive">
                                        <table class="table table-sm table-hover table-sibeca align-middle mb-0">
                                            <thead>
                                                <tr>
                                                    <th>#</th>
                                                    <th>Modalidad</th>
                                                    <th>Tipo</th>
                                                    <th>Límite de Edad (RN-02)</th>
                                                    <th class="text-center">Mín. ENP (RN-09)</th>
                                                </tr>
                                            </thead>
                                            <tbody class="small">
                                                <% if (modalidades != null) {
                                                    for (Modalidad m : modalidades) { %>
                                                <tr>
                                                    <td><%= m.getIdModalidad() %></td>
                                                    <td class="fw-semibold"><%= m.getNombre() %></td>
                                                    <td>
                                                        <span class="badge <%= m.esOrdinaria() ? "bg-primary" : "bg-info text-dark" %>">
                                                            <%= m.getTipo() %>
                                                        </span>
                                                    </td>
                                                    <td><%= m.getEdadMaximaTexto() %></td>
                                                    <td class="text-center fw-bold"><%= m.getPuntajeMinimoEnp() %> pts</td>
                                                </tr>
                                                <%  }
                                                   } %>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Resumen de Etapas y Bitácora Reciente -->
                        <div class="col-lg-5">
                            <div class="card border-0 shadow-sm rounded-4 mb-4">
                                <div class="card-header bg-white border-0 pt-4 px-4 pb-0 d-flex justify-content-between align-items-center">
                                    <h5 class="fw-bold mb-0"><i class="bi bi-calendar-check text-primary me-2"></i>Estado del Cronograma</h5>
                                    <a href="${pageContext.request.contextPath}/admin/cronograma" class="btn btn-sm btn-link">Editar</a>
                                </div>
                                <div class="card-body p-4">
                                    <ul class="list-group list-group-flush small">
                                        <% if (etapas != null) {
                                            for (Etapa e : etapas) { %>
                                        <li class="list-group-item px-0 d-flex justify-content-between align-items-center">
                                            <div>
                                                <span class="fw-bold text-primary"><%= e.getOrden() %>.</span>
                                                <%= e.getNombre() %>
                                                <div class="text-muted" style="font-size: 0.75rem;"><%= e.getRangoFechasFormateado() %></div>
                                            </div>
                                            <span class="badge <%= e.isPublicada() ? "bg-success" : "bg-secondary" %>">
                                                <%= e.isPublicada() ? "Publicada" : "Oculta" %>
                                            </span>
                                        </li>
                                        <%  }
                                           } %>
                                    </ul>
                                </div>
                            </div>

                            <div class="card border-0 shadow-sm rounded-4">
                                <div class="card-header bg-white border-0 pt-4 px-4 pb-0 d-flex justify-content-between align-items-center">
                                    <h6 class="fw-bold mb-0"><i class="bi bi-journal-code text-primary me-2"></i>Últimos Eventos en Bitácora TXT (RF-16)</h6>
                                    <a href="${pageContext.request.contextPath}/admin/seleccion?accion=bitacora" class="small">Ver todo</a>
                                </div>
                                <div class="card-body p-3">
                                    <div class="bg-dark text-light rounded-3 p-3 small font-monospace" style="max-height: 180px; overflow-y: auto; font-size: 0.74rem;">
                                        <% if (ultimosEventos == null || ultimosEventos.isEmpty()) { %>
                                            <div>Sin eventos registrados aún en el archivo.</div>
                                        <% } else {
                                            for (String ev : ultimosEventos) { %>
                                            <div class="mb-1 border-bottom border-secondary pb-1"><%= ev %></div>
                                        <%  }
                                           } %>
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
