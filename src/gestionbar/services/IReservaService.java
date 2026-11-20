package gestionbar.services;

import gestionbar.DTOs.ReservaDTO;
import java.util.List;

public interface IReservaService {
    String agregarReserva(String idCliente, String idMesa);
    ReservaDTO buscarPorId(String idReserva);
    List<ReservaDTO> listarReservas();
    List<ReservaDTO> listarPorCliente(String idCliente);
    List<ReservaDTO> listarPorMesa(String idMesa);
    int consultarLugaresDisponibles(String idMesa);
    void cambiarMesa(String idReserva, String idNuevaMesa);
    void cambiarCliente(String idReserva, String idNuevoCliente);
    void eliminarReserva(String idReserva);
    void reactivarReserva(String idReserva);
}
