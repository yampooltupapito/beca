<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="pe.edu.sibeca18.model.Etapa"%>
<%@page import="pe.edu.sibeca18.model.Postulante"%>
<%
    Postulante p = (Postulante) request.getAttribute("postulante");
    List<Etapa> etapas = (List<Etapa>) request.getAttribute("etapas");
    int etapaActualNum = (p != null) ? p.getEtapaActualProceso() : 1;

    String claseBadge = "estado-inscrito";
    if (p != null && p.getEstado() != null) {
        claseBadge = switch (p.getEstado()) {
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
    }
%>
<!DOCTYPE html>
<html lang="es-PE">
    <head>
        <title>Mi Estado y Línea de Tiempo | SIBECA-18</title>
        <jsp:include page="/WEB-INF/views/shared/head.jsp" />
    </head>
    <body>
        <jsp:include page="/WEB-INF/views/shared/navbar.jsp" />

        <div class="container my-4">
            <% if (request.getAttribute("exito") != null) { %>
            <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
                <i class="bi bi-check-circle-fill me-2"></i>${exito}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <% } %>

            <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
                <i class="bi bi-exclamation-octagon-fill me-2"></i>${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <% } %>

            <!-- Cabecera de resumen del beneficiario -->
            <div class="card border-0 shadow-sm rounded-4 mb-4">
                <div class="card-body p-4 d-flex flex-wrap justify-content-between align-items-center gap-3">
                    <div>
                        <span class="text-uppercase small fw-bold text-primary">Expediente de Postulación · Convocatoria 2027</span>
                        <h3 class="fw-bold mb-1">${postulante.nombreCompleto}</h3>
                        <div class="text-secondary small">
                            <span class="me-3"><i class="bi bi-credit-card-2-front me-1"></i><strong>DNI:</strong> ${postulante.dni}</span>
                            <span class="me-3"><i class="bi bi-award me-1"></i><strong>Modalidad:</strong> ${postulante.modalidadNombre}</span>
                            <span><i class="bi bi-geo-alt me-1"></i><strong>Región:</strong> ${postulante.region}</span>
                        </div>
                    </div>
                    <div class="text-md-end">
                        <div class="small text-muted mb-1">Estado Vigente del Proceso</div>
                        <span class="badge-estado <%= claseBadge %> fs-6">${postulante.estado}</span>
                        <div class="mt-2">
                            <% if (p != null && "INSCRITO".equals(p.getEstado())) { %>
                            <a href="${pageContext.request.contextPath}/postulante/panel?vista=editar"
                               class="btn btn-sm btn-outline-primary">
                                <i class="bi bi-pencil-square me-1"></i>Editar Mi Inscripción (RN-07)
                            </a>
                            <% } else { %>
                            <a href="${pageContext.request.contextPath}/postulante/panel?vista=editar"
                               class="btn btn-sm btn-outline-secondary" title="Comprobar bloqueo RN-07 / CP-10">
                                <i class="bi bi-lock-fill me-1"></i>Edición Bloqueada (Estado != INSCRITO)
                            </a>
                            <% } %>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row g-4">
                <!-- Columna izquierda: Ficha de Datos Registrados -->
                <div class="col-lg-5">
                    <div class="card border-0 shadow-sm rounded-4 h-100">
                        <div class="card-header bg-white border-0 pt-4 px-4 pb-0">
                            <h5 class="fw-bold text-dark mb-0">
                                <i class="bi bi-file-earmark-person text-primary me-2"></i>Mis Datos de Inscripción
                            </h5>
                        </div>
                        <div class="card-body p-4">
                            <dl class="row mb-0 small">
                                <dt class="col-sm-5 text-muted py-1">DNI / Usuario:</dt>
                                <dd class="col-sm-7 fw-semibold py-1">${postulante.dni}</dd>

                                <dt class="col-sm-5 text-muted py-1">Fecha de Nacimiento:</dt>
                                <dd class="col-sm-7 py-1">${postulante.fechaNacimientoFormateada} (${postulante.edadCorte} años al corte)</dd>

                                <dt class="col-sm-5 text-muted py-1">Nacionalidad:</dt>
                                <dd class="col-sm-7 py-1">${postulante.nacionalidad}</dd>

                                <dt class="col-sm-5 text-muted py-1">Correo Electrónico:</dt>
                                <dd class="col-sm-7 py-1">${postulante.correo}</dd>

                                <dt class="col-sm-5 text-muted py-1">Celular:</dt>
                                <dd class="col-sm-7 py-1">${postulante.celular}</dd>

                                <dt class="col-sm-5 text-muted py-1">Región / Colegio:</dt>
                                <dd class="col-sm-7 py-1">${postulante.region} - ${postulante.colegio}</dd>

                                <dt class="col-sm-5 text-muted py-1">Situación Académica:</dt>
                                <dd class="col-sm-7 py-1">${postulante.situacionLegible}</dd>

                                <dt class="col-sm-5 text-muted py-1">Tercio Superior:</dt>
                                <dd class="col-sm-7 py-1">${postulante.tercioSuperior ? 'Sí acredita' : 'No acredita'}</dd>

                                <dt class="col-sm-5 text-muted py-1">Clasificación SISFOH:</dt>
                                <dd class="col-sm-7 py-1">${postulante.clasificacionLegible}</dd>

                                <dt class="col-sm-5 text-muted py-1">Discapacidad CONADIS:</dt>
                                <dd class="col-sm-7 py-1">${postulante.discapacidadAcreditada ? 'Sí acredita' : 'No'}</dd>

                                <dt class="col-sm-5 text-muted py-1">Modalidad Elegida:</dt>
                                <dd class="col-sm-7 fw-bold text-primary py-1">${postulante.modalidadNombre}</dd>

                                <dt class="col-sm-5 text-muted py-1">Fecha de Inscripción:</dt>
                                <dd class="col-sm-7 py-1">${postulante.fechaInscripcionFormateada}</dd>
                            </dl>
                        </div>
                    </div>
                </div>

                <!-- Columna derecha: Línea de Tiempo del Proceso (RF-06) -->
                <div class="col-lg-7">
                    <div class="card border-0 shadow-sm rounded-4 h-100">
                        <div class="card-header bg-white border-0 pt-4 px-4 pb-0 d-flex justify-content-between align-items-center">
                            <h5 class="fw-bold text-dark mb-0">
                                <i class="bi bi-diagram-3-fill text-primary me-2"></i>Línea de Tiempo del Concurso Beca 18 (RF-06)
                            </h5>
                            <span class="badge bg-info text-dark">Etapa actual: #<%= etapaActualNum %></span>
                        </div>
                        <div class="card-body p-4">
                            <div class="timeline-sibeca">
                                <% if (etapas != null) {
                                    for (Etapa et : etapas) {
                                        String estadoClase = "";
                                        if (et.getOrden() < etapaActualNum) {
                                            estadoClase = "completada";
                                        } else if (et.getOrden() == etapaActualNum) {
                                            estadoClase = "actual";
                                        }
                                %>
                                <div class="timeline-item <%= estadoClase %>">
                                    <div class="timeline-marker">
                                        <% if (et.getOrden() < etapaActualNum) { %>
                                            <i class="bi bi-check-lg"></i>
                                        <% } else { %>
                                            <%= et.getOrden() %>
                                        <% } %>
                                    </div>
                                    <div class="d-flex flex-wrap justify-content-between align-items-center mb-1">
                                        <h6 class="fw-bold mb-0 <%= et.getOrden() == etapaActualNum ? "text-primary" : "text-dark" %>">
                                            <%= et.getNombre() %>
                                        </h6>
                                        <span class="badge <%= et.isPublicada() ? "bg-success" : "bg-secondary" %>" style="font-size: 0.7rem;">
                                            <%= et.isPublicada() ? "Etapa Publicada" : "En Proceso / No Publicada" %>
                                        </span>
                                    </div>
                                    <div class="small text-muted mb-1">
                                        <i class="bi bi-calendar-event me-1"></i><strong>Fechas programadas:</strong> <%= et.getRangoFechasFormateado() %>
                                    </div>
                                    <% if (et.getOrden() == etapaActualNum) { %>
                                    <div class="mt-2 p-2 rounded bg-primary bg-opacity-10 text-primary small fw-semibold">
                                        <i class="bi bi-geo-fill me-1"></i>Usted se encuentra actualmente en esta etapa con estado: <strong>${postulante.estado}</strong>.
                                    </div>
                                    <% } %>
                                </div>
                                <%  }
                                   } %>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <jsp:include page="/WEB-INF/views/shared/footer.jsp" />
    </body>
</html>
