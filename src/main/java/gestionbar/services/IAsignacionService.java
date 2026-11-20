package gestionbar.services;

import gestionbar.DTOs.AsignacionDTO;
import java.util.List;

public interface IAsignacionService {
    String agregarAsignacion(String idMesero, String idMesa);
    AsignacionDTO buscarPorId(String id);
    List<AsignacionDTO> listarAsignaciones();
    List<AsignacionDTO> listarPorMesero(String idMesero);
    List<AsignacionDTO> listarPorMesa(String idMesa);
    void cambiarMesero(String idAsignacion, String idNuevoMesero);
    void eliminarAsignacion(String idAsignacion);
    void reactivarAsignacion(String idAsignacion);
}
