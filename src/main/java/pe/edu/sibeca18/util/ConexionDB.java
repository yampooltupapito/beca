package pe.edu.sibeca18.util;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import pe.edu.sibeca18.exception.AccesoDatosException;

/**
 * Gestor de conexiones a MySQL 8 mediante un DataSource JNDI administrado por
 * Apache Tomcat 10.1 (java:comp/env/jdbc/sibeca18DS) o GlassFish 7 (jdbc/sibeca18DS).
 * No contiene credenciales fijas en el código fuente.
 */
public final class ConexionDB {

    private static final String JNDI_TOMCAT = "java:comp/env/jdbc/sibeca18DS";
    private static final String JNDI_GLASSFISH = "jdbc/sibeca18DS";

    private static volatile DataSource dataSource;

    private ConexionDB() {
    }

    /**
     * Permite inyectar un DataSource en entornos de prueba controlada (JUnit).
     *
     * @param ds DataSource configurado externamente
     */
    public static synchronized void setDataSource(DataSource ds) {
        dataSource = ds;
    }

    /**
     * Obtiene el DataSource desde el contexto JNDI del servidor de aplicaciones.
     *
     * @return DataSource activo
     */
    public static DataSource obtenerDataSource() {
        if (dataSource == null) {
            synchronized (ConexionDB.class) {
                if (dataSource == null) {
                    dataSource = buscarDataSourceJndi();
                }
            }
        }
        return dataSource;
    }

    private static DataSource buscarDataSourceJndi() {
        try {
            Context initContext = new InitialContext();
            try {
                return (DataSource) initContext.lookup(JNDI_TOMCAT);
            } catch (NamingException exTomcat) {
                return (DataSource) initContext.lookup(JNDI_GLASSFISH);
            }
        } catch (NamingException ex) {
            throw new AccesoDatosException(
                    "No se pudo localizar el DataSource JNDI 'jdbc/sibeca18DS'. "
                    + "Verifique la configuración del recurso en META-INF/context.xml o en el servidor Tomcat/GlassFish.",
                    ex);
        }
    }

    /**
     * Obtiene una conexión del pool del DataSource JNDI.
     *
     * @return Connection activa hacia MySQL 8
     * @throws SQLException si ocurre un error al obtener la conexión
     */
    public static Connection obtenerConexion() throws SQLException {
        return obtenerDataSource().getConnection();
    }
}
