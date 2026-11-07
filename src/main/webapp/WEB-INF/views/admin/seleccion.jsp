<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="pe.edu.sibeca18.model.PostulacionSeleccion"%>
<%@page import="pe.edu.sibeca18.model.ProgramaEstudio"%>
<%
    Map<ProgramaEstudio, List<PostulacionSeleccion>> rankingPorPrograma =
            (Map<ProgramaEstudio, List<PostulacionSeleccion>>) request.getAttribute("rankingPorPrograma");
    Map<String, Integer> totalesPorPrograma = (Map<String, Integer>) request.getAttribute("totalesPorPrograma");
    Map<String, Integer> totalesPorModalidad = (Map<String, Integer>) request.getAttribute("totalesPorModalidad");
    List<PostulacionSeleccion> seleccionados = (List<PostulacionSeleccion>) request.getAttribute("seleccionados");
%>
<!DOCTYPE html>
<html lang="es-PE">
    <head>
        <title>Ranking, Selección Final y Reportes | SIBECA-18</title>
        <jsp:include page="/WEB-INF/views/shared/head.jsp" />
    </head>
    <body>
        <jsp:include page="/WEB-INF/views/shared/navbar.jsp" />

        <div class="container-fluid">
            <div class="row">
                <jsp:include page="/WEB-INF/views/shared/admin-sidebar.jsp" />

                <main class="col-lg-10 col-md-9 p-4">
                    <div class="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-2">
                        <div>
                            <span class="badge bg-primary mb-1">RF-14 · RF-15 · RN-12 · RANKING CON COLECCIONES Y SELECCIÓN TRANSACCIONAL</span>
                            <h3 class="fw-bold mb-0">Ejecución de la Selección Final y Reporte de Becarios</h3>
                            <p class="text-muted small mb-0">
                                Orden de mérito por puntaje ENP descendente y desempate por fecha/hora de postulación ascendente (RN-12 / CP-15).
                            </p>
                        </div>
                        <div class="d-flex gap-2">
                            <a href="${pageContext.request.contextPath}/admin/seleccion?accion=exportarReporteTxt"
                               class="btn btn-dark btn-sm fw-semibold">
                                <i class="bi bi-file-earmark-arrow-down me-1"></i>Exportar Reporte TXT (RF-15)
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
                        <i class="bi bi-x-octagon-fill me-2"></i><strong>Transacción Revertida (ROLLBACK):</strong> ${error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                    <% } %>

                    <!-- Panel de Ejecución Transaccional (RF-14, CP-15, CP-16) y Reto Opcional con Hilos (7.4) -->
                    <div class="row g-3 mb-4">
                        <div class="col-lg-8">
                            <div class="card border-0 shadow-sm rounded-4 h-100">
                                <div class="card-body p-4">
                                    <h5 class="fw-bold text-primary mb-2">
                                        <i class="bi bi-cpu-fill me-2"></i>Ejecutar Selección Final en Una Sola Transacción JDBC (RF-14)
                                    </h5>
                                    <p class="small text-secondary mb-3">
                                        Asigna vacantes por programa declarando <code>SELECCIONADO</code> a quienes alcancen vacante y
                                        <code>NO SELECCIONADO</code> al resto. Si ocurre cualquier error, ejecuta <code>conn.rollback()</code> completo.
                                    </p>
                                    <form action="${pageContext.request.contextPath}/admin/seleccion" method="post"
                                          class="d-flex flex-wrap align-items-center justify-content-between gap-3">
                                        <input type="hidden" name="accion" value="ejecutarSeleccion">

                                        <div class="form-check form-switch p-2 px-3 bg-warning bg-opacity-10 border border-warning rounded-3">
                                            <input class="form-check-input ms-0 me-2" type="checkbox" id="simularErrorControlado"
                                                   name="simularErrorControlado" value="true">
                                            <label class="form-check-label small fw-semibold text-dark" for="simularErrorControlado">
                                                <strong>Modo Prueba CP-16:</strong> Provocar error controlado a mitad del proceso para demostrar <code>ROLLBACK</code> total.
                                            </label>
                                        </div>

                                        <button type="submit" class="btn btn-primary fw-semibold px-4">
                                            <i class="bi bi-trophy-fill me-2"></i>Ejecutar Selección Transaccional
                                        </button>
                                    </form>
                                </div>
                            </div>
                        </div>

                        <div class="col-lg-4">
                            <div class="card border-0 shadow-sm rounded-4 h-100">
                                <div class="card-body p-4">
                                    <div class="d-flex justify-content-between align-items-center mb-2">
                                        <h6 class="fw-bold mb-0"><i class="bi bi-lightning-charge-fill text-warning me-1"></i>Reto 7.4: Hilo en 2.° Plano</h6>
                                        <span class="badge bg-info text-dark">${estadoTareaHilo}</span>
                                    </div>
                                    <p class="small text-muted mb-2">${mensajeTareaHilo}</p>
                                    <form action="${pageContext.request.contextPath}/admin/seleccion" method="post" class="d-flex gap-2">
                                        <input type="hidden" name="accion" value="ejecutarSeleccionHilo">
                                        <button type="submit" class="btn btn-sm btn-outline-primary w-100 fw-semibold">
                                            <i class="bi bi-play-fill me-1"></i>Ejecutar con Hilo Sincronizado
                                        </button>
                                        <a href="${pageContext.request.contextPath}/admin/seleccion" class="btn btn-sm btn-light border">
                                            <i class="bi bi-arrow-clockwise"></i>
                                        </a>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Resumen de Totales Calculados con Colecciones (RF-15) -->
                    <div class="row g-4 mb-4">
                        <div class="col-md-6">
                            <div class="card border-0 shadow-sm rounded-4 h-100">
                                <div class="card-header bg-white border-0 pt-3 px-4 pb-0">
                                    <h6 class="fw-bold mb-0"><i class="bi bi-bar-chart-fill text-primary me-2"></i>Seleccionados por Programa de Estudios (Colecciones Map)</h6>
                                </div>
                                <div class="card-body p-3">
                                    <% if (totalesPorPrograma == null || totalesPorPrograma.isEmpty()) { %>
                                    <div class="text-muted small">Aún no hay seleccionados. Ejecute el proceso de selección arriba.</div>
                                    <% } else { %>
                                    <ul class="list-group list-group-flush small">
                                        <% for (Map.Entry<String, Integer> entry : totalesPorPrograma.entrySet()) { %>
                                        <li class="list-group-item d-flex justify-content-between align-items-center px-1">
                                            <span><%= entry.getKey() %></span>
                                            <span class="badge bg-success rounded-pill"><%= entry.getValue() %> seleccionado(s)</span>
                                        </li>
                                        <% } %>
                                    </ul>
                                    <% } %>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <div class="card border-0 shadow-sm rounded-4 h-100">
                                <div class="card-header bg-white border-0 pt-3 px-4 pb-0">
                                    <h6 class="fw-bold mb-0"><i class="bi bi-pie-chart-fill text-primary me-2"></i>Seleccionados por Modalidad de Beca 18 (Colecciones Map)</h6>
                                </div>
                                <div class="card-body p-3">
                                    <% if (totalesPorModalidad == null || totalesPorModalidad.isEmpty()) { %>
                                    <div class="text-muted small">Aún no hay seleccionados registrados por modalidad.</div>
                                    <% } else { %>
                                    <ul class="list-group list-group-flush small">
                                        <% for (Map.Entry<String, Integer> entry : totalesPorModalidad.entrySet()) { %>
                                        <li class="list-group-item d-flex justify-content-between align-items-center px-1">
                                            <span class="fw-semibold"><%= entry.getKey() %></span>
                                            <span class="badge bg-primary rounded-pill"><%= entry.getValue() %> becario(s)</span>
                                        </li>
                                        <% } %>
                                    </ul>
                                    <% } %>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Ranking Detallado por Programa (RN-12, CP-15) -->
                    <h5 class="fw-bold mb-3"><i class="bi bi-list-ol text-primary me-2"></i>Ranking de Mérito y Desempate por Programa (RN-12)</h5>

                    <% if (rankingPorPrograma != null) {
                        for (Map.Entry<ProgramaEstudio, List<PostulacionSeleccion>> entry : rankingPorPrograma.entrySet()) {
                            ProgramaEstudio prog = entry.getKey();
                            List<PostulacionSeleccion> postulaciones = entry.getValue();
                    %>
                    <div class="card border-0 shadow-sm rounded-4 mb-3">
                        <div class="card-header bg-white border-bottom py-3 px-4 d-flex flex-wrap justify-content-between align-items-center">
                            <div>
                                <span class="badge bg-dark me-2">Programa #<%= prog.getIdPrograma() %></span>
                                <strong class="text-primary"><%= prog.getInstitucion() %></strong> — <strong><%= prog.getProgramaEstudios() %></strong>
                                <span class="text-muted small ms-2">(<%= prog.getTipoGestion() %> · <%= prog.getRegion() %>)</span>
                            </div>
                            <div>
                                <span class="badge bg-primary fs-6">Vacantes Disponibles: <%= prog.getVacantes() %></span>
                            </div>
                        </div>
                        <div class="card-body p-0">
                            <% if (postulaciones == null || postulaciones.isEmpty()) { %>
                            <div class="p-3 text-muted small">Sin postulaciones registradas en este programa.</div>
                            <% } else { %>
                            <div class="table-responsive">
                                <table class="table table-sm table-hover align-middle mb-0 small">
                                    <thead class="table-light">
                                        <tr>
                                            <th class="ps-4">Orden Mérito</th>
                                            <th>DNI</th>
                                            <th>Postulante</th>
                                            <th>Modalidad</th>
                                            <th class="text-center">Puntaje ENP (1.° Criterio)</th>
                                            <th>Fecha y Hora Exacta de Postulación (2.° Criterio Desempate)</th>
                                            <th class="text-center pe-4">Resultado Selección</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% for (PostulacionSeleccion ps : postulaciones) {
                                            boolean ganaVacante = ps.getOrdenMerito() != null && ps.getOrdenMerito() <= prog.getVacantes();
                                        %>
                                        <tr class="<%= "SELECCIONADO".equals(ps.getResultado()) ? "table-success" : "" %>">
                                            <td class="ps-4 fw-bold">
                                                <span class="badge <%= ganaVacante ? "bg-primary" : "bg-secondary" %>">
                                                    #<%= ps.getOrdenMerito() %>
                                                </span>
                                            </td>
                                            <td class="font-monospace fw-semibold"><%= ps.getDniPostulante() %></td>
                                            <td class="fw-semibold"><%= ps.getNombrePostulante() %></td>
                                            <td><%= ps.getModalidadNombre() %></td>
                                            <td class="text-center fw-bold text-primary"><%= ps.getPuntajeEnpFormateado() %></td>
                                            <td class="font-monospace"><%= ps.getFechaHoraFormateada() %></td>
                                            <td class="text-center pe-4">
                                                <% if ("SELECCIONADO".equals(ps.getResultado())) { %>
                                                <span class="badge bg-success"><i class="bi bi-trophy-fill me-1"></i>SELECCIONADO</span>
                                                <% } else if ("NO SELECCIONADO".equals(ps.getResultado())) { %>
                                                <span class="badge bg-danger">NO SELECCIONADO</span>
                                                <% } else { %>
                                                <span class="badge bg-warning text-dark">PENDIENTE (Proyección: <%= ganaVacante ? "GANA VACANTE" : "SIN VACANTE" %>)</span>
                                                <% } %>
                                            </td>
                                        </tr>
                                        <% } %>
                                    </tbody>
                                </table>
                            </div>
                            <% } %>
                        </div>
                    </div>
                    <%  }
                       } %>
                </main>
            </div>
        </div>

        <jsp:include page="/WEB-INF/views/shared/footer.jsp" />
    </body>
</html>
