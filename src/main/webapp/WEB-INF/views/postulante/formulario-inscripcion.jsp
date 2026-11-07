<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="pe.edu.sibeca18.model.Modalidad"%>
<%@page import="pe.edu.sibeca18.model.Postulante"%>
<%
    Boolean modoEdicionObj = (Boolean) request.getAttribute("modoEdicion");
    boolean modoEdicion = (modoEdicionObj != null && modoEdicionObj);
    Postulante pForm = (Postulante) request.getAttribute("postulanteForm");
    if (pForm == null) {
        pForm = new Postulante();
    }
    List<Modalidad> modalidades = (List<Modalidad>) request.getAttribute("modalidades");
    String[] regionesPeru = {
        "Amazonas", "Áncash", "Apurímac", "Arequipa", "Ayacucho", "Cajamarca", "Callao",
        "Cusco", "Huancavelica", "Huánuco", "Ica", "Junín", "La Libertad", "Lambayeque",
        "Lima", "Loreto", "Madre de Dios", "Moquegua", "Pasco", "Piura", "Puno",
        "San Martín", "Tacna", "Tumbes", "Ucayali"
    };
%>
<!DOCTYPE html>
<html lang="es-PE">
    <head>
        <title><%= modoEdicion ? "Editar Mi Inscripción" : "Ficha Pública de Inscripción" %> | SIBECA-18</title>
        <jsp:include page="/WEB-INF/views/shared/head.jsp" />
    </head>
    <body>
        <jsp:include page="/WEB-INF/views/shared/navbar.jsp" />

        <div class="container my-4">
            <div class="row justify-content-center">
                <div class="col-xl-10">
                    <!-- Encabezado contextual reutilizado para Registro (RF-03) y Edición (RF-05) -->
                    <div class="d-flex flex-wrap justify-content-between align-items-center mb-3">
                        <div>
                            <span class="badge bg-primary mb-1">
                                <%= modoEdicion ? "RF-05 · MODO EDICIÓN DE INSCRIPCIÓN" : "RF-03 · REGISTRO PÚBLICO EN UNA SOLA TRANSACCIÓN" %>
                            </span>
                            <h3 class="fw-bold mb-0 text-dark">
                                <%= modoEdicion ? "Modificar Ficha de Inscripción al ENP" : "Formulario Oficial de Inscripción - Beca 18 (Convocatoria 2027)" %>
                            </h3>
                            <p class="text-muted small mb-0">
                                Fecha de corte parametrizable para el cálculo de edad (RN-02): <strong>${fechaCorteFormateada}</strong>
                            </p>
                        </div>
                        <div>
                            <% if (modoEdicion) { %>
                            <a href="${pageContext.request.contextPath}/postulante/panel?vista=estado" class="btn btn-outline-secondary btn-sm">
                                <i class="bi bi-arrow-left me-1"></i>Volver a Mi Estado
                            </a>
                            <% } else { %>
                            <a href="${pageContext.request.contextPath}/auth?accion=login" class="btn btn-outline-primary btn-sm">
                                <i class="bi bi-box-arrow-in-right me-1"></i>Ya estoy inscrito (Iniciar Sesión)
                            </a>
                            <% } %>
                        </div>
                    </div>

                    <% if (request.getAttribute("error") != null) { %>
                    <div class="alert alert-danger d-flex align-items-start shadow-sm" role="alert">
                        <i class="bi bi-x-octagon-fill fs-4 me-3 text-danger"></i>
                        <div>
                            <div class="fw-bold">No se pudo guardar la inscripción:</div>
                            <div>${error}</div>
                        </div>
                    </div>
                    <% } %>

                    <div id="alertaErroresCliente" class="alert alert-warning d-none shadow-sm" role="alert">
                        <div class="fw-bold mb-1"><i class="bi bi-exclamation-triangle-fill me-1"></i>Corrija los siguientes campos antes de continuar (Validación Cliente jQuery):</div>
                        <div id="alertaErroresClienteTexto"></div>
                    </div>

                    <div class="card border-0 shadow-sm rounded-4">
                        <div class="card-body p-4">
                            <input type="hidden" id="fechaCorteHidden" value="${fechaCorte}">

                            <form id="formInscripcion"
                                  action="${pageContext.request.contextPath}<%= modoEdicion ? "/postulante/panel" : "/inscripcion" %>"
                                  method="post" novalidate>

                                <% if (modoEdicion) { %>
                                <input type="hidden" name="accion" value="actualizarInscripcion">
                                <input type="hidden" name="idPostulante" value="${postulanteForm.idPostulante}">
                                <% } %>

                                <!-- SECCIÓN 1: Datos Personales e Identificación -->
                                <div class="form-section-title">
                                    <i class="bi bi-person-vcard-fill text-primary"></i>
                                    <span>1. Datos Personales y Nacionalidad (RN-01, RN-02, RN-06)</span>
                                </div>

                                <div class="row g-3 mb-4">
                                    <div class="col-md-4">
                                        <label for="dni" class="form-label fw-semibold">DNI (8 dígitos) <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="dni" name="dni" maxlength="8"
                                               value="${postulanteForm.dni}" placeholder="Ej. 70200300"
                                               <%= modoEdicion ? "readonly class='form-control bg-light'" : "required" %>>
                                        <div class="form-text">El DNI será su usuario de acceso al sistema.</div>
                                    </div>

                                    <div class="col-md-4">
                                        <label for="nombres" class="form-label fw-semibold">Nombres Completos <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="nombres" name="nombres"
                                               value="${postulanteForm.nombres}" placeholder="Ej. Juan Carlos" required>
                                    </div>

                                    <div class="col-md-4">
                                        <label for="apellidos" class="form-label fw-semibold">Apellidos Completos <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="apellidos" name="apellidos"
                                               value="${postulanteForm.apellidos}" placeholder="Ej. Quispe Flores" required>
                                    </div>

                                    <div class="col-md-4">
                                        <label for="fechaNacimiento" class="form-label fw-semibold">Fecha de Nacimiento <span class="text-danger">*</span></label>
                                        <input type="date" class="form-control" id="fechaNacimiento" name="fechaNacimiento"
                                               value="${postulanteForm.fechaNacimientoIso}" required>
                                        <div class="form-text text-primary fw-semibold" id="edadCalculadaPreview"></div>
                                    </div>

                                    <div class="col-md-4">
                                        <label for="nacionalidad" class="form-label fw-semibold">Nacionalidad (RN-01) <span class="text-danger">*</span></label>
                                        <select class="form-select" id="nacionalidad" name="nacionalidad" required>
                                            <option value="PERUANA" <%= "PERUANA".equalsIgnoreCase(pForm.getNacionalidad()) ? "selected" : "" %>>PERUANA</option>
                                            <option value="EXTRANJERA" <%= "EXTRANJERA".equalsIgnoreCase(pForm.getNacionalidad()) ? "selected" : "" %>>EXTRANJERA (Prueba RN-01)</option>
                                        </select>
                                    </div>

                                    <div class="col-md-4 d-flex align-items-end">
                                        <div class="form-check form-switch p-3 bg-light rounded-3 border w-100">
                                            <input class="form-check-input ms-0 me-2" type="checkbox" id="discapacidadAcreditada"
                                                   name="discapacidadAcreditada" value="true"
                                                   <%= pForm.isDiscapacidadAcreditada() ? "checked" : "" %>>
                                            <label class="form-check-label fw-semibold small" for="discapacidadAcreditada">
                                                Acredita Discapacidad CONADIS (Exonerado de límite de edad - RN-02)
                                            </label>
                                        </div>
                                    </div>
                                </div>

                                <!-- SECCIÓN 2: Contacto y Ubicación -->
                                <div class="form-section-title">
                                    <i class="bi bi-geo-alt-fill text-primary"></i>
                                    <span>2. Datos de Contacto y procedencia (RF-04)</span>
                                </div>

                                <div class="row g-3 mb-4">
                                    <div class="col-md-5">
                                        <label for="correo" class="form-label fw-semibold">Correo Electrónico <span class="text-danger">*</span></label>
                                        <input type="email" class="form-control" id="correo" name="correo"
                                               value="${postulanteForm.correo}" placeholder="postulante@correo.pe" required>
                                    </div>

                                    <div class="col-md-3">
                                        <label for="celular" class="form-label fw-semibold">Celular (9 dígitos) <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="celular" name="celular" maxlength="9"
                                               value="${postulanteForm.celular}" placeholder="987654321" required>
                                    </div>

                                    <div class="col-md-4">
                                        <label for="region" class="form-label fw-semibold">Región de Residencia <span class="text-danger">*</span></label>
                                        <select class="form-select" id="region" name="region" required>
                                            <option value="">-- Seleccione región --</option>
                                            <% for (String reg : regionesPeru) {
                                                boolean sel = reg.equalsIgnoreCase(pForm.getRegion());
                                            %>
                                            <option value="<%= reg %>" <%= sel ? "selected" : "" %>><%= reg %></option>
                                            <% } %>
                                        </select>
                                    </div>
                                </div>

                                <!-- SECCIÓN 3: Modalidad, Colegio, Rendimiento y Clasificación Socioeconómica -->
                                <div class="form-section-title">
                                    <i class="bi bi-journal-bookmark-fill text-primary"></i>
                                    <span>3. Modalidad de Postulación, Situación Académica y Socioeconómica (RN-02 a RN-05)</span>
                                </div>

                                <div class="row g-3 mb-4">
                                    <div class="col-md-6">
                                        <label for="idModalidad" class="form-label fw-semibold">Modalidad de Beca 18 <span class="text-danger">*</span></label>
                                        <select class="form-select" id="idModalidad" name="idModalidad" required>
                                            <option value="0">-- Seleccione una modalidad --</option>
                                            <% if (modalidades != null) {
                                                for (Modalidad m : modalidades) {
                                                    boolean sel = (pForm.getIdModalidad() != null && pForm.getIdModalidad().equals(m.getIdModalidad()));
                                            %>
                                            <option value="<%= m.getIdModalidad() %>"
                                                    data-tipo="<%= m.getTipo() %>"
                                                    data-edadmax="<%= m.getEdadMaxima() != null ? m.getEdadMaxima() : "null" %>"
                                                    data-puntajemin="<%= m.getPuntajeMinimoEnp() %>"
                                                    <%= sel ? "selected" : "" %>>
                                                <%= m.getNombre() %> (<%= m.getTipo() %> - Min. ENP: <%= m.getPuntajeMinimoEnp() %> pts)
                                            </option>
                                            <%  }
                                               } %>
                                        </select>
                                    </div>

                                    <div class="col-md-6">
                                        <label for="colegio" class="form-label fw-semibold">Institución Educativa (Colegio de Secundaria) <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="colegio" name="colegio"
                                               value="${postulanteForm.colegio}" placeholder="Ej. I.E. Mariscal Cáceres" required>
                                    </div>

                                    <div class="col-12">
                                        <div class="alert alert-info py-2 px-3 mb-0 small d-flex align-items-center">
                                            <i class="bi bi-info-circle-fill me-2 fs-5"></i>
                                            <span id="infoReglasModalidad">Seleccione una modalidad para ver sus requisitos específicos de edad y puntaje.</span>
                                        </div>
                                    </div>

                                    <div class="col-md-4">
                                        <label for="situacionEstudios" class="form-label fw-semibold">Situación Académica (RN-03) <span class="text-danger">*</span></label>
                                        <select class="form-select" id="situacionEstudios" name="situacionEstudios" required>
                                            <option value="EGRESADO" <%= "EGRESADO".equals(pForm.getSituacionEstudios()) ? "selected" : "" %>>Egresado de Secundaria</option>
                                            <option value="CURSANDO_5TO" <%= "CURSANDO_5TO".equals(pForm.getSituacionEstudios()) ? "selected" : "" %>>Cursando 5.° de Secundaria</option>
                                        </select>
                                    </div>

                                    <div class="col-md-4">
                                        <label for="anioEgreso" class="form-label fw-semibold">Año de Egreso o Cursado (RN-03) <span class="text-danger">*</span></label>
                                        <input type="number" class="form-control" id="anioEgreso" name="anioEgreso"
                                               min="1980" max="2026" value="<%= pForm.getAnioEgreso() > 0 ? pForm.getAnioEgreso() : 2025 %>" required>
                                        <div class="form-text">En modalidad Ordinaria debe ser entre 2023 y 2026.</div>
                                    </div>

                                    <div class="col-md-4">
                                        <label for="clasificacionSocioeconomica" class="form-label fw-semibold">Clasificación SISFOH (RN-05) <span class="text-danger">*</span></label>
                                        <select class="form-select" id="clasificacionSocioeconomica" name="clasificacionSocioeconomica" required>
                                            <option value="POBREZA_EXTREMA" <%= "POBREZA_EXTREMA".equals(pForm.getClasificacionSocioeconomica()) ? "selected" : "" %>>Pobreza Extrema</option>
                                            <option value="POBREZA" <%= "POBREZA".equals(pForm.getClasificacionSocioeconomica()) ? "selected" : "" %>>Pobreza</option>
                                            <option value="VULNERABLE" <%= "VULNERABLE".equals(pForm.getClasificacionSocioeconomica()) ? "selected" : "" %>>Vulnerable / Modalidad Especial</option>
                                            <option value="NO_POBRE" <%= "NO_POBRE".equals(pForm.getClasificacionSocioeconomica()) ? "selected" : "" %>>No Pobre (Prueba RN-05)</option>
                                        </select>
                                    </div>

                                    <div class="col-12">
                                        <div class="form-check p-3 bg-light rounded-3 border">
                                            <input class="form-check-input ms-0 me-2" type="checkbox" id="tercioSuperior"
                                                   name="tercioSuperior" value="true"
                                                   <%= pForm.isTercioSuperior() ? "checked" : "" %>>
                                            <label class="form-check-label fw-semibold" for="tercioSuperior">
                                                Acredito alto rendimiento académico perteneciente al <strong>Tercio Superior</strong> en los dos últimos grados de secundaria (Obligatorio en Modalidad Ordinaria - RN-04)
                                            </label>
                                        </div>
                                    </div>
                                </div>

                                <% if (!modoEdicion) { %>
                                <!-- SECCIÓN 4: Credenciales de Acceso (Solo en registro inicial RF-03) -->
                                <div class="form-section-title">
                                    <i class="bi bi-shield-lock-fill text-primary"></i>
                                    <span>4. Creación de Cuenta de Usuario (Transacción única Usuario + Postulante RF-03)</span>
                                </div>

                                <div class="row g-3 mb-4">
                                    <div class="col-md-6">
                                        <label for="password" class="form-label fw-semibold">Contraseña de Acceso (Mín. 6 caracteres) <span class="text-danger">*</span></label>
                                        <input type="password" class="form-control" id="password" name="password"
                                               placeholder="Ingrese una contraseña segura" required>
                                        <div class="form-text">Su contraseña se almacena cifrada con hash seguro SHA-256.</div>
                                    </div>

                                    <div class="col-md-6">
                                        <div class="p-3 rounded-3 border border-warning bg-warning bg-opacity-10">
                                            <div class="fw-bold small text-dark mb-1">
                                                <i class="bi bi-sliders me-1"></i>Opciones de Verificación Académica (Casos de Prueba CP-08 y CP-09):
                                            </div>
                                            <div class="form-check small">
                                                <input class="form-check-input" type="checkbox" id="simularFalloTransaccion" name="simularFalloTransaccion" value="true">
                                                <label class="form-check-label" for="simularFalloTransaccion">
                                                    <strong>CP-09:</strong> Forzar fallo en la 2.ª inserción (postulante) para demostrar Rollback transaccional.
                                                </label>
                                            </div>
                                            <div class="form-check small mt-1">
                                                <input class="form-check-input" type="checkbox" id="omitirValidacionCliente">
                                                <label class="form-check-label" for="omitirValidacionCliente">
                                                    Omitir validación jQuery en navegador para probar directamente el rechazo en el Servlet (CP-08).
                                                </label>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <% } %>

                                <div class="d-flex justify-content-end gap-2 border-top pt-3">
                                    <% if (modoEdicion) { %>
                                    <a href="${pageContext.request.contextPath}/postulante/panel?vista=estado" class="btn btn-light border px-4">
                                        Cancelar
                                    </a>
                                    <button type="submit" class="btn btn-primary px-4 fw-semibold">
                                        <i class="bi bi-save me-1"></i>Guardar Cambios de Inscripción
                                    </button>
                                    <% } else { %>
                                    <a href="${pageContext.request.contextPath}/auth?accion=login" class="btn btn-light border px-4">
                                        Cancelar
                                    </a>
                                    <button type="submit" class="btn btn-primary px-4 fw-semibold">
                                        <i class="bi bi-check2-circle me-1"></i>Completar Inscripción a Beca 18
                                    </button>
                                    <% } %>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <jsp:include page="/WEB-INF/views/shared/footer.jsp" />
    </body>
</html>
