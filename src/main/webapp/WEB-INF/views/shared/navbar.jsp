<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="pe.edu.sibeca18.model.Usuario"%>
<%
    Usuario navUser = (Usuario) session.getAttribute("usuarioAutenticado");
%>
<nav class="navbar navbar-expand-lg navbar-dark navbar-sibeca py-2">
    <div class="container-fluid px-3 px-lg-4">
        <a class="navbar-brand d-flex align-items-center gap-2 fw-bold" href="${pageContext.request.contextPath}/">
            <i class="bi bi-mortarboard-fill fs-4 text-info"></i>
            <span>SIBECA-18</span>
            <span class="badge bg-info text-dark ms-1 fw-semibold" style="font-size: 0.72rem;">CONVOCATORIA 2027</span>
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSibeca">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarSibeca">
            <% if (navUser != null && navUser.esPostulante()) { %>
            <ul class="navbar-nav me-auto mb-2 mb-lg-0 ms-lg-3">
                <li class="nav-item">
                    <a class="nav-link ${vistaActiva == 'estado' ? 'active fw-bold text-white' : ''}"
                       href="${pageContext.request.contextPath}/postulante/panel?vista=estado">
                        <i class="bi bi-person-badge me-1"></i>Mi Estado y Cronograma
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${vistaActiva == 'editar' ? 'active fw-bold text-white' : ''}"
                       href="${pageContext.request.contextPath}/postulante/panel?vista=editar">
                        <i class="bi bi-pencil-square me-1"></i>Editar Inscripción
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${vistaActiva == 'resultados' ? 'active fw-bold text-white' : ''}"
                       href="${pageContext.request.contextPath}/postulante/panel?vista=resultados">
                        <i class="bi bi-award me-1"></i>Mis Resultados
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${vistaActiva == 'postulacion' ? 'active fw-bold text-white' : ''}"
                       href="${pageContext.request.contextPath}/postulante/panel?vista=postulacion">
                        <i class="bi bi-building-check me-1"></i>Postulación a Selección
                    </a>
                </li>
            </ul>
            <% } else { %>
            <div class="me-auto"></div>
            <% } %>

            <div class="d-flex align-items-center gap-3">
                <% if (navUser != null) { %>
                <div class="text-white text-end d-none d-sm-block">
                    <div class="small fw-semibold">
                        <i class="bi bi-person-circle me-1"></i>${sessionScope.usuarioAutenticado.username}
                    </div>
                    <span class="badge bg-light text-primary" style="font-size: 0.68rem;">
                        ROL: ${sessionScope.usuarioAutenticado.rol}
                    </span>
                </div>
                <a href="${pageContext.request.contextPath}/auth?accion=logout"
                   class="btn btn-sm btn-outline-light fw-semibold">
                    <i class="bi bi-box-arrow-right me-1"></i>Cerrar Sesión
                </a>
                <% } else { %>
                <a href="${pageContext.request.contextPath}/auth?accion=login"
                   class="btn btn-sm btn-light text-primary fw-semibold">
                    <i class="bi bi-box-arrow-in-right me-1"></i>Iniciar Sesión
                </a>
                <% } %>
            </div>
        </div>
    </div>
</nav>
