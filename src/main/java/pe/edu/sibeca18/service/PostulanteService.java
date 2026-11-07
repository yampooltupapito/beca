package pe.edu.sibeca18.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import pe.edu.sibeca18.dao.EtapaDAO;
import pe.edu.sibeca18.dao.ModalidadDAO;
import pe.edu.sibeca18.dao.PostulanteDAO;
import pe.edu.sibeca18.dao.UsuarioDAO;
import pe.edu.sibeca18.dao.impl.EtapaDAOImpl;
import pe.edu.sibeca18.dao.impl.ModalidadDAOImpl;
import pe.edu.sibeca18.dao.impl.PostulanteDAOImpl;
import pe.edu.sibeca18.dao.impl.UsuarioDAOImpl;
import pe.edu.sibeca18.exception.AccesoDatosException;
import pe.edu.sibeca18.exception.ReglaNegocioException;
import pe.edu.sibeca18.model.Etapa;
import pe.edu.sibeca18.model.Modalidad;
import pe.edu.sibeca18.model.Postulante;
import pe.edu.sibeca18.model.Usuario;
import pe.edu.sibeca18.util.BitacoraUtil;
import pe.edu.sibeca18.util.ConexionDB;
import pe.edu.sibeca18.util.FormatoUtil;
import pe.edu.sibeca18.util.PasswordUtil;
import pe.edu.sibeca18.util.ValidadorUtil;

/**
 * Servicio de negocio para la gestión integral del postulante, validaciones de inscripción,
 * revisión documental y preselección por puntaje del ENP (RF-03..RF-11, RN-01..RN-09).
 */
public class PostulanteService {

    private final PostulanteDAO postulanteDAO;
    private final UsuarioDAO usuarioDAO;
    private final ModalidadDAO modalidadDAO;
    private final EtapaDAO etapaDAO;

    public PostulanteService() {
        this.postulanteDAO = new PostulanteDAOImpl();
        this.usuarioDAO = new UsuarioDAOImpl();
        this.modalidadDAO = new ModalidadDAOImpl();
        this.etapaDAO = new EtapaDAOImpl();
    }

    public List<Postulante> listarTodos() {
        return postulanteDAO.listar();
    }

    public List<Postulante> listarConFiltros(Integer idModalidad, String estado, String dni) {
        return postulanteDAO.listarConFiltros(idModalidad, estado, dni);
    }

    public Postulante buscarPorId(int idPostulante) {
        return postulanteDAO.buscarPorId(idPostulante);
    }

    public Postulante buscarPorDni(String dni) {
        return postulanteDAO.buscarPorDni(dni);
    }

    public Postulante buscarPorIdUsuario(int idUsuario) {
        return postulanteDAO.buscarPorIdUsuario(idUsuario);
    }

    public List<Modalidad> listarModalidades() {
        return modalidadDAO.listar();
    }

    public LocalDate obtenerFechaCorteEdad() {
        return etapaDAO.obtenerFechaCorteEdad();
    }

    /**
     * Calcula estadísticas por estado utilizando colecciones Map (Tema 9).
     */
    public Map<String, Integer> obtenerEstadisticasPorEstado() {
        List<Postulante> todos = postulanteDAO.listar();
        Map<String, Integer> conteo = new LinkedHashMap<>();
        conteo.put("TOTAL", todos.size());
        conteo.put("INSCRITO", 0);
        conteo.put("EN REVISION", 0);
        conteo.put("APTO", 0);
        conteo.put("NO APTO", 0);
        conteo.put("PRESELECCIONADO", 0);
        conteo.put("NO PRESELECCIONADO", 0);
        conteo.put("POSTULO A SELECCION", 0);
        conteo.put("SELECCIONADO", 0);
        conteo.put("NO SELECCIONADO", 0);

        for (Postulante p : todos) {
            String est = p.getEstado();
            conteo.put(est, conteo.getOrDefault(est, 0) + 1);
        }
        return conteo;
    }

