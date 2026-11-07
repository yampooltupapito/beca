package pe.edu.sibeca18.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import pe.edu.sibeca18.dao.PostulacionSeleccionDAO;
import pe.edu.sibeca18.dao.PostulanteDAO;
import pe.edu.sibeca18.dao.ProgramaDAO;
import pe.edu.sibeca18.dao.impl.PostulacionSeleccionDAOImpl;
import pe.edu.sibeca18.dao.impl.PostulanteDAOImpl;
import pe.edu.sibeca18.dao.impl.ProgramaDAOImpl;
import pe.edu.sibeca18.exception.AccesoDatosException;
import pe.edu.sibeca18.exception.ReglaNegocioException;
import pe.edu.sibeca18.model.PostulacionSeleccion;
import pe.edu.sibeca18.model.Postulante;
import pe.edu.sibeca18.model.ProgramaEstudio;
import pe.edu.sibeca18.util.BitacoraUtil;
import pe.edu.sibeca18.util.ConexionDB;
import pe.edu.sibeca18.util.FormatoUtil;

/**
 * Servicio de Postulación a Selección, Ranking por Colecciones, Selección Transaccional
 * con Rollback, Hilo en Segundo Plano (Reto Opcional 7.4) y Exportación de Reportes TXT
 * (RF-08, RF-14, RF-15, RN-10, RN-11, RN-12).
 */
public class SeleccionService {

    private final PostulacionSeleccionDAO postulacionDAO;
    private final PostulanteDAO postulanteDAO;
    private final ProgramaDAO programaDAO;
    private final CronogramaService cronogramaService;

    // Estado sincronizado para el reto opcional de ejecución en segundo plano con Hilos (Sección 7.4)
    private static final Object HILO_LOCK = new Object();
    private static volatile String estadoTareaHilo = "INACTIVO";
    private static volatile String mensajeTareaHilo = "No se ha ejecutado ninguna tarea en segundo plano aún.";

    public SeleccionService() {
        this.postulacionDAO = new PostulacionSeleccionDAOImpl();
        this.postulanteDAO = new PostulanteDAOImpl();
        this.programaDAO = new ProgramaDAOImpl();
        this.cronogramaService = new CronogramaService();
    }

    public PostulacionSeleccion buscarPorPostulante(int idPostulante) {
        return postulacionDAO.buscarPorPostulante(idPostulante);
    }

    public List<PostulacionSeleccion> listarTodas() {
        return postulacionDAO.listarTodas();
    }

