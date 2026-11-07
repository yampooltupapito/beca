<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="pe.edu.sibeca18.model.PostulacionSeleccion"%>
<%@page import="pe.edu.sibeca18.model.Postulante"%>
<%
    Postulante p = (Postulante) request.getAttribute("postulante");
    PostulacionSeleccion postulacion = (PostulacionSeleccion) request.getAttribute("postulacion");
%>
<!DOCTYPE html>
<html lang="es-PE">
    <head>
        <title>Evaluación de Postulante DNI ${postulante.dni} | SIBECA-18</title>
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
                            <span class="badge bg-primary mb-1">RF-09 · RF-10 · RF-11 · EXPEDIENTE INDIVIDUAL (buscarPorId)</span>
                            <h3 class="fw-bold mb-0">${postulante.nombreCompleto} (DNI: ${postulante.dni})</h3>
                        </div>
                        <a href="${pageContext.request.contextPath}/admin/postulantes" class="btn btn-outline-secondary btn-sm">
                            <i class="bi bi-arrow-left me-1"></i>Volver al Listado de Postulantes
                        </a>
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
                        <!-- Columna izquierda: Datos completos del postulante -->
                        <div class="col-lg-6">
                            <div class="card border-0 shadow-sm rounded-4 h-100">
                                <div class="card-header bg-white border-0 pt-4 px-4 pb-0 d-flex justify-content-between align-items-center">
                                    <h5 class="fw-bold mb-0"><i class="bi bi-person-lines-fill text-primary me-2"></i>Ficha Integral del Postulante</h5>
                                    <span class="badge bg-primary fs-6">${postulante.estado}</span>
                                </div>
                                <div class="card-body p-4">
                                    <dl class="row mb-0 small">
                                        <dt class="col-sm-5 text-muted py-1">ID Postulante / Usuario:</dt>
                                        <dd class="col-sm-7 py-1">#${postulante.idPostulante} (Usuario: <code>${postulante.username}</code>)</dd>

                                        <dt class="col-sm-5 text-muted py-1">DNI:</dt>
                                        <dd class="col-sm-7 fw-bold py-1">${postulante.dni}</dd>

                                        <dt class="col-sm-5 text-muted py-1">Nombres y Apellidos:</dt>
                                        <dd class="col-sm-7 py-1">${postulante.nombres} ${postulante.apellidos}</dd>

                                        <dt class="col-sm-5 text-muted py-1">Fecha de Nacimiento:</dt>
                                        <dd class="col-sm-7 py-1">${postulante.fechaNacimientoFormateada} (<strong>${postulante.edadCorte} años</strong> a la fecha de corte)</dd>

                                        <dt class="col-sm-5 text-muted py-1">Nacionalidad (RN-01):</dt>
                                        <dd class="col-sm-7 py-1">${postulante.nacionalidad}</dd>

                                        <dt class="col-sm-5 text-muted py-1">Correo / Celular:</dt>
                                        <dd class="col-sm-7 py-1">${postulante.correo} · ${postulante.celular}</dd>

                                        <dt class="col-sm-5 text-muted py-1">Región / Colegio:</dt>
                                        <dd class="col-sm-7 py-1">${postulante.region} — ${postulante.colegio}</dd>

                                        <dt class="col-sm-5 text-muted py-1">Situación Académica:</dt>
                                        <dd class="col-sm-7 py-1">${postulante.situacionLegible}</dd>

                                        <dt class="col-sm-5 text-muted py-1">Tercio Superior (RN-04):</dt>
                                        <dd class="col-sm-7 py-1">${postulante.tercioSuperior ? 'SÍ ACREDITA' : 'NO ACREDITA'}</dd>

                                        <dt class="col-sm-5 text-muted py-1">Clasificación SISFOH:</dt>
                                        <dd class="col-sm-7 py-1">${postulante.clasificacionLegible}</dd>

                                        <dt class="col-sm-5 text-muted py-1">Discapacidad CONADIS:</dt>
                                        <dd class="col-sm-7 py-1">${postulante.discapacidadAcreditada ? 'SÍ ACREDITA' : 'NO'}</dd>

                                        <dt class="col-sm-5 text-muted py-1">Modalidad Postulada:</dt>
                                        <dd class="col-sm-7 fw-bold text-primary py-1">
                                            ${postulante.modalidadNombre} (${postulante.modalidad.tipo} · Mín. ENP: ${postulante.modalidad.puntajeMinimoEnp} pts)
                                        </dd>

                                        <dt class="col-sm-5 text-muted py-1">Observación de Revisión:</dt>
                                        <dd class="col-sm-7 py-1">${not empty postulante.observacionRevision ? postulante.observacionRevision : 'Sin observación registrada'}</dd>

                                        <dt class="col-sm-5 text-muted py-1">Puntaje ENP (es-PE):</dt>
                                        <dd class="col-sm-7 fw-bold text-primary py-1">${postulante.puntajeEnpFormateado}</dd>
                                    </dl>

                                    <% if (postulacion != null) { %>
                                    <hr>
                                    <h6 class="fw-bold text-primary"><i class="bi bi-building-check me-1"></i>Postulación a Selección Registrada</h6>
                                    <div class="small">
                                        <div><strong>IES / Programa:</strong> ${postulacion.institucionNombre} - ${postulacion.programaNombre}</div>
                                        <div><strong>Fecha y Hora (RN-12):</strong> ${postulacion.fechaHoraFormateada}</div>
                                        <div><strong>Resultado Ranking:</strong> <span class="badge bg-dark">${postulacion.resultado}</span></div>
                                    </div>
                                    <% } %>
                                </div>
                            </div>
                        </div>

                        <!-- Columna derecha: Flujo Secuencial de Revisión y Registro de Puntaje ENP (RN-08, RN-09) -->
                        <div class="col-lg-6">
                            <!-- ETAPA A: Revisión de la Inscripción (RF-10) -->
                            <div class="card border-0 shadow-sm rounded-4 mb-4">
                                <div class="card-body p-4">
                                    <h5 class="fw-bold mb-2">
                                        <i class="bi bi-clipboard-check-fill text-primary me-2"></i>1. Revisión de la Inscripción (RF-10 · RN-08)
                                    </h5>
                                    <p class="text-muted small mb-3">
                                        Transición secuencial obligatoria: <code>INSCRITO</code> &rarr; <code>EN REVISION</code> &rarr; <code>APTO</code> / <code>NO APTO</code> (con observación obligatoria si es NO APTO - CP-11).
                                    </p>

                                    <!-- Botón Paso 1: Iniciar Revisión (INSCRITO -> EN REVISION) -->
                                    <div class="p-3 bg-light rounded-3 border mb-3 d-flex justify-content-between align-items-center">
                                        <div class="small">
                                            <strong>Paso 1.1:</strong> Pasar expediente a estado <code>EN REVISION</code>
                                        </div>
                                        <form action="${pageContext.request.contextPath}/admin/postulantes" method="post">
                                            <input type="hidden" name="accion" value="iniciarRevision">
                                            <input type="hidden" name="idPostulante" value="${postulante.idPostulante}">
                                            <button type="submit" class="btn btn-sm btn-warning fw-semibold"
                                                    <%= (p != null && "INSCRITO".equals(p.getEstado())) ? "" : "" %>>
                                                <i class="bi bi-play-circle me-1"></i>Iniciar Revisión
                                            </button>
                                        </form>
                                    </div>

                                    <!-- Formulario Paso 2: Dictaminar APTO o NO APTO -->
                                    <form id="formDictaminarRevision" action="${pageContext.request.contextPath}/admin/postulantes" method="post">
                                        <input type="hidden" name="accion" value="dictaminarRevision">
                                        <input type="hidden" name="idPostulante" value="${postulante.idPostulante}">

                                        <div id="errorRevisionCliente" class="alert alert-warning d-none py-2 small"></div>

                                        <div class="mb-3">
                                            <label class="form-label fw-semibold small">Paso 1.2: Resultado de la Revisión Documental</label>
                                            <div class="d-flex gap-4">
                                                <div class="form-check">
                                                    <input class="form-check-input" type="radio" name="resultadoRevision" id="resApto" value="APTO" checked>
                                                    <label class="form-check-label fw-semibold text-success" for="resApto">
                                                        Declarar APTO
                                                    </label>
                                                </div>
                                                <div class="form-check">
                                                    <input class="form-check-input" type="radio" name="resultadoRevision" id="resNoApto" value="NO APTO">
                                                    <label class="form-check-label fw-semibold text-danger" for="resNoApto">
                                                        Declarar NO APTO (Exige observación - CP-11)
                                                    </label>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="mb-3">
                                            <label for="observacionRevision" class="form-label fw-semibold small">
                                                Observación de Revisión (Obligatoria si es NO APTO)
                                            </label>
                                            <textarea class="form-control form-control-sm" id="observacionRevision" name="observacionRevision"
                                                      rows="2" placeholder="Detalle el sustento de la revisión o el motivo de rechazo...">${postulante.observacionRevision}</textarea>
                                        </div>

                                        <div class="d-flex justify-content-between align-items-center">
                                            <div class="form-check small">
                                                <input class="form-check-input" type="checkbox" id="omitirValidacionRevisionCliente">
                                                <label class="form-check-label text-muted" for="omitirValidacionRevisionCliente">
                                                    Omitir validación jQuery para probar CP-11 en el Servlet
                                                </label>
                                            </div>
                                            <button type="submit" class="btn btn-primary btn-sm px-3 fw-semibold">
                                                <i class="bi bi-check2-square me-1"></i>Guardar Dictamen de Revisión
                                            </button>
                                        </div>
                                    </form>
                                </div>
                            </div>

                            <!-- ETAPA B: Registro del Puntaje ENP y Preselección Automática (RF-11, RN-09, CP-12, CP-13) -->
                            <div class="card border-0 shadow-sm rounded-4">
                                <div class="card-body p-4">
                                    <h5 class="fw-bold mb-2">
                                        <i class="bi bi-calculator-fill text-primary me-2"></i>2. Registro de Puntaje ENP y Preselección Automática (RF-11)
                                    </h5>
                                    <p class="text-muted small mb-3">
                                        Solo permitido cuando el estado es <code>APTO</code> (RN-08 / CP-13). Rango: 0 a 120 pts.
                                        En modalidad <strong>${postulante.modalidadNombre}</strong> se exige mínimo
                                        <strong>${postulante.modalidad.puntajeMinimoEnp} puntos</strong> para ser <code>PRESELECCIONADO</code> (RN-09 / CP-12).
                                    </p>

                                    <form id="formPuntajeEnp" action="${pageContext.request.contextPath}/admin/postulantes" method="post">
                                        <input type="hidden" name="accion" value="registrarPuntajeEnp">
                                        <input type="hidden" name="idPostulante" value="${postulante.idPostulante}">

                                        <div id="errorEnpCliente" class="alert alert-warning d-none py-2 small"></div>

                                        <div class="row g-2 align-items-end mb-3">
                                            <div class="col-sm-7">
                                                <label for="puntajeEnp" class="form-label fw-semibold small">
                                                    Puntaje Obtenido en el ENP (0 a 120 puntos)
                                                </label>
                                                <input type="number" step="0.01" min="0" max="120" class="form-control"
                                                       id="puntajeEnp" name="puntajeEnp"
                                                       value="${postulante.puntajeEnp}" placeholder="Ej. 84 o 66" required>
                                            </div>
                                            <div class="col-sm-5">
                                                <button type="submit" class="btn btn-success w-100 fw-semibold">
                                                    <i class="bi bi-award-fill me-1"></i>Registrar ENP y Evaluar
                                                </button>
                                            </div>
                                        </div>

                                        <div class="form-check small">
                                            <input class="form-check-input" type="checkbox" id="omitirValidacionEnpCliente">
                                            <label class="form-check-label text-muted" for="omitirValidacionEnpCliente">
                                                Omitir validación en cliente para probar validación de rango/estado en servidor (CP-13)
                                            </label>
                                        </div>
                                    </form>
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
