<%@page contentType="text/html" pageEncoding="UTF-8"%>
<div class="col-lg-2 col-md-3 admin-sidebar p-3">
    <div class="mb-3 pb-2 border-bottom border-secondary">
        <div class="text-uppercase small text-info fw-bold">Panel PRONABEC</div>
        <div class="small text-white-50">Gestión Convocatoria 2027</div>
    </div>
    <nav class="nav flex-column">
        <a class="nav-link ${menuActivo == 'dashboard' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/admin/dashboard">
            <i class="bi bi-speedometer2 me-2"></i>Dashboard General
        </a>
        <a class="nav-link ${menuActivo == 'postulantes' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/admin/postulantes">
            <i class="bi bi-people-fill me-2"></i>Postulantes y ENP
        </a>
        <a class="nav-link ${menuActivo == 'cronograma' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/admin/cronograma">
            <i class="bi bi-calendar-range me-2"></i>Cronograma y Publicación
        </a>
        <a class="nav-link ${menuActivo == 'programas' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/admin/programas">
            <i class="bi bi-bank2 me-2"></i>IES y Programas
        </a>
        <a class="nav-link ${menuActivo == 'seleccion' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/admin/seleccion">
            <i class="bi bi-trophy-fill me-2"></i>Ranking y Selección
        </a>
        <a class="nav-link ${menuActivo == 'bitacora' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/admin/seleccion?accion=bitacora">
            <i class="bi bi-journal-text me-2"></i>Bitácora de Eventos
        </a>
    </nav>
    <hr class="border-secondary my-3">
    <div class="small text-white-50 px-2">
        <div class="fw-semibold text-white mb-1"><i class="bi bi-shield-check text-info me-1"></i>Reglas Activas</div>
        <div>• Ordinaria: &lt; 22 años, ENP ≥ 84</div>
        <div>• Especiales: ENP ≥ 66</div>
        <div>• FF.AA.: &lt; 31 años</div>
    </div>
</div>
