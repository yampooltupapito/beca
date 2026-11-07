<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%
    List<String> lineasBitacora = (List<String>) request.getAttribute("lineasBitacora");
%>
<!DOCTYPE html>
<html lang="es-PE">
    <head>
        <title>Bitácora de Eventos del Sistema | SIBECA-18</title>
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
                            <span class="badge bg-primary mb-1">RF-16 · TEMA 3 · FLUJOS DE ENTRADA / SALIDA EN ARCHIVO DE TEXTO</span>
                            <h3 class="fw-bold mb-0">Bitácora de Auditoría del Sistema (TXT)</h3>
                            <p class="text-muted small mb-0">
                                Archivo físico de registro: <code>${rutaArchivoBitacora}</code>
                            </p>
                        </div>
                        <div>
                            <a href="${pageContext.request.contextPath}/admin/seleccion?accion=exportarBitacoraTxt"
                               class="btn btn-primary btn-sm fw-semibold">
                                <i class="bi bi-download me-1"></i>Descargar Archivo bitacora_sibeca18.txt
                            </a>
                        </div>
                    </div>

                    <div class="card border-0 shadow-sm rounded-4">
                        <div class="card-body p-4">
                            <div class="bg-dark text-light rounded-3 p-3 font-monospace small" style="min-height: 420px; max-height: 600px; overflow-y: auto;">
                                <% if (lineasBitacora == null || lineasBitacora.isEmpty()) { %>
                                <div class="text-white-50">No se han registrado líneas en el archivo de bitácora aún.</div>
                                <% } else {
                                    for (String linea : lineasBitacora) { %>
                                <div class="py-1 border-bottom border-secondary"><%= linea %></div>
                                <%  }
                                   } %>
                            </div>
                        </div>
                    </div>
                </main>
            </div>
        </div>

        <jsp:include page="/WEB-INF/views/shared/footer.jsp" />
    </body>
</html>
