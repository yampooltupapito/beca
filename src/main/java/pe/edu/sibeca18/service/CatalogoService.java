package pe.edu.sibeca18.service;

import java.util.List;
import pe.edu.sibeca18.dao.InstitucionDAO;
import pe.edu.sibeca18.dao.ProgramaDAO;
import pe.edu.sibeca18.dao.impl.InstitucionDAOImpl;
import pe.edu.sibeca18.dao.impl.ProgramaDAOImpl;
import pe.edu.sibeca18.exception.ReglaNegocioException;
import pe.edu.sibeca18.model.Institucion;
import pe.edu.sibeca18.model.ProgramaEstudio;
import pe.edu.sibeca18.util.BitacoraUtil;
import pe.edu.sibeca18.util.ValidadorUtil;

/**
 * Servicio para el mantenimiento CRUD de Instituciones y Programas de Estudios (RF-13, RN-11).
 */
public class CatalogoService {

    private final InstitucionDAO institucionDAO;
    private final ProgramaDAO programaDAO;

    public CatalogoService() {
        this.institucionDAO = new InstitucionDAOImpl();
        this.programaDAO = new ProgramaDAOImpl();
    }

    public List<Institucion> listarInstituciones() {
        return institucionDAO.listar();
    }

    public Institucion buscarInstitucionPorId(int idInstitucion) {
        return institucionDAO.buscarPorId(idInstitucion);
    }

    public void guardarInstitucion(Institucion inst, String usuarioAdmin) throws ReglaNegocioException {
        if (inst == null || ValidadorUtil.esVacio(inst.getNombre()) || ValidadorUtil.esVacio(inst.getRegion())) {
            throw new ReglaNegocioException("RF-13: El nombre y la región de la institución son obligatorios.");
        }
        if (!"PUBLICA".equals(inst.getTipoGestion()) && !"PRIVADA".equals(inst.getTipoGestion())) {
            throw new ReglaNegocioException("RF-13: El tipo de gestión debe ser PUBLICA o PRIVADA.");
        }

        if (inst.getIdInstitucion() == null || inst.getIdInstitucion() <= 0) {
            institucionDAO.insertar(inst);
            BitacoraUtil.registrarEvento("CATALOGO_IES", usuarioAdmin,
                    "Registro de nueva institución elegible: " + inst.getNombre() + " (" + inst.getRegion() + ").");
        } else {
            institucionDAO.actualizar(inst);
            BitacoraUtil.registrarEvento("CATALOGO_IES", usuarioAdmin,
                    "Actualización de institución ID " + inst.getIdInstitucion() + ": " + inst.getNombre() + ".");
        }
    }

    public void eliminarInstitucion(int idInstitucion, String usuarioAdmin) throws ReglaNegocioException {
        Institucion existente = institucionDAO.buscarPorId(idInstitucion);
        if (existente == null) {
            throw new ReglaNegocioException("La institución especificada no existe.");
        }
        institucionDAO.eliminar(idInstitucion);
        BitacoraUtil.registrarEvento("CATALOGO_IES", usuarioAdmin,
                "Eliminación de institución: " + existente.getNombre() + ".");
    }

    public List<ProgramaEstudio> listarProgramas() {
        return programaDAO.listar();
    }

    public ProgramaEstudio buscarProgramaPorId(int idPrograma) {
        return programaDAO.buscarPorId(idPrograma);
    }

    public void guardarPrograma(ProgramaEstudio prog, String usuarioAdmin) throws ReglaNegocioException {
        if (prog == null) {
            throw new ReglaNegocioException("Los datos del programa son obligatorios.");
        }
        // Si seleccionó un idInstitucion, sincronizar nombre, gestión y región si no fueron provistos
        if (prog.getIdInstitucion() != null && prog.getIdInstitucion() > 0) {
            Institucion inst = institucionDAO.buscarPorId(prog.getIdInstitucion());
            if (inst != null) {
                prog.setInstitucion(inst.getNombre());
                if (ValidadorUtil.esVacio(prog.getTipoGestion())) {
                    prog.setTipoGestion(inst.getTipoGestion());
                }
                if (ValidadorUtil.esVacio(prog.getRegion())) {
                    prog.setRegion(inst.getRegion());
                }
            }
        }
        if (ValidadorUtil.esVacio(prog.getInstitucion()) || ValidadorUtil.esVacio(prog.getProgramaEstudios())) {
            throw new ReglaNegocioException("RF-13: La institución y el nombre del programa de estudios son obligatorios.");
        }
        if (ValidadorUtil.esVacio(prog.getRegion())) {
            throw new ReglaNegocioException("RF-13: La región del programa es obligatoria.");
        }
        if (!"PUBLICA".equals(prog.getTipoGestion()) && !"PRIVADA".equals(prog.getTipoGestion())) {
            throw new ReglaNegocioException("RF-13: El tipo de gestión debe ser PUBLICA o PRIVADA.");
        }
        if (prog.getVacantes() < 1) {
            throw new ReglaNegocioException("RN-11: El número de vacantes del programa debe ser como mínimo 1.");
        }

        if (prog.getIdPrograma() == null || prog.getIdPrograma() <= 0) {
            programaDAO.insertar(prog);
            BitacoraUtil.registrarEvento("CATALOGO_PROGRAMA", usuarioAdmin,
                    "Registro de programa: " + prog.getInstitucion() + " - " + prog.getProgramaEstudios()
                    + " (Vacantes: " + prog.getVacantes() + ").");
        } else {
            programaDAO.actualizar(prog);
            BitacoraUtil.registrarEvento("CATALOGO_PROGRAMA", usuarioAdmin,
                    "Actualización de programa ID " + prog.getIdPrograma() + ": " + prog.getInstitucion()
                    + " - " + prog.getProgramaEstudios() + " (Vacantes: " + prog.getVacantes() + ").");
        }
    }

    public void eliminarPrograma(int idPrograma, String usuarioAdmin) throws ReglaNegocioException {
        ProgramaEstudio existente = programaDAO.buscarPorId(idPrograma);
        if (existente == null) {
            throw new ReglaNegocioException("El programa de estudios no existe.");
        }
        if (existente.getTotalPostulantes() > 0) {
            throw new ReglaNegocioException("No se puede eliminar el programa '" + existente.getProgramaEstudios()
                    + "' porque tiene " + existente.getTotalPostulantes() + " postulación(es) asociada(s).");
        }
        programaDAO.eliminar(idPrograma);
        BitacoraUtil.registrarEvento("CATALOGO_PROGRAMA", usuarioAdmin,
                "Eliminación de programa: " + existente.getInstitucion() + " - " + existente.getProgramaEstudios() + ".");
    }
}