    /**
     * Registra o actualiza la postulación de un beneficiario preseleccionado a un programa con vacantes (RF-08, RN-10, RN-11, CP-14).
     */
    public void registrarOActualizarPostulacion(int idPostulante, int idPrograma, String usuarioActor)
            throws ReglaNegocioException {

        Postulante postulante = postulanteDAO.buscarPorId(idPostulante);
        if (postulante == null) {
            throw new ReglaNegocioException("El postulante no existe.");
        }

        // RN-10 / CP-14: Solo los preseleccionados pueden postular a la selección (o quienes ya postularon y están editando su opción)
        String estadoActual = postulante.getEstado();
        if (!"PRESELECCIONADO".equals(estadoActual) && !"POSTULO A SELECCION".equals(estadoActual)) {
            throw new ReglaNegocioException("RN-10",
                    "Operación bloqueada: Solo los postulantes en estado PRESELECCIONADO pueden postular a la selección "
                    + "(su estado actual es: " + estadoActual + ").");
        }

        // RN-10: Únicamente dentro de las fechas del primer o segundo momento
        int momentoVigente = cronogramaService.obtenerMomentoPostulacionVigente();
        if (momentoVigente == 0) {
            throw new ReglaNegocioException("RN-10",
                    "La postulación a la selección solo está permitida dentro de las fechas vigentes del Primer o Segundo Momento.");
        }

        // RN-11: Elegir un programa de estudios de una institución elegible con vacantes
        ProgramaEstudio programa = programaDAO.buscarPorId(idPrograma);
        if (programa == null || !programa.isActivo()) {
            throw new ReglaNegocioException("RN-11", "El programa de estudios seleccionado no existe o no se encuentra elegible.");
        }
        if (programa.getVacantes() <= 0) {
            throw new ReglaNegocioException("RN-11", "El programa seleccionado no cuenta con vacantes disponibles.");
        }

        PostulacionSeleccion existente = postulacionDAO.buscarPorPostulante(idPostulante);
        if (existente == null) {
            // Nueva postulación en transacción con cambio de estado a POSTULO A SELECCION (RN-08)
            try (Connection conn = ConexionDB.obtenerConexion()) {
                conn.setAutoCommit(false);
                try {
                    PostulacionSeleccion nueva = new PostulacionSeleccion();
                    nueva.setIdPostulante(idPostulante);
                    nueva.setIdPrograma(idPrograma);
                    nueva.setMomento(momentoVigente);
                    nueva.setFechaHora(LocalDateTime.now());
                    nueva.setResultado("PENDIENTE");

                    postulacionDAO.insertar(conn, nueva);
                    postulanteDAO.actualizarEstado(conn, idPostulante, "POSTULO A SELECCION");

                    conn.commit();
                    BitacoraUtil.registrarEvento("POSTULACION_SELECCION", usuarioActor,
                            "Postulante DNI " + postulante.getDni() + " postuló a '"
                            + programa.getInstitucion() + " - " + programa.getProgramaEstudios()
                            + "' (Momento " + momentoVigente + "). Estado -> POSTULO A SELECCION.");
                } catch (SQLException ex) {
                    conn.rollback();
                    throw ex;
                } finally {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException ex) {
                throw new AccesoDatosException("Error transaccional al registrar la postulación a selección.", ex);
            }
        } else {
            // Edición de postulación dentro del momento abierto (RF-08)
            existente.setIdPrograma(idPrograma);
            existente.setMomento(momentoVigente);
            existente.setFechaHora(LocalDateTime.now());
            postulacionDAO.actualizar(existente);
            BitacoraUtil.registrarEvento("EDICION_POSTULACION", usuarioActor,
                    "Postulante DNI " + postulante.getDni() + " cambió su postulación al programa '"
                    + programa.getInstitucion() + " - " + programa.getProgramaEstudios() + "'.");
        }
    }

    /**
     * Permite retirar la postulación mientras el momento siga abierto, regresando a PRESELECCIONADO (RF-08).
     */
    public void retirarPostulacion(int idPostulante, String usuarioActor) throws ReglaNegocioException {
        Postulante postulante = postulanteDAO.buscarPorId(idPostulante);
        if (postulante == null) {
            throw new ReglaNegocioException("El postulante no existe.");
        }
        if (!"POSTULO A SELECCION".equals(postulante.getEstado())) {
            throw new ReglaNegocioException("RN-08",
                    "Solo se puede retirar una postulación mientras el estado sea POSTULO A SELECCION.");
        }
        int momentoVigente = cronogramaService.obtenerMomentoPostulacionVigente();
        if (momentoVigente == 0) {
            throw new ReglaNegocioException("RN-10",
                    "No se puede retirar la postulación porque el periodo del momento de selección ha concluido.");
        }
        PostulacionSeleccion existente = postulacionDAO.buscarPorPostulante(idPostulante);
        if (existente == null) {
            throw new ReglaNegocioException("No existe una postulación activa para retirar.");
        }

        try (Connection conn = ConexionDB.obtenerConexion()) {
            conn.setAutoCommit(false);
            try {
                postulacionDAO.eliminar(conn, existente.getIdPostulacion());
                postulanteDAO.actualizarEstado(conn, idPostulante, "PRESELECCIONADO");
                conn.commit();
                BitacoraUtil.registrarEvento("RETIRO_POSTULACION", usuarioActor,
                        "Postulante DNI " + postulante.getDni() + " retiró su postulación a selección. Estado -> PRESELECCIONADO.");
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error transaccional al retirar la postulación.", ex);
        }
    }

    /**
     * Organiza y ordena el ranking por programa utilizando Colecciones Java (Map, List, Comparable)
     * según RN-12: mayor a menor puntaje ENP y, en caso de empate, fecha y hora de postulación ascendente.
     */
    public Map<ProgramaEstudio, List<PostulacionSeleccion>> construirRankingPorPrograma() {
        List<ProgramaEstudio> programas = programaDAO.listar();
        Map<ProgramaEstudio, List<PostulacionSeleccion>> mapaRanking = new LinkedHashMap<>();

        for (ProgramaEstudio prog : programas) {
            List<PostulacionSeleccion> postulaciones = postulacionDAO.listarPorPrograma(prog.getIdPrograma());
            // Ordenar mediante Collections.sort usando PostulacionSeleccion.compareTo (RN-12)
            Collections.sort(postulaciones);
            for (int i = 0; i < postulaciones.size(); i++) {
                PostulacionSeleccion item = postulaciones.get(i);
                item.setOrdenMerito(i + 1);
            }
            mapaRanking.put(prog, postulaciones);
        }
        return mapaRanking;
    }

    /**
     * Ejecuta el proceso integral de selección final en una única transacción JDBC (RF-14, RN-12, CP-15, CP-16).
     * Si simularErrorControlado es true (CP-16) o falla cualquier operación, ejecuta rollback total.
     *
     * @return resumen estadístico de la selección ejecutada
     */
    public Map<String, Integer> ejecutarSeleccionTransaccional(boolean simularErrorControlado, String usuarioAdmin)
            throws ReglaNegocioException {

        Map<ProgramaEstudio, List<PostulacionSeleccion>> rankingPorPrograma = construirRankingPorPrograma();
        int totalEvaluados = 0;
        for (List<PostulacionSeleccion> lista : rankingPorPrograma.values()) {
            totalEvaluados += lista.size();
        }
        if (totalEvaluados == 0) {
            throw new ReglaNegocioException("No existen postulaciones registradas para ejecutar el proceso de selección.");
        }

        int seleccionadosCount = 0;
        int noSeleccionadosCount = 0;
        int operacionesProcesadas = 0;

        try (Connection conn = ConexionDB.obtenerConexion()) {
            conn.setAutoCommit(false);
            try {
                for (Map.Entry<ProgramaEstudio, List<PostulacionSeleccion>> entry : rankingPorPrograma.entrySet()) {
                    ProgramaEstudio programa = entry.getKey();
                    List<PostulacionSeleccion> listaOrdenada = entry.getValue();
                    int vacantesDisponibles = programa.getVacantes();

                    for (int idx = 0; idx < listaOrdenada.size(); idx++) {
                        PostulacionSeleccion post = listaOrdenada.get(idx);
                        int ordenMerito = idx + 1;
                        String resultado = (ordenMerito <= vacantesDisponibles) ? "SELECCIONADO" : "NO SELECCIONADO";

                        // Actualizar resultado y orden de mérito en postulacion_seleccion
                        postulacionDAO.actualizarResultadoSeleccion(conn, post.getIdPostulacion(), ordenMerito, resultado);
                        // Actualizar estado vigente del postulante
                        postulanteDAO.actualizarEstado(conn, post.getIdPostulante(), resultado);

                        if ("SELECCIONADO".equals(resultado)) {
                            seleccionadosCount++;
                        } else {
                            noSeleccionadosCount++;
                        }
                        operacionesProcesadas++;

                        // CP-16: Provocar un error controlado a mitad de la selección para demostrar el rollback total
                        if (simularErrorControlado && operacionesProcesadas >= 1) {
                            throw new SQLException("CP-16: Error controlado provocado durante la ejecución de la selección tras procesar "
                                    + operacionesProcesadas + " registro(s).");
                        }
                    }
                }

                // Confirmar toda la selección únicamente cuando todos los programas terminaron sin errores
                conn.commit();

                BitacoraUtil.registrarEvento("SELECCION_EJECUTADA", usuarioAdmin,
                        "Selección final completada con COMMIT. Evaluados: " + totalEvaluados
                        + " | SELECCIONADOS: " + seleccionadosCount
                        + " | NO SELECCIONADOS: " + noSeleccionadosCount + ".");

                Map<String, Integer> resumen = new LinkedHashMap<>();
                resumen.put("EVALUADOS", totalEvaluados);
                resumen.put("SELECCIONADOS", seleccionadosCount);
                resumen.put("NO_SELECCIONADOS", noSeleccionadosCount);
                return resumen;

            } catch (SQLException ex) {
                conn.rollback();
                BitacoraUtil.registrarEvento("ROLLBACK_SELECCION", usuarioAdmin,
                        "Reversión total (ROLLBACK) del proceso de selección. Ningún estado fue modificado. Causa: " + ex.getMessage());
                if (simularErrorControlado) {
                    throw new ReglaNegocioException("CP-16: Se provocó el error controlado durante la selección. "
                            + "La transacción ejecutó ROLLBACK completo y ningún estado de postulante ni postulación cambió.");
                }
                throw new AccesoDatosException("Error durante la transacción de selección. Se revirtieron todos los cambios.", ex);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            throw new AccesoDatosException("Error de conexión al ejecutar la selección transaccional.", ex);
        }
    }

    /**
     * Reto opcional (Sección 7.4 - Tema 5: Hilos): Ejecuta la generación del ranking y selección
     * en segundo plano mediante un hilo sincronizado e informa el resultado al administrador.
     */
    public void ejecutarSeleccionEnSegundoPlano(String usuarioAdmin) {
        synchronized (HILO_LOCK) {
            if ("EN_EJECUCION".equals(estadoTareaHilo)) {
                return;
            }
            estadoTareaHilo = "EN_EJECUCION";
            mensajeTareaHilo = "Procesando ranking y selección en segundo plano...";
        }

        Thread hiloSeleccion = new Thread(() -> {
            try {
                Thread.sleep(600); // Simulación breve de procesamiento pesado sincronizado
                synchronized (HILO_LOCK) {
                    Map<String, Integer> res = ejecutarSeleccionTransaccional(false, usuarioAdmin + " (Hilo-Async)");
                    estadoTareaHilo = "COMPLETADO";
                    mensajeTareaHilo = "Tarea en segundo plano finalizada ("
                            + FormatoUtil.formatearFechaHora(LocalDateTime.now())
                            + "): Evaluados=" + res.get("EVALUADOS")
                            + ", Seleccionados=" + res.get("SELECCIONADOS")
                            + ", No Seleccionados=" + res.get("NO_SELECCIONADOS") + ".";
                }
            } catch (Exception ex) {
                synchronized (HILO_LOCK) {
                    estadoTareaHilo = "ERROR";
                    mensajeTareaHilo = "Error en hilo de selección: " + ex.getMessage();
                }
            }
        }, "SIBECA18-RankingWorker");
        hiloSeleccion.setDaemon(true);
        hiloSeleccion.start();
    }

    public String getEstadoTareaHilo() {
        return estadoTareaHilo;
    }

    public String getMensajeTareaHilo() {
        return mensajeTareaHilo;
    }

    /**
     * Obtiene la lista de postulantes seleccionados a partir de todas las postulaciones.
     */
    public List<PostulacionSeleccion> listarSeleccionados() {
        List<PostulacionSeleccion> todas = postulacionDAO.listarTodas();
        List<PostulacionSeleccion> seleccionados = new ArrayList<>();
        for (PostulacionSeleccion ps : todas) {
            if ("SELECCIONADO".equals(ps.getResultado())) {
                seleccionados.add(ps);
            }
        }
        return seleccionados;
    }

    /**
     * Calcula totales de seleccionados agrupados por programa mediante colecciones Map (RF-15).
     */
    public Map<String, Integer> calcularTotalesSeleccionadosPorPrograma() {
        Map<String, Integer> totales = new LinkedHashMap<>();
        for (PostulacionSeleccion ps : listarSeleccionados()) {
            String clave = ps.getInstitucionNombre() + " - " + ps.getProgramaNombre();
            totales.put(clave, totales.getOrDefault(clave, 0) + 1);
        }
        return totales;
    }

    /**
     * Calcula totales de seleccionados agrupados por modalidad mediante colecciones Map (RF-15).
     */
    public Map<String, Integer> calcularTotalesSeleccionadosPorModalidad() {
        Map<String, Integer> totales = new LinkedHashMap<>();
        for (PostulacionSeleccion ps : listarSeleccionados()) {
            String mod = ps.getModalidadNombre();
            totales.put(mod, totales.getOrDefault(mod, 0) + 1);
        }
        return totales;
    }

    /**
     * Genera el reporte oficial de seleccionados y ranking completo en texto plano usando StringBuilder (RF-15, Tema 3 y 4).
     */
    public String generarReporteSeleccionadosTxt() {
        List<PostulacionSeleccion> seleccionados = listarSeleccionados();
        Map<String, Integer> porPrograma = calcularTotalesSeleccionadosPorPrograma();
        Map<String, Integer> porModalidad = calcularTotalesSeleccionadosPorModalidad();
        Map<ProgramaEstudio, List<PostulacionSeleccion>> rankingCompleto = construirRankingPorPrograma();

        StringBuilder sb = new StringBuilder(2048);
        sb.append("=========================================================================================\n");
        sb.append(" PRONABEC - SIBECA-18 | REPORTE OFICIAL DE SELECCIONADOS - CONVOCATORIA 2027\n");
        sb.append(" Fecha y hora de emisión (es-PE): ").append(FormatoUtil.formatearFechaHora(LocalDateTime.now())).append("\n");
        sb.append("=========================================================================================\n\n");

        sb.append("1. RESUMEN DE SELECCIONADOS POR PROGRAMA DE ESTUDIOS\n");
        sb.append("-----------------------------------------------------------------------------------------\n");
        if (porPrograma.isEmpty()) {
            sb.append("   No existen becarios con estado SELECCIONADO aún. Ejecute la selección en el panel.\n");
        } else {
            for (Map.Entry<String, Integer> entry : porPrograma.entrySet()) {
                sb.append(String.format("   - %-65s : %d seleccionado(s)\n", entry.getKey(), entry.getValue()));
            }
        }
        sb.append("   TOTAL GENERAL DE SELECCIONADOS: ").append(seleccionados.size()).append("\n\n");

        sb.append("2. RESUMEN DE SELECCIONADOS POR MODALIDAD DE BECA 18\n");
        sb.append("-----------------------------------------------------------------------------------------\n");
        if (porModalidad.isEmpty()) {
            sb.append("   Sin registros de seleccionados por modalidad.\n");
        } else {
            for (Map.Entry<String, Integer> entry : porModalidad.entrySet()) {
                sb.append(String.format("   - %-45s : %d seleccionado(s)\n", entry.getKey(), entry.getValue()));
            }
        }
        sb.append("\n");

        sb.append("3. DETALLE DEL RANKING DE MÉRITO POR PROGRAMA (RN-12)\n");
        sb.append("-----------------------------------------------------------------------------------------\n");
        for (Map.Entry<ProgramaEstudio, List<PostulacionSeleccion>> entry : rankingCompleto.entrySet()) {
            ProgramaEstudio prog = entry.getKey();
            List<PostulacionSeleccion> lista = entry.getValue();
            sb.append("\n>> PROGRAMA: ").append(prog.getInstitucion()).append(" - ").append(prog.getProgramaEstudios())
              .append(" | Gestión: ").append(prog.getTipoGestion())
              .append(" | Región: ").append(prog.getRegion())
              .append(" | Vacantes: ").append(prog.getVacantes()).append("\n");

            if (lista.isEmpty()) {
                sb.append("   (Sin postulaciones registradas en este programa)\n");
            } else {
                sb.append(String.format("   %-6s | %-10s | %-28s | %-20s | %-12s | %-23s | %-16s\n",
                        "MÉRITO", "DNI", "POSTULANTE", "MODALIDAD", "PUNTAJE ENP", "FECHA/HORA POSTULACIÓN", "RESULTADO"));
                sb.append("   ----------------------------------------------------------------------------------------------------------------------------------\n");
                for (PostulacionSeleccion ps : lista) {
                    sb.append(String.format("   #%-5d | %-10s | %-28s | %-20s | %-12s | %-23s | %-16s\n",
                            ps.getOrdenMerito() != null ? ps.getOrdenMerito() : 0,
                            ps.getDniPostulante(),
                            recortar(ps.getNombrePostulante(), 28),
                            recortar(ps.getModalidadNombre(), 20),
                            ps.getPuntajeEnpFormateado(),
                            ps.getFechaHoraFormateada(),
                            ps.getResultado()));
                }
            }
        }
        sb.append("\n=========================================================================================\n");
        sb.append(" Fin del reporte generado por SIBECA-18.\n");
        return sb.toString();
    }

    private String recortar(String texto, int max) {
        if (texto == null) {
            return "";
        }
        return texto.length() <= max ? texto : texto.substring(0, max - 3) + "...";
    }
}
