<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es-PE">
    <head>
        <title>Iniciar Sesión | SIBECA-18 Convocatoria 2027</title>
        <jsp:include page="/WEB-INF/views/shared/head.jsp" />
    </head>
    <body>
        <div class="login-wrapper">
            <div class="container">
                <div class="row justify-content-center">
                    <div class="col-xl-10 col-lg-11">
                        <div class="card login-card">
                            <div class="row g-0">
                                <!-- Columna izquierda institucional -->
                                <div class="col-md-6 login-banner">
                                    <div>
                                        <div class="d-flex align-items-center gap-2 mb-4">
                                            <span class="badge bg-info text-dark px-3 py-2 fw-bold">PRONABEC · BECA 18</span>
                                            <span class="badge bg-light text-primary px-2 py-2">Convocatoria 2027</span>
                                        </div>
                                        <h2 class="fw-bold mb-3">SIBECA-18</h2>
                                        <p class="text-white-50 mb-4">
                                            Sistema Web de Seguimiento Integral del Concurso Beca 18: desde la inscripción
                                            al Examen Nacional de Preselección (ENP) hasta la publicación final de seleccionados.
                                        </p>
                                        <div class="bg-white bg-opacity-10 rounded-3 p-3 mb-3">
                                            <h6 class="fw-bold text-info mb-2"><i class="bi bi-check2-circle me-1"></i>Modalidades Convocadas (9)</h6>
                                            <p class="small mb-0 text-white-50">
                                                Ordinaria · Huallaga · Vraem · Protección · Repared · CNA · Pueblo Afroperuano · EIB · Fuerzas Armadas.
                                            </p>
                                        </div>
                                    </div>
                                    <div class="small text-white-50 border-top border-light border-opacity-25 pt-3">
                                        <div class="fw-semibold text-white mb-1"><i class="bi bi-key-fill text-warning me-1"></i>Credenciales de Prueba Académica:</div>
                                        <div>• <strong>Administrador:</strong> usuario <code>admin</code> | clave <code>admin123</code></div>
                                        <div>• <strong>Postulantes (1 al 12):</strong> DNI <code>70100001</code> al <code>70100012</code> | clave <code>beca2027</code></div>
                                    </div>
                                </div>

                                <!-- Columna derecha de formulario -->
                                <div class="col-md-6 p-4 p-lg-5">
                                    <div class="mb-4">
                                        <img src="${pageContext.request.contextPath}/assets/img/logo-sibeca18.svg" alt="SIBECA-18" height="46" class="mb-2">
                                        <h4 class="fw-bold text-dark mb-1">Acceso al Sistema</h4>
                                        <p class="text-muted small mb-0">Ingrese su usuario (o DNI) y contraseña para continuar.</p>
                                    </div>

                                    <% if (request.getAttribute("error") != null) { %>
                                    <div class="alert alert-danger d-flex align-items-center py-2" role="alert">
                                        <i class="bi bi-exclamation-triangle-fill me-2 fs-5"></i>
                                        <div>${error}</div>
                                    </div>
                                    <% } %>

                                    <% if (request.getAttribute("exito") != null) { %>
                                    <div class="alert alert-success d-flex align-items-center py-2" role="alert">
                                        <i class="bi bi-check-circle-fill me-2 fs-5"></i>
                                        <div>${exito}</div>
                                    </div>
                                    <% } %>

                                    <div id="alertaLoginCliente" class="alert alert-warning d-none py-2 small"></div>

                                    <form id="formLogin" action="${pageContext.request.contextPath}/auth" method="post" novalidate>
                                        <div class="mb-3">
                                            <label for="username" class="form-label fw-semibold">Usuario o DNI</label>
                                            <div class="input-group">
                                                <span class="input-group-text bg-light"><i class="bi bi-person-fill text-primary"></i></span>
                                                <input type="text" class="form-control" id="username" name="username"
                                                       value="${usernamePrevio}" placeholder="Ej. admin o 70100001" required autofocus>
                                            </div>
                                        </div>

                                        <div class="mb-4">
                                            <label for="password" class="form-label fw-semibold">Contraseña</label>
                                            <div class="input-group">
                                                <span class="input-group-text bg-light"><i class="bi bi-lock-fill text-primary"></i></span>
                                                <input type="password" class="form-control" id="password" name="password"
                                                       placeholder="Ingrese su contraseña" required>
                                            </div>
                                        </div>

                                        <div class="d-grid gap-2 mb-4">
                                            <button type="submit" class="btn btn-primary btn-lg fw-semibold">
                                                <i class="bi bi-box-arrow-in-right me-2"></i>Iniciar Sesión
                                            </button>
                                        </div>
                                    </form>

                                    <div class="text-center border-top pt-3">
                                        <p class="small text-muted mb-2">¿Aún no se ha inscrito en la Convocatoria 2027?</p>
                                        <a href="${pageContext.request.contextPath}/inscripcion" class="btn btn-outline-primary w-100 fw-semibold">
                                            <i class="bi bi-person-plus-fill me-1"></i>Inscribirme al Concurso Beca 18 (ENP)
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/js/validaciones.js"></script>
    </body>
</html>
