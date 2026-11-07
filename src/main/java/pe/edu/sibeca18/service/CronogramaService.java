package pe.edu.sibeca18.service;

import java.time.LocalDate;
import java.util.List;
import pe.edu.sibeca18.dao.EtapaDAO;
import pe.edu.sibeca18.dao.impl.EtapaDAOImpl;
import pe.edu.sibeca18.exception.ReglaNegocioException;
import pe.edu.sibeca18.model.Etapa;
import pe.edu.sibeca18.util.BitacoraUtil;
import pe.edu.sibeca18.util.FormatoUtil;
import pe.edu.sibeca18.util.ValidadorUtil;

/**
 * Servicio para administrar el cronograma parametrizable, publicación de resultados y fecha de corte (RF-12, RN-02, RN-13).
 */
public class CronogramaService {

    private final EtapaDAO etapaDAO;

    public CronogramaService() {
        this.etapaDAO = new EtapaDAOImpl();
    }

    public List<Etapa> listarEtapas() {
        return etapaDAO.listar();
    }

    public Etapa buscarPorId(int idEtapa) {
        return etapaDAO.buscarPorId(idEtapa);
    }

    public Etapa buscarPorOrden(int orden) {
        return etapaDAO.buscarPorOrden(orden);
    }

    public LocalDate obtenerFechaCorteEdad() {
        return etapaDAO.obtenerFechaCorteEdad();
    }

    public void actualizarFechaCorteEdad(LocalDate nuevaFechaCorte, String usuarioAdmin) throws ReglaNegocioException {
        if (nuevaFechaCorte == null) {
            throw new ReglaNegocioException("Debe ingresar una fecha de corte válida.");
        }
        etapaDAO.actualizarFechaCorteEdad(nuevaFechaCorte);
        BitacoraUtil.registrarEvento("PARAMETRO_SISTEMA", usuarioAdmin,
                "Fecha de corte de edad (RN-02) actualizada a " + FormatoUtil.formatearFecha(nuevaFechaCorte) + ".");
    }

    public void actualizarEtapa(Etapa etapa, String usuarioAdmin) throws ReglaNegocioException {
        if (etapa == null || etapa.getIdEtapa() == null) {
            throw new ReglaNegocioException("Datos de etapa inválidos.");
        }
        if (ValidadorUtil.esVacio(etapa.getNombre())) {
            throw new ReglaNegocioException("El nombre de la etapa es obligatorio.");
        }
        if (etapa.getFechaInicio() == null || etapa.getFechaFin() == null) {
            throw new ReglaNegocioException("Las fechas de inicio y fin de la etapa son obligatorias.");
        }
        if (etapa.getFechaFin().isBefore(etapa.getFechaInicio())) {
            throw new ReglaNegocioException("La fecha de fin no puede ser anterior a la fecha de inicio.");
        }
        etapaDAO.actualizar(etapa);
        BitacoraUtil.registrarEvento("CRONOGRAMA", usuarioAdmin,
                "Actualización de etapa #" + etapa.getOrden() + " (" + etapa.getNombre()
                + "): " + etapa.getRangoFechasFormateado()
                + " | Publicada: " + (etapa.isPublicada() ? "SÍ" : "NO") + ".");
    }

    /**
     * Verifica si una etapa específica (3=Aptos, 5=Preseleccionados, 8=Seleccionados) está publicada (RN-13).
     */
    public boolean esEtapaPublicada(int ordenEtapa) {
        Etapa e = etapaDAO.buscarPorOrden(ordenEtapa);
        return e != null && e.isPublicada();
    }

    /**
     * Determina qué momento de postulación a selección está vigente hoy (1, 2, o 0 si ninguno está vigente).
     */
    public int obtenerMomentoPostulacionVigente() {
        LocalDate hoy = LocalDate.now();
        Etapa mom1 = etapaDAO.buscarPorOrden(6);
        if (mom1 != null && mom1.estaVigenteEn(hoy)) {
            return 1;
        }
        Etapa mom2 = etapaDAO.buscarPorOrden(7);
        if (mom2 != null && mom2.estaVigenteEn(hoy)) {
            return 2;
        }
        return 0;
    }
}
