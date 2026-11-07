package pe.edu.sibeca18.dao;

import java.time.LocalDate;
import java.util.List;
import pe.edu.sibeca18.model.Etapa;

/**
 * Interfaz DAO para la entidad Etapa y parámetros del cronograma (Sección 8: listar, buscarPorId, actualizar).
 */
public interface EtapaDAO {

    List<Etapa> listar();

    Etapa buscarPorId(int idEtapa);

    Etapa buscarPorOrden(int orden);

    boolean actualizar(Etapa etapa);

    LocalDate obtenerFechaCorteEdad();

    boolean actualizarFechaCorteEdad(LocalDate nuevaFechaCorte);
}
