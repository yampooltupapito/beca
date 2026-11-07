<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="pe.edu.sibeca18.model.PostulacionSeleccion"%>
<%@page import="pe.edu.sibeca18.model.Postulante"%>
<%
    Postulante p = (Postulante) request.getAttribute("postulante");
    PostulacionSeleccion postulacion = (PostulacionSeleccion) request.getAttribute("postulacionActual");
    Boolean pubRevObj = (Boolean) request.getAttribute("publicadaRevision");
    Boolean pubPreObj = (Boolean) request.getAttribute("publicadaPreseleccion");
    Boolean pubSelObj = (Boolean) request.getAttribute("publicadaSeleccion");

    boolean publicadaRevision = pubRevObj != null && pubRevObj;
    boolean publicadaPreseleccion = pubPreObj != null && pubPreObj;
    boolean publicadaSeleccion = pubSelObj != null && pubSelObj;
%>
<!DOCTYPE html>
<html lang="es-PE">
    <head>
        <title>Mis Resultados Oficiales | SIBECA-18</title>
        <jsp:include page="/WEB-INF/views/shared/head.jsp" />
    </head>
    <body>
        <jsp:include page="/WEB-INF/views/shared/navbar.jsp" />

        <div class="container my-4">
            <div class="d-flex flex-wrap justify-content-between align-items-center mb-4">
                <div>
                    <span class="badge bg-primary mb-1">RF-07 · RN-13 · PUBLICACIÓN CONTROLADA DE RESULTADOS</span>
                    <h3 class="fw-bold mb-0">Mis Resultados Oficiales - Convocatoria 2027</h3>
                    <p class="text-muted small mb-0">
                        De acuerdo con la Regla de Negocio <strong>RN-13</strong>, cada resultado solo es visible cuando el administrador marca la etapa correspondiente como publicada.
                    </p>
                </div>
            </div>

            <div class="row g-4">
                <!-- 1. Resultado de Revisión Documental (Etapa 3) -->
                <div class="col-md-4">
                    <div class="card border-0 shadow-sm rounded-4 h-100">
                        <div class="card-body p-4">
                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <span class="badge bg-light text-primary border">Etapa 3</span>
                                <span class="badge <%= publicadaRevision ? "bg-success" : "bg-warning text-dark" %>">
                                    <%= publicadaRevision ? "PUBLICADA" : "PENDIENTE DE PUBLICACIÓN" %>
                                </span>
                            </div>
                            <h5 class="fw-bold">1. Revisión de Inscripción</h5>
                            <p class="text-muted small">Validación de requisitos de inscripción para rendir el Examen Nacional de Preselección.</p>
                            <hr>
                            <% if (!publicadaRevision) { %>
                            <div class="alert alert-secondary small mb-0">
                                <i class="bi bi-hourglass-split me-1"></i>
                                Los resultados de la etapa de revisión aún no han sido publicados oficialmente por PRONABEC (RN-13).
                            </div>
                            <% } else { %>
                            <div class="mb-2">
                                <div class="small text-muted">Condición de Revisión:</div>
                                <% if ("INSCRITO".equals(p.getEstado()) || "EN REVISION".equals(p.getEstado())) { %>
                                <span class="badge bg-warning text-dark fs-6 mt-1">${postulante.estado}</span>
                                <% } else if ("NO APTO".equals(p.getEstado())) { %>
                                <span class="badge bg-danger fs-6 mt-1">NO APTO</span>
                                <% } else { %>
                                <span class="badge bg-success fs-6 mt-1">APTO PARA EL ENP</span>
                                <% } %>
                            </div>
                            <div class="small mt-3">
                                <strong>Observación del Evaluador:</strong><br>
                                <span class="text-secondary">
                                    ${not empty postulante.observacionRevision ? postulante.observacionRevision : 'Sin observaciones adicionales.'}
                                </span>
                            </div>
                            <% } %>
                        </div>
                    </div>
                </div>

                <!-- 2. Resultado del Examen Nacional de Preselección ENP (Etapa 5) -->
                <div class="col-md-4">
                    <div class="card border-0 shadow-sm rounded-4 h-100">
                        <div class="card-body p-4">
                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <span class="badge bg-light text-primary border">Etapa 5</span>
                                <span class="badge <%= publicadaPreseleccion ? "bg-success" : "bg-warning text-dark" %>">
                                    <%= publicadaPreseleccion ? "PUBLICADA" : "PENDIENTE DE PUBLICACIÓN" %>
                                </span>
                            </div>
                            <h5 class="fw-bold">2. Puntaje ENP y Preselección</h5>
                            <p class="text-muted small">
                                Puntaje mínimo exigido en modalidad <strong>${postulante.modalidadNombre}</strong>:
                                <strong>${postulante.modalidad.puntajeMinimoEnp} puntos</strong> (RN-09).
                            </p>
                            <hr>
                            <% if (!publicadaPreseleccion) { %>
                            <div class="alert alert-secondary small mb-0">
                                <i class="bi bi-hourglass-split me-1"></i>
                                La lista oficial de preseleccionados y puntajes del ENP aún no ha sido publicada (RN-13).
                            </div>
                            <% } else if (p.getPuntajeEnp() == null) { %>
                            <div class="alert alert-info small mb-0">
                                <i class="bi bi-info-circle me-1"></i>
                                Aún no se ha registrado su puntaje del Examen Nacional de Preselección.
                            </div>
                            <% } else { %>
                            <div class="mb-3">
                                <div class="small text-muted">Puntaje Obtenido (Formato es-PE):</div>
                                <div class="display-6 fw-bold text-primary">${postulante.puntajeEnpFormateado}</div>
                                <div class="small text-muted">de 120,00 pts posibles</div>
                            </div>
                            <div>
                                <div class="small text-muted">Resultado de Preselección:</div>
                                <% if ("NO PRESELECCIONADO".equals(p.getEstado())) { %>
                                <span class="badge bg-danger fs-6 mt-1">NO PRESELECCIONADO</span>
                                <% } else { %>
                                <span class="badge bg-primary fs-6 mt-1">PRESELECCIONADO</span>
                                <% } %>
                            </div>
                            <% } %>
                        </div>
                    </div>
                </div>

                <!-- 3. Resultado de Selección Final (Etapa 8) -->
                <div class="col-md-4">
                    <div class="card border-0 shadow-sm rounded-4 h-100">
                        <div class="card-body p-4">
                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <span class="badge bg-light text-primary border">Etapa 8</span>
                                <span class="badge <%= publicadaSeleccion ? "bg-success" : "bg-warning text-dark" %>">
                                    <%= publicadaSeleccion ? "PUBLICADA" : "PENDIENTE DE PUBLICACIÓN" %>
                                </span>
                            </div>
                            <h5 class="fw-bold">3. Selección Final de Beca 18</h5>
                            <p class="text-muted small">Asignación de vacantes por estricto orden de mérito y desempate por prelación temporal (RN-12).</p>
                            <hr>
                            <% if (!publicadaSeleccion) { %>
                            <div class="alert alert-secondary small mb-0">
                                <i class="bi bi-lock-fill me-1"></i>
                                <strong>Etapa sin publicar (RN-13 / CP-18):</strong> Los resultados finales de selección serán visibles únicamente cuando el administrador publique la Etapa 8 en el Cronograma.
                            </div>
                            <% } else if (postulacion == null) { %>
                            <div class="alert alert-warning small mb-0">
                                Usted no registró una postulación a institución/programa en la etapa de selección.
                            </div>
                            <% } else { %>
                            <div class="mb-2 small">
                                <strong>IES / Programa:</strong><br>
                                ${postulacionActual.institucionNombre} - ${postulacionActual.programaNombre}
                            </div>
                            <div class="mb-2 small">
                                <strong>Orden de Mérito en el Programa:</strong>
                                <span class="badge bg-dark">
                                    <%= postulacion.getOrdenMerito() != null ? ("#" + postulacion.getOrdenMerito()) : "En evaluación" %>
                                </span>
                            </div>
                            <div class="mt-3">
                                <div class="small text-muted">Condición Final:</div>
                                <% if ("SELECCIONADO".equals(p.getEstado()) || "SELECCIONADO".equals(postulacion.getResultado())) { %>
                                <div class="p-3 rounded-3 bg-success text-white mt-1 text-center">
                                    <i class="bi bi-trophy-fill fs-3 d-block mb-1"></i>
                                    <span class="fw-bold fs-5">¡SELECCIONADO BECARIO!</span>
                                </div>
                                <% } else if ("NO SELECCIONADO".equals(p.getEstado()) || "NO SELECCIONADO".equals(postulacion.getResultado())) { %>
                                <span class="badge bg-danger fs-6 mt-1">NO SELECCIONADO (Sin vacante disponible)</span>
                                <% } else { %>
                                <span class="badge bg-info text-dark fs-6 mt-1">PENDIENTE DE EJECUCIÓN DE RANKING</span>
                                <% } %>
                            </div>
                            <% } %>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <jsp:include page="/WEB-INF/views/shared/footer.jsp" />
    </body>
</html>
