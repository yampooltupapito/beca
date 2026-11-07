package pe.edu.sibeca18;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.postgresql.ds.PGSimpleDataSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import pe.edu.sibeca18.exception.ReglaNegocioException;
import pe.edu.sibeca18.model.Etapa;
import pe.edu.sibeca18.model.PostulacionSeleccion;
import pe.edu.sibeca18.model.Postulante;
import pe.edu.sibeca18.model.ProgramaEstudio;
import pe.edu.sibeca18.model.Usuario;
import pe.edu.sibeca18.service.AuthService;
import pe.edu.sibeca18.service.CronogramaService;
import pe.edu.sibeca18.service.PostulanteService;
import pe.edu.sibeca18.service.SeleccionService;
import pe.edu.sibeca18.util.BitacoraUtil;
import pe.edu.sibeca18.util.ConexionDB;
import pe.edu.sibeca18.util.ValidadorUtil;

/**
 * Suite integral de pruebas funcionales y de integración para verificar los 18 Casos de Prueba
 * (CP-01 a CP-18) y las 14 Reglas de Negocio (RN-01 a RN-14) sobre PostgreSQL 17 (base de datos: beca).
 * Lee el DataSource local desde src/main/webapp/META-INF/context.xml sin credenciales fijas en Java.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CasosPruebaSibeca18Test {

    private static AuthService authService;
    private static PostulanteService postulanteService;
    private static CronogramaService cronogramaService;
    private static SeleccionService seleccionService;
    private static Integer idPostulanteCp05;

    @BeforeAll
    static void inicializarDataSourceYServicios() throws Exception {
        File tempLog = new File("target/test-bitacora/bitacora_sibeca18_test.txt");
        BitacoraUtil.configurarRutaArchivo(tempLog.getAbsolutePath());

        // Leer configuración local del DataSource desde META-INF/context.xml (no versionado)
        File contextXml = new File("src/main/webapp/META-INF/context.xml");
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(contextXml);
        NodeList resources = doc.getElementsByTagName("Resource");
        Element resource = (Element) resources.item(0);

        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setUrl(resource.getAttribute("url"));
        ds.setUser(resource.getAttribute("username"));
        ds.setPassword(resource.getAttribute("password"));

        ConexionDB.setDataSource(ds);
        restaurarBaseDeDatosSemilla();

        authService = new AuthService();
        postulanteService = new PostulanteService();
        cronogramaService = new CronogramaService();
        seleccionService = new SeleccionService();
    }

    @AfterAll
    static void restaurarEstadoInicialSemilla() throws Exception {
        restaurarBaseDeDatosSemilla();
    }

    private static void restaurarBaseDeDatosSemilla() throws Exception {
        String sqlSchema = Files.readString(new File("database/schema.sql").toPath()).replace("\uFEFF", "");
        String sqlSeed = Files.readString(new File("database/seed.sql").toPath()).replace("\uFEFF", "");
        try (Connection conn = ConexionDB.obtenerConexion();
             Statement st = conn.createStatement()) {
            st.execute(sqlSchema);
            st.execute(sqlSeed);
        }
    }

    private Postulante crearPostulanteBase(String dni, LocalDate fechaNac, int idModalidad) {
        Postulante p = new Postulante();
        p.setDni(dni);
        p.setUsername(dni);
        p.setNombres("Postulante Prueba");
        p.setApellidos("Caso Académico");
        p.setFechaNacimiento(fechaNac);
        p.setNacionalidad("PERUANA");
        p.setCorreo("prueba." + dni + "@correo.pe");
        p.setCelular("987654321");
        p.setRegion("Ayacucho");
        p.setColegio("I.E. Mariscal Cáceres");
        p.setSituacionEstudios("EGRESADO");
        p.setAnioEgreso(2025);
        p.setTercioSuperior(true);
        p.setClasificacionSocioeconomica("POBREZA");
        p.setDiscapacidadAcreditada(false);
        p.setIdModalidad(idModalidad);
        return p;
    }

    @Test
    @Order(1)
    @DisplayName("CP-01: Inicio de sesión con credenciales válidas de administrador")
    void cp01_loginValidoAdministrador() throws Exception {
        Usuario admin = authService.autenticar("admin", "admin123");
        Assertions.assertNotNull(admin);
        Assertions.assertEquals("ADMINISTRADOR", admin.getRol());
        Assertions.assertTrue(admin.esAdministrador());
    }

    @Test
    @Order(2)
    @DisplayName("CP-02: Inicio de sesión con credenciales inválidas")
    void cp02_loginCredencialesInvalidas() {
        ReglaNegocioException ex = Assertions.assertThrows(ReglaNegocioException.class,
                () -> authService.autenticar("admin", "claveErronea999"));
        Assertions.assertTrue(ex.getMessage().toLowerCase().contains("incorrectos"));
    }

    @Test
    @Order(3)
    @DisplayName("CP-03 y CP-04: Verificación de roles y restricción de acceso (RN-14)")
    void cp03_cp04_controlAccesoYRoles() throws Exception {
        Usuario postulanteUser = authService.autenticar("70100001", "beca2027");
        Assertions.assertNotNull(postulanteUser);
        Assertions.assertTrue(postulanteUser.esPostulante());
        Assertions.assertFalse(postulanteUser.esAdministrador(),
                "CP-04: Un usuario con rol POSTULANTE no tiene permisos de ADMINISTRADOR.");
    }

    @Test
    @Order(4)
    @DisplayName("CP-05: Inscripción válida en modalidad Ordinaria (21 años, tercio superior, pobreza)")
    void cp05_inscripcionValidaOrdinaria() throws Exception {
        // Fecha de corte: 2026-10-09 -> Nacido el 2005-05-10 tiene 21 años cumplidos (< 22)
        Postulante nuevo = crearPostulanteBase("70900005", LocalDate.of(2005, 5, 10), 1);
        Postulante registrado = postulanteService.registrarInscripcion(nuevo, "beca2027", false);

        Assertions.assertNotNull(registrado.getIdPostulante());
        Assertions.assertEquals("INSCRITO", registrado.getEstado());
        Assertions.assertEquals(21, registrado.getEdadCorte());
        idPostulanteCp05 = registrado.getIdPostulante();
    }

    @Test
    @Order(5)
    @DisplayName("CP-06: Inscripción en modalidad Ordinaria con 22 años cumplidos a la fecha de corte")
    void cp06_inscripcionRechazadaPorEdadOrdinaria() {
        // Nacido el 2004-10-09 cumple exactamente 22 años el 2026-10-09 -> Rechazado (RN-02)
        Postulante mayor22 = crearPostulanteBase("70900006", LocalDate.of(2004, 10, 9), 1);
        ReglaNegocioException ex = Assertions.assertThrows(ReglaNegocioException.class,
                () -> postulanteService.registrarInscripcion(mayor22, "beca2027", false));
        Assertions.assertEquals("RN-02", ex.getCodigoRegla());
    }

    @Test
    @Order(6)
    @DisplayName("CP-07: Inscripción en Repared con 35 años; Fuerzas Armadas con 30 (aceptada) y 31 (rechazada)")
    void cp07_inscripcionModalidadesEspecialesEdad() throws Exception {
        // 1. Repared (idModalidad=5) con 35 años -> Aceptada
        Postulante repared35 = crearPostulanteBase("70900007", LocalDate.of(1991, 5, 10), 5);
        Postulante regRepared = postulanteService.registrarInscripcion(repared35, "beca2027", false);
        Assertions.assertEquals("INSCRITO", regRepared.getEstado());
        Assertions.assertEquals(35, regRepared.getEdadCorte());

        // 2. Fuerzas Armadas (idModalidad=9) con 30 años -> Aceptada (< 31)
        Postulante ffaa30 = crearPostulanteBase("70900008", LocalDate.of(1996, 5, 10), 9);
        Postulante regFfaa30 = postulanteService.registrarInscripcion(ffaa30, "beca2027", false);
        Assertions.assertEquals("INSCRITO", regFfaa30.getEstado());
        Assertions.assertEquals(30, regFfaa30.getEdadCorte());

        // 3. Fuerzas Armadas (idModalidad=9) con 31 años -> Rechazada
        Postulante ffaa31 = crearPostulanteBase("70900009", LocalDate.of(1995, 5, 10), 9);
        ReglaNegocioException exFfaa31 = Assertions.assertThrows(ReglaNegocioException.class,
                () -> postulanteService.registrarInscripcion(ffaa31, "beca2027", false));
        Assertions.assertEquals("RN-02", exFfaa31.getCodigoRegla());
    }

    @Test
    @Order(7)
    @DisplayName("CP-08: Inscripción con DNI de 7 dígitos y con un DNI ya registrado")
    void cp08_dniInvalidoYDniDuplicado() {
        // 1. DNI de 7 dígitos
        Postulante dni7 = crearPostulanteBase("1234567", LocalDate.of(2006, 5, 10), 1);
        ReglaNegocioException exFormato = Assertions.assertThrows(ReglaNegocioException.class,
                () -> postulanteService.registrarInscripcion(dni7, "beca2027", false));
        Assertions.assertTrue(exFormato.getMessage().contains("8 dígitos"));

        // 2. DNI ya registrado (70100001 existe en seed.sql)
        Postulante dniDuplicado = crearPostulanteBase("70100001", LocalDate.of(2006, 5, 10), 1);
        ReglaNegocioException exDuplicado = Assertions.assertThrows(ReglaNegocioException.class,
                () -> postulanteService.registrarInscripcion(dniDuplicado, "beca2027", false));
        Assertions.assertEquals("RN-06", exDuplicado.getCodigoRegla());
        Assertions.assertNotEquals(exFormato.getMessage(), exDuplicado.getMessage(),
                "CP-08: Ambas deben rechazarse con mensaje diferenciado.");
    }

    @Test
    @Order(8)
    @DisplayName("CP-09: Fallo forzado en la segunda inserción de la inscripción (Rollback total)")
    void cp09_rollbackFalloSegundaInsercionInscripcion() {
        String dniRollback = "70900099";
        Postulante pFallo = crearPostulanteBase(dniRollback, LocalDate.of(2006, 4, 10), 1);

        Assertions.assertThrows(ReglaNegocioException.class,
                () -> postulanteService.registrarInscripcion(pFallo, "beca2027", true));

        // Verificar que no quedó guardado ni el postulante ni el usuario en PostgreSQL
        Assertions.assertNull(postulanteService.buscarPorDni(dniRollback),
                "CP-09: No debe existir el postulante tras el rollback.");
        Assertions.assertThrows(ReglaNegocioException.class,
                () -> authService.autenticar(dniRollback, "beca2027"),
                "CP-09: No debe existir el usuario tras el rollback.");
    }

    @Test
    @Order(9)
    @DisplayName("CP-11 y CP-10: Revisión NO APTO sin observación (rechazado) y bloqueo de edición cuando ya es APTO")
    void cp11_cp10_revisionYBloqueoEdicion() throws Exception {
        // Iniciar revisión del postulante creado en CP-05 (INSCRITO -> EN REVISION)
        postulanteService.iniciarRevision(idPostulanteCp05, "admin");
        Assertions.assertEquals("EN REVISION", postulanteService.buscarPorId(idPostulanteCp05).getEstado());

        // CP-11: Intentar declarar NO APTO sin observación -> Rechazado
        Assertions.assertThrows(ReglaNegocioException.class,
                () -> postulanteService.dictaminarRevision(idPostulanteCp05, "NO APTO", "", "admin"),
                "CP-11: Revisar como NO APTO sin observación debe ser rechazado.");

        // Declarar APTO correctamente
        postulanteService.dictaminarRevision(idPostulanteCp05, "APTO", "Expediente conforme.", "admin");
        Postulante apto = postulanteService.buscarPorId(idPostulanteCp05);
        Assertions.assertEquals("APTO", apto.getEstado());

        // CP-10: Intentar editar la inscripción cuando el estado ya es APTO -> Operación no permitida (RN-07)
        apto.setColegio("I.E. Modificada Ilegalmente");
        ReglaNegocioException exRn07 = Assertions.assertThrows(ReglaNegocioException.class,
                () -> postulanteService.actualizarInscripcion(apto, apto.getDni()));
        Assertions.assertEquals("RN-07", exRn07.getCodigoRegla());
    }

    @Test
    @Order(10)
    @DisplayName("CP-13: Intentar preseleccionar a un postulante que no es APTO")
    void cp13_intentarPreseleccionarNoApto() throws Exception {
        // Crear postulante en estado INSCRITO e intentar registrarle puntaje ENP directamente sin ser APTO
        Postulante inscrito = crearPostulanteBase("70900013", LocalDate.of(2006, 5, 10), 1);
        postulanteService.registrarInscripcion(inscrito, "beca2027", false);

        ReglaNegocioException exRn08 = Assertions.assertThrows(ReglaNegocioException.class,
                () -> postulanteService.registrarPuntajeEnp(inscrito.getIdPostulante(), new BigDecimal("90"), "admin"));
        Assertions.assertEquals("RN-08", exRn08.getCodigoRegla());
    }

    @Test
    @Order(11)
    @DisplayName("CP-12: Registrar puntajes 84 y 83 en Ordinaria; 66 y 65 en modalidad especial")
    void cp12_limitesPuntajePreseleccion() throws Exception {
        // 1. En el postulante APTO de Ordinaria (idPostulanteCp05), probar 83 -> NO PRESELECCIONADO
        String res83 = postulanteService.registrarPuntajeEnp(idPostulanteCp05, new BigDecimal("83"), "admin");
        Assertions.assertEquals("NO PRESELECCIONADO", res83);

        // 2. Preparar otro postulante de Ordinaria en APTO y probar 84 -> PRESELECCIONADO
        Postulante ord84 = crearPostulanteBase("70900084", LocalDate.of(2006, 3, 15), 1);
        postulanteService.registrarInscripcion(ord84, "beca2027", false);
        postulanteService.iniciarRevision(ord84.getIdPostulante(), "admin");
        postulanteService.dictaminarRevision(ord84.getIdPostulante(), "APTO", "Conforme", "admin");
        String res84 = postulanteService.registrarPuntajeEnp(ord84.getIdPostulante(), new BigDecimal("84"), "admin");
        Assertions.assertEquals("PRESELECCIONADO", res84);

        // 3. En los postulantes de modalidad especial (Repared #70900007 y FFAA #70900008): probar 66 y 65
        Postulante repared = postulanteService.buscarPorDni("70900007");
        postulanteService.iniciarRevision(repared.getIdPostulante(), "admin");
        postulanteService.dictaminarRevision(repared.getIdPostulante(), "APTO", "Conforme", "admin");
        String res66 = postulanteService.registrarPuntajeEnp(repared.getIdPostulante(), new BigDecimal("66"), "admin");
        Assertions.assertEquals("PRESELECCIONADO", res66);

        Postulante ffaa = postulanteService.buscarPorDni("70900008");
        postulanteService.iniciarRevision(ffaa.getIdPostulante(), "admin");
        postulanteService.dictaminarRevision(ffaa.getIdPostulante(), "APTO", "Conforme", "admin");
        String res65 = postulanteService.registrarPuntajeEnp(ffaa.getIdPostulante(), new BigDecimal("65"), "admin");
        Assertions.assertEquals("NO PRESELECCIONADO", res65);
    }

    @Test
    @Order(12)
    @DisplayName("CP-14: Un postulante no preseleccionado intenta postular a selección")
    void cp14_bloqueoPostulacionNoPreseleccionado() {
        // El postulante 3 (DNI 70100003, id=3) está en estado NO PRESELECCIONADO (puntaje 83 en Ordinaria)
        ReglaNegocioException exRn10 = Assertions.assertThrows(ReglaNegocioException.class,
                () -> seleccionService.registrarOActualizarPostulacion(3, 1, "70100003"));
        Assertions.assertEquals("RN-10", exRn10.getCodigoRegla());
    }

    @Test
    @Order(13)
    @DisplayName("CP-16: Provocar un error controlado durante la ejecución de la selección (Rollback total)")
    void cp16_rollbackSeleccionErrorControlado() {
        String estadoAntesPost2 = postulanteService.buscarPorId(2).getEstado();
        String estadoAntesPost4 = postulanteService.buscarPorId(4).getEstado();

        Assertions.assertThrows(ReglaNegocioException.class,
                () -> seleccionService.ejecutarSeleccionTransaccional(true, "admin"));

        // Verificar que tras el rollback ningún estado cambió en PostgreSQL
        Assertions.assertEquals(estadoAntesPost2, postulanteService.buscarPorId(2).getEstado(),
                "CP-16: El estado del postulante 2 no debe cambiar tras el rollback.");
        Assertions.assertEquals(estadoAntesPost4, postulanteService.buscarPorId(4).getEstado(),
                "CP-16: El estado del postulante 4 no debe cambiar tras el rollback.");
        Assertions.assertEquals("PENDIENTE", seleccionService.buscarPorPostulante(2).getResultado());
    }

    @Test
    @Order(14)
    @DisplayName("CP-15: Ejecutar la selección con un empate y un programa de una sola vacante")
    void cp15_ejecutarSeleccionConEmpateYUnaVacante() throws Exception {
        // En el Programa 1 (UNI - Ingeniería de Sistemas, 1 sola vacante) compiten:
        // - Postulante 2 (DNI 70100002, puntaje 84, postuló el 2027-01-10 08:30:00)
        // - Postulante 4 (DNI 70100004, puntaje 84, postuló el 2027-01-10 09:15:00)
        Map<String, Integer> resumen = seleccionService.ejecutarSeleccionTransaccional(false, "admin");
        Assertions.assertTrue(resumen.get("EVALUADOS") >= 5);

        PostulacionSeleccion resPost2 = seleccionService.buscarPorPostulante(2);
        PostulacionSeleccion resPost4 = seleccionService.buscarPorPostulante(4);

        Assertions.assertEquals(1, resPost2.getOrdenMerito());
        Assertions.assertEquals("SELECCIONADO", resPost2.getResultado(),
                "CP-15: Gana quien postuló primero (Postulante 2).");
        Assertions.assertEquals("SELECCIONADO", postulanteService.buscarPorId(2).getEstado());

        Assertions.assertEquals(2, resPost4.getOrdenMerito());
        Assertions.assertEquals("NO SELECCIONADO", resPost4.getResultado(),
                "CP-15: El segundo en el empate queda NO SELECCIONADO porque el programa tiene 1 sola vacante.");
        Assertions.assertEquals("NO SELECCIONADO", postulanteService.buscarPorId(4).getEstado());
    }

    @Test
    @Order(15)
    @DisplayName("CP-17 y CP-18: Publicación controlada de resultados (RN-13) y reporte TXT")
    void cp17_cp18_publicacionControladaYReporteTxt() throws Exception {
        // CP-18: Inicialmente la Etapa 8 (Publicación final de seleccionados) NO está publicada en seed.sql
        Assertions.assertFalse(cronogramaService.esEtapaPublicada(8),
                "CP-18: Inicialmente la etapa 8 está sin publicar y el postulante no ve el resultado final.");

        // El administrador publica la Etapa 8
        Etapa etapa8 = cronogramaService.buscarPorOrden(8);
        etapa8.setPublicada(true);
        cronogramaService.actualizarEtapa(etapa8, "admin");

        Assertions.assertTrue(cronogramaService.esEtapaPublicada(8),
                "CP-18: Tras publicar la etapa 8, el resultado final queda visible para el postulante.");

        // Verificar generación del reporte TXT de seleccionados (RF-15)
        String reporteTxt = seleccionService.generarReporteSeleccionadosTxt();
        Assertions.assertTrue(reporteTxt.contains("70100002"));
        Assertions.assertTrue(reporteTxt.contains("SELECCIONADO"));
    }
}