    /**
     * Valida exhaustivamente los campos de formato (RF-04) y las Reglas de Negocio RN-01 a RN-06.
     */
    public void validarDatosYReglasInscripcion(Postulante p, boolean esEdicion) throws ReglaNegocioException {
        if (p == null) {
            throw new ReglaNegocioException("Los datos del postulante son obligatorios.");
        }

        // 1. Validaciones de formato con expresiones regulares (RF-04, CP-08)
        if (!ValidadorUtil.esDniValido(p.getDni())) {
            throw new ReglaNegocioException("RF-04: El DNI debe contener exactamente 8 dígitos numéricos.");
        }
        if (ValidadorUtil.esVacio(p.getNombres()) || ValidadorUtil.esVacio(p.getApellidos())) {
            throw new ReglaNegocioException("RF-04: Los nombres y apellidos del postulante son obligatorios.");
        }
        if (p.getFechaNacimiento() == null) {
            throw new ReglaNegocioException("RF-04: La fecha de nacimiento es obligatoria.");
        }
        if (!ValidadorUtil.esCorreoValido(p.getCorreo())) {
            throw new ReglaNegocioException("RF-04: El formato del correo electrónico no es válido.");
        }
        if (!ValidadorUtil.esCelularPeruanoValido(p.getCelular())) {
            throw new ReglaNegocioException("RF-04: El número de celular debe tener 9 dígitos y empezar con 9.");
        }
        if (ValidadorUtil.esVacio(p.getRegion()) || ValidadorUtil.esVacio(p.getColegio())) {
            throw new ReglaNegocioException("RF-04: La región y el nombre del colegio son obligatorios.");
        }
        if (p.getIdModalidad() == null || p.getIdModalidad() <= 0) {
            throw new ReglaNegocioException("RF-04: Debe seleccionar una modalidad de postulación válida.");
        }

        // 2. RN-01: Solo pueden inscribirse postulantes de nacionalidad peruana
        if (ValidadorUtil.esVacio(p.getNacionalidad())
                || !"PERUANA".equalsIgnoreCase(p.getNacionalidad().trim())) {
            throw new ReglaNegocioException("RN-01",
                    "Solo pueden inscribirse postulantes de nacionalidad peruana.");
        }

        // 3. RN-06: Un DNI solo puede tener una inscripción (CP-08)
        Postulante existentePorDni = postulanteDAO.buscarPorDni(p.getDni().trim());
        if (existentePorDni != null) {
            if (!esEdicion || !existentePorDni.getIdPostulante().equals(p.getIdPostulante())) {
                throw new ReglaNegocioException("RN-06",
                        "El DNI " + p.getDni().trim() + " ya registra una inscripción en la convocatoria vigente.");
            }
        }

        // Cargar modalidad desde la base de datos
        Modalidad modalidad = modalidadDAO.buscarPorId(p.getIdModalidad());
        if (modalidad == null) {
            throw new ReglaNegocioException("La modalidad seleccionada no existe en el sistema.");
        }
        p.setModalidad(modalidad);

        // 4. RN-02: Cálculo de edad a la fecha de corte parametrizable y límites por modalidad (CP-06, CP-07)
        LocalDate fechaCorte = etapaDAO.obtenerFechaCorteEdad();
        int edadCalculada = FormatoUtil.calcularEdad(p.getFechaNacimiento(), fechaCorte);
        if (edadCalculada < 12 || edadCalculada > 90) {
            throw new ReglaNegocioException("RN-02",
                    "La fecha de nacimiento ingresada no corresponde a una edad válida para postular.");
        }
        p.setEdadCorte(edadCalculada);

        // Si NO acredita discapacidad y la modalidad tiene límite de edad, debe ser menor de edad_maxima (< 22 o < 31)
        if (!p.isDiscapacidadAcreditada() && modalidad.getEdadMaxima() != null) {
            int limiteExclusivo = modalidad.getEdadMaxima();
            if (edadCalculada >= limiteExclusivo) {
                throw new ReglaNegocioException("RN-02",
                        "Límite de edad excedido para la modalidad " + modalidad.getNombre()
                        + ": tiene " + edadCalculada + " años cumplidos a la fecha de corte ("
                        + FormatoUtil.formatearFecha(fechaCorte) + "), pero se exige ser menor de "
                        + limiteExclusivo + " años.");
            }
        }

        // 5. RN-03: Cursando último grado de secundaria o egresado; en Ordinaria egreso entre 2023 y 2026
        String situacion = p.getSituacionEstudios();
        if (!"CURSANDO_5TO".equals(situacion) && !"EGRESADO".equals(situacion)) {
            throw new ReglaNegocioException("RN-03",
                    "El postulante debe estar cursando el último grado de secundaria o haber egresado.");
        }
        if (modalidad.esOrdinaria()) {
            if (p.getAnioEgreso() < 2023 || p.getAnioEgreso() > 2026) {
                throw new ReglaNegocioException("RN-03",
                        "En la modalidad Ordinaria, el año de egreso de secundaria debe estar entre 2023 y 2026.");
            }
        } else {
            if (p.getAnioEgreso() < 1970 || p.getAnioEgreso() > 2026) {
                throw new ReglaNegocioException("RN-03",
                        "El año de conclusión o cursado de secundaria debe estar entre 1970 y 2026.");
            }
        }

        // 6. RN-04: En la modalidad Ordinaria, debe pertenecer al tercio superior
        if (modalidad.esOrdinaria() && !p.isTercioSuperior()) {
            throw new ReglaNegocioException("RN-04",
                    "En la modalidad Ordinaria, el postulante debe pertenecer al tercio superior de rendimiento académico.");
        }

        // 7. RN-05: En la modalidad Ordinaria, clasificación socioeconómica de pobreza o pobreza extrema
        if (modalidad.esOrdinaria()) {
            String cse = p.getClasificacionSocioeconomica();
            if (!"POBREZA".equals(cse) && !"POBREZA_EXTREMA".equals(cse)) {
                throw new ReglaNegocioException("RN-05",
                        "En la modalidad Ordinaria, el hogar debe tener clasificación socioeconómica de Pobreza o Pobreza Extrema vigente.");
            }
        }
    }

