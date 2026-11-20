package gestionbar.DAOs;

import gestionbar.models.Reserva;
import java.util.List;

public interface IReservaDAO extends ICrud<Reserva, String> {

    void actualizarTodas(List<Reserva> entidades);

    List<Reserva> listarPorCliente(String idCliente);

    List<Reserva> listarPorMesa(String idMesa);

    int contarReservasActivasPorMesa(String idMesa);

    boolean tieneReservaActiva(String idCliente);
}
