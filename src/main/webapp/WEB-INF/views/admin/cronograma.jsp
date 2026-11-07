<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="pe.edu.sibeca18.model.Etapa"%>
<%
    List<Etapa> etapas = (List<Etapa>) request.getAttribute("etapas");
%>
<!DOCTYPE html>
<html lang="es-PE">
    <head>
        <title>Cronograma y Publicación de Resultados | SIBECA-18</title>
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
                            <span class="badge bg-primary mb-1">RF-12 · RN-02 · RN-13 · CRONOGRAMA PARAMETRIZABLE Y PUBLICACIÓN</span>
                            <h3 class="fw-bold mb-0">Configuración del Cronograma - Convocatoria 2027</h3>
                            <p class="text-muted small mb-0">
                                Edite las fechas de inicio y fin de cada etapa y controle la visibilidad de resultados para los postulantes (RN-13 / CP-18).
                            </p>
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

                    <!-- Parámetro de Fecha de Corte de Edad (RN-02) -->
                    <div class="card border-0 shadow-sm rounded-4 mb-4">
                        <div class="card-body p-3 d-flex flex-wrap justify-content-between align-items-center gap-3">
                            <div>
                                <h6 class="fw-bold mb-1 text-primary">
                                    <i class="bi bi-calendar2-heart-fill me-1"></i>Fecha de Corte Parametrizable para Cálculo de Edad (RN-02)
                                </h6>
                                <p class="small text-muted mb-0">
                                    Referencialmente corresponde a la fecha de publicación de las bases (09/10/2026). Actualmente configurada en: <strong>${fechaCorteFormateada}</strong>.
                                </p>
                            </div>
                            <form action="${pageContext.request.contextPath}/admin/cronograma" method="post" class="d-flex align-items-center gap-2">
                                <input type="hidden" name="accion" value="actualizarFechaCorte">
                                <input type="date" name="fechaCorteEdad" class="form-control form-control-sm" value="${fechaCorteIso}" required>
                                <button type="submit" class="btn btn-sm btn-primary fw-semibold text-nowrap">
                                    <i class="bi bi-save me-1"></i>Actualizar Corte
                                </button>
                            </form>
                        </div>
                    </div>

                    <!-- Tabla editable de las 8 etapas del concurso -->
                    <div class="card border-0 shadow-sm rounded-4">
                        <div class="card-body p-0">
                            <div class="table-responsive">
                                <table class="table table-hover table-sibeca align-middle mb-0">
                                    <thead>
                                        <tr>
                                            <th class="ps-3">N.°</th>
                                            <th>Etapa del Proceso (Referencia Convocatoria 2027)</th>
                                            <th>Fecha Inicio</th>
                                            <th>Fecha Fin</th>
                                            <th class="text-center">Vigencia Hoy</th>
                                            <th class="text-center">Publicada (RN-13)</th>
                                            <th class="text-end pe-3">Guardar / Publicar</th>
                                        </tr>
                                    </thead>
                                    <tbody class="small">
                                        <% if (etapas != null) {
                                            for (Etapa e : etapas) { %>
                                        <tr>
                                            <form action="${pageContext.request.contextPath}/admin/cronograma" method="post">
                                                <input type="hidden" name="accion" value="actualizarEtapa">
                                                <input type="hidden" name="idEtapa" value="<%= e.getIdEtapa() %>">
                                                <input type="hidden" name="descripcion" value="<%= e.getDescripcion() != null ? e.getDescripcion() : "" %>">

                                                <td class="ps-3 fw-bold"><%= e.getOrden() %></td>
                                                <td>
                                                    <input type="text" name="nombre" class="form-control form-control-sm fw-semibold"
                                                           value="<%= e.getNombre() %>" required>
                                                    <div class="text-muted mt-1" style="font-size: 0.73rem;">
                                                        Ref. bases: <%= e.getFechaReferencia() %>
                                                    </div>
                                                </td>
                                                <td>
                                                    <input type="date" name="fechaInicio" class="form-control form-control-sm"
                                                           value="<%= e.getFechaInicio() %>" required>
                                                </td>
                                                <td>
                                                    <input type="date" name="fechaFin" class="form-control form-control-sm"
                                                           value="<%= e.getFechaFin() %>" required>
                                                </td>
                                                <td class="text-center">
                                                    <span class="badge <%= e.isVigenteHoy() ? "bg-info text-dark" : "bg-light text-muted border" %>">
                                                        <%= e.isVigenteHoy() ? "VIGENTE" : "CERRADA" %>
                                                    </span>
                                                </td>
                                                <td class="text-center">
                                                    <div class="form-check form-switch d-inline-block">
                                                        <input class="form-check-input" type="checkbox" name="publicada" value="true"
                                                               <%= e.isPublicada() ? "checked" : "" %>>
                                                        <span class="badge <%= e.isPublicada() ? "bg-success" : "bg-secondary" %>">
                                                            <%= e.isPublicada() ? "SÍ" : "NO" %>
                                                        </span>
                                                    </div>
                                                </td>
                                                <td class="text-end pe-3">
                                                    <button type="submit" class="btn btn-sm btn-primary fw-semibold">
                                                        <i class="bi bi-check2 me-1"></i>Guardar
                                                    </button>
                                                </td>
                                            </form>
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
