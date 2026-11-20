package gestionbar.services;

import gestionbar.DTOs.MesaDTO;
import java.util.List;

public interface IMesaService {
    String agregarMesa(int capacidad);
    MesaDTO buscarPorId(String idMesa);
    List<MesaDTO> listarMesas();
    List<MesaDTO> listarMesasActivas();
    List<MesaDTO> listarMesasDisponibles();
    int consultarLugaresDisponibles(String idMesa);
    void actualizarCapacidad(String idMesa, int nuevaCapacidad);
    void eliminarMesa(String idMesa);
    void reactivarMesa(String idMesa);
}