    /**
     * Registra en una única transacción JDBC el Usuario y el Postulante (RF-03, CP-05, CP-09).
     * Si ocurre cualquier error en la segunda inserción (o fallo simulado CP-09), ejecuta rollback completo.
     */
    public Postulante registrarInscripcion(Postulante postulante, String passwordPlano, boolean simularFalloSegundaInsercion)
            throws ReglaNegocioException {

        // Verificar que la etapa 1 de inscripción esté vigente
        Etapa etapaInscripcion = etapaDAO.buscarPorOrden(1);
        if (etapaInscripcion != null && !etapaInscripcion.isVigenteHoy()) {
            throw new ReglaNegocioException("RN-07",
                    "La etapa de Inscripción al ENP (" + etapaInscripcion.getRangoFechasFormateado() + ") no se encuentra vigente.");
        }

        if (ValidadorUtil.esVacio(passwordPlano) || passwordPlano.trim().length() < 6) {
            throw new ReglaNegocioException("RF-03: La contraseña debe tener al menos 6 caracteres.");
        }

        validarDatosYReglasInscripcion(postulante, false);

        String username = !ValidadorUtil.esVacio(postulante.getUsername())
                ? postulante.getUsername().trim()
                : postulante.getDni().trim();

        if (usuarioDAO.buscarPorUsername(username) != null) {
            throw new ReglaNegocioException("RN-06",
                    "El usuario '" + username + "' ya se encuentra registrado en el sistema.");
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(username);
        nuevoUsuario.setPasswordHash(PasswordUtil.hashPassword(passwordPlano.trim()));
        nuevoUsuario.setRol("POSTULANTE");
        nuevoUsuario.setActivo(true);

        postulante.setEstado("INSCRITO");

        try (Connection conn = ConexionDB.obtenerConexion()) {
            conn.setAutoCommit(false);
            try {
                // Primera operación de la transacción: insertar Usuario
                int idUsuarioGenerado = usuarioDAO.insertar(conn, nuevoUsuario);
                postulante.setIdUsuario(idUsuarioGenerado);
                postulante.setUsername(username);

                // Soporte explícito para probar CP-09 (Fallo forzado en la segunda inserción)
                if (simularFalloSegundaInsercion) {
                    throw new SQLException("CP-09: Fallo forzado en la segunda inserción (postulante) para verificar rollback transaccional.");
                }

                // Segunda operación de la transacción: insertar Postulante
                postulanteDAO.insertar(conn, postulante);

                conn.commit();
                BitacoraUtil.registrarEvento("INSCRIPCION", username,
                        "Inscripción exitosa de postulante DNI " + postulante.getDni()
                        + " en modalidad " + postulante.getModalidadNombre() + " (Estado: INSCRITO).");
                return postulante;
            } catch (SQLException ex) {
                conn.rollback();
                BitacoraUtil.registrarEvento("ROLLBACK_INSCRIPCION", username,
                        "Transacción de inscripción revertida (rollback) para DNI " + postulante.getDni() + ": " + ex.getMessage());
                if (simularFalloSegundaInsercion) {
                    throw new ReglaNegocioException("CP-09: Se produjo el fallo controlado en la segunda inserción. "
                            + "La transacción ejecutó ROLLBACK y no se guardó el usuario ni el postulante.");
                }
                throw new AccesoDatosException("Error en la transacción de registro de inscripción. Se revirtieron los cambios.", ex);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error de conexión al procesar la transacción de inscripción.", ex);
        }
    }

    /**
     * Edita la inscripción de un postulante respetando estrictamente RN-07 y RN-01..RN-05 (RF-05, CP-10).
     */
    public void actualizarInscripcion(Postulante datosNuevos, String usuarioActor) throws ReglaNegocioException {
        if (datosNuevos == null || datosNuevos.getIdPostulante() == null) {
            throw new ReglaNegocioException("No se especificó la inscripción a editar.");
        }

        Postulante actual = postulanteDAO.buscarPorId(datosNuevos.getIdPostulante());
        if (actual == null) {
            throw new ReglaNegocioException("El postulante indicado no existe.");
        }

        // RN-07: La inscripción solo puede editarse mientras el estado sea INSCRITO y la etapa de inscripción esté vigente (CP-10)
        if (!"INSCRITO".equals(actual.getEstado())) {
            throw new ReglaNegocioException("RN-07",
                    "Operación no permitida: La inscripción solo puede editarse mientras el estado sea INSCRITO "
                    + "(estado actual: " + actual.getEstado() + ").");
        }

        Etapa etapaInscripcion = etapaDAO.buscarPorOrden(1);
        if (etapaInscripcion != null && !etapaInscripcion.isVigenteHoy()) {
            throw new ReglaNegocioException("RN-07",
                    "Operación no permitida: La etapa de inscripción (" + etapaInscripcion.getRangoFechasFormateado()
                    + ") no se encuentra vigente.");
        }

        // Conservar DNI e identificadores inmutables
        datosNuevos.setDni(actual.getDni());
        datosNuevos.setIdUsuario(actual.getIdUsuario());
        datosNuevos.setEstado(actual.getEstado());

        validarDatosYReglasInscripcion(datosNuevos, true);

        postulanteDAO.actualizar(datosNuevos);
        BitacoraUtil.registrarEvento("EDICION_INSCRIPCION", usuarioActor,
                "Actualización de datos de inscripción del postulante DNI " + actual.getDni() + ".");
    }

    /**
     * Elimina una inscripción verificando que se encuentre en estado INSCRITO (RN-07, RF-09).
     */
    public void eliminarInscripcion(int idPostulante, String usuarioActor) throws ReglaNegocioException {
        Postulante actual = postulanteDAO.buscarPorId(idPostulante);
        if (actual == null) {
            throw new ReglaNegocioException("La inscripción que intenta eliminar no existe.");
        }
        if (!"INSCRITO".equals(actual.getEstado())) {
            throw new ReglaNegocioException("RN-07",
                    "Solo se pueden eliminar inscripciones cuyo estado sea INSCRITO (estado actual: " + actual.getEstado() + ").");
        }
        postulanteDAO.eliminar(idPostulante);
        BitacoraUtil.registrarEvento("ELIMINACION_INSCRIPCION", usuarioActor,
                "Eliminación de inscripción en estado INSCRITO del DNI " + actual.getDni() + ".");
    }

    /**
     * Paso 1 de Revisión: Cambia el estado de INSCRITO a EN REVISION (RF-10, RN-08).
     */
    public void iniciarRevision(int idPostulante, String usuarioAdmin) throws ReglaNegocioException {
        Postulante actual = postulanteDAO.buscarPorId(idPostulante);
        if (actual == null) {
            throw new ReglaNegocioException("El postulante no existe.");
        }
        if (!"INSCRITO".equals(actual.getEstado())) {
            throw new ReglaNegocioException("RN-08",
                    "Transición inválida: Solo se puede pasar a EN REVISIÓN desde el estado INSCRITO "
                    + "(estado actual: " + actual.getEstado() + ").");
        }
        postulanteDAO.actualizarEstadoRevision(idPostulante, "EN REVISION", actual.getObservacionRevision());
        BitacoraUtil.registrarEvento("CAMBIO_ESTADO", usuarioAdmin,
                "Postulante DNI " + actual.getDni() + " pasó de INSCRITO a EN REVISION.");
    }

    /**
     * Paso 2 de Revisión: Dictamina APTO o NO APTO respetando RN-08 y exigiendo observación si es NO APTO (RF-10, CP-11).
     */
    public void dictaminarRevision(int idPostulante, String resultadoRevision, String observacion, String usuarioAdmin)
            throws ReglaNegocioException {

        Postulante actual = postulanteDAO.buscarPorId(idPostulante);
        if (actual == null) {
            throw new ReglaNegocioException("El postulante no existe.");
        }

        // RN-08: Los estados avanzan en orden; para dictaminar APTO o NO APTO debe estar EN REVISION
        if (!"EN REVISION".equals(actual.getEstado())) {
            throw new ReglaNegocioException("RN-08",
                    "Transición de estado no permitida: El postulante debe estar en estado EN REVISIÓN antes de ser declarado APTO o NO APTO "
                    + "(estado actual: " + actual.getEstado() + ").");
        }

        if (!"APTO".equals(resultadoRevision) && !"NO APTO".equals(resultadoRevision)) {
            throw new ReglaNegocioException("El resultado de la revisión debe ser APTO o NO APTO.");
        }

        // CP-11 / RF-10: Observación obligatoria cuando es NO APTO
        if ("NO APTO".equals(resultadoRevision) && ValidadorUtil.esVacio(observacion)) {
            throw new ReglaNegocioException("RF-10",
                    "Cuando el postulante es declarado NO APTO, es obligatorio registrar una observación fundamentando el motivo.");
        }

        String obsFinal = ValidadorUtil.esVacio(observacion)
                ? "Requisitos de inscripción verificados y conformes."
                : observacion.trim();

        postulanteDAO.actualizarEstadoRevision(idPostulante, resultadoRevision, obsFinal);
        BitacoraUtil.registrarEvento("CAMBIO_ESTADO", usuarioAdmin,
                "Postulante DNI " + actual.getDni() + " pasó de EN REVISION a " + resultadoRevision
                + " (Obs: " + obsFinal + ").");
    }

    /**
     * Registra el puntaje del ENP y determina automáticamente PRESELECCIONADO o NO PRESELECCIONADO (RF-11, RN-08, RN-09, CP-12, CP-13).
     */
    public String registrarPuntajeEnp(int idPostulante, BigDecimal puntaje, String usuarioAdmin)
            throws ReglaNegocioException {

        Postulante actual = postulanteDAO.buscarPorId(idPostulante);
        if (actual == null) {
            throw new ReglaNegocioException("El postulante no existe.");
        }

        // RN-08 / CP-13: No se puede preseleccionar ni registrar puntaje a quien no fue declarado APTO
        if (!"APTO".equals(actual.getEstado())) {
            throw new ReglaNegocioException("RN-08",
                    "Operación no permitida: Solo se puede registrar puntaje del ENP a postulantes en estado APTO "
                    + "(estado actual del postulante DNI " + actual.getDni() + ": " + actual.getEstado() + ").");
        }

        if (puntaje == null) {
            throw new ReglaNegocioException("RN-09", "Debe ingresar un puntaje numérico válido para el ENP.");
        }

        // RN-09: El puntaje máximo referencial para este ejercicio es 120 (rango 0 a 120)
        if (puntaje.compareTo(BigDecimal.ZERO) < 0 || puntaje.compareTo(new BigDecimal("120.00")) > 0) {
            throw new ReglaNegocioException("RN-09",
                    "El puntaje del ENP debe encontrarse entre 0 y 120 puntos (puntaje ingresado: " + puntaje + ").");
        }

        int puntajeMinimoModalidad = (actual.getModalidad() != null)
                ? actual.getModalidad().getPuntajeMinimoEnp()
                : 84;

        String nuevoEstado = (puntaje.compareTo(BigDecimal.valueOf(puntajeMinimoModalidad)) >= 0)
                ? "PRESELECCIONADO"
                : "NO PRESELECCIONADO";

        postulanteDAO.registrarPuntajeEnp(idPostulante, puntaje, nuevoEstado);

        BitacoraUtil.registrarEvento("CAMBIO_ESTADO", usuarioAdmin,
                "Registro ENP para DNI " + actual.getDni() + " (" + actual.getModalidadNombre()
                + "): Puntaje = " + FormatoUtil.formatearPuntaje(puntaje)
                + " (Mínimo exigido: " + puntajeMinimoModalidad + ") -> Nuevo estado: " + nuevoEstado + ".");

        return nuevoEstado;
    }
}
