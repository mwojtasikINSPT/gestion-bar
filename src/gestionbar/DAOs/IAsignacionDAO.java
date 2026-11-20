package gestionbar.DAOs;

import gestionbar.models.Asignacion;
import java.util.List;

public interface IAsignacionDAO extends ICrud<Asignacion, String> {

    void actualizarTodas(List<Asignacion> entidades);

    List<Asignacion> listarPorMesero(String idMesero);

    List<Asignacion> listarPorMesa(String idMesa);

    boolean tieneAsignacionActiva(String idMesa);

    Asignacion buscarAsignacionActivaPorMesa(String idMesa);
}
