<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="pe.edu.sibeca18.model.Usuario"%>
<!DOCTYPE html>
<html lang="es-PE">
    <head>
        <title>Acceso Denegado | SIBECA-18</title>
        <jsp:include page="/WEB-INF/views/shared/head.jsp" />
    </head>
    <body>
        <jsp:include page="/WEB-INF/views/shared/navbar.jsp" />

        <div class="container my-5">
            <div class="row justify-content-center">
                <div class="col-lg-7">
                    <div class="card border-0 shadow-sm rounded-4 p-4 text-center">
                        <div class="mb-3">
                            <span class="d-inline-flex align-items-center justify-content-center bg-danger bg-opacity-10 text-danger rounded-circle"
                                  style="width: 76px; height: 76px; font-size: 2.3rem;">
                                <i class="bi bi-shield-lock-fill"></i>
                            </span>
                        </div>
                        <h3 class="fw-bold text-danger mb-2">Acceso Denegado (HTTP 403)</h3>
                        <p class="text-secondary mb-4">
                            ${not empty mensajeDenegado ? mensajeDenegado : 'No cuenta con los permisos necesarios para visualizar este módulo protegido (RN-14 / RF-02).'}
                        </p>
                        <div class="d-flex justify-content-center gap-3">
                            <%
                                Usuario u = (Usuario) session.getAttribute("usuarioAutenticado");
                                if (u != null && u.esPostulante()) {
                            %>
                            <a href="${pageContext.request.contextPath}/postulante/panel?vista=estado" class="btn btn-primary px-4">
                                <i class="bi bi-arrow-left me-1"></i>Volver a Mi Panel de Postulante
                            </a>
                            <% } else if (u != null && u.esAdministrador()) { %>
                            <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-primary px-4">
                                <i class="bi bi-arrow-left me-1"></i>Volver al Panel Administrativo
                            </a>
                            <% } else { %>
                            <a href="${pageContext.request.contextPath}/auth?accion=login" class="btn btn-primary px-4">
                                <i class="bi bi-box-arrow-in-right me-1"></i>Ir al Inicio de Sesión
                            </a>
                            <% } %>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <jsp:include page="/WEB-INF/views/shared/footer.jsp" />
    </body>
</html>
