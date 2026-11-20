package gestionbar.controllers;

import gestionbar.services.IReservaService;
import gestionbar.DTOs.ReservaDTO;

import java.util.List;

public final class ReservaController {

    private final IReservaService reservaService;

    public ReservaController(IReservaService reservaService) {
        this.reservaService = reservaService;
    }

    public String agregarReserva(String idCliente, String idMesa) {

        return reservaService.agregarReserva(idCliente, idMesa);
    }

    public ReservaDTO buscarPorId(String idReserva) {

        return reservaService.buscarPorId(idReserva);
    }

    public List<ReservaDTO> listarReservas() {

        return reservaService.listarReservas();
    }

    public List<ReservaDTO> listarPorCliente(String idCliente) {

        return reservaService.listarPorCliente(idCliente);
    }

    public List<ReservaDTO> listarPorMesa(String idMesa) {

        return reservaService.listarPorMesa(idMesa);
    }

    public int consultarLugaresDisponibles(String idMesa) {

        return reservaService.consultarLugaresDisponibles(idMesa);
    }

    public void cambiarMesa(String idReserva, String idNuevaMesa) {

        reservaService.cambiarMesa(idReserva, idNuevaMesa);
    }

    public void cambiarCliente(String idReserva, String idNuevoCliente) {

        reservaService.cambiarCliente(idReserva, idNuevoCliente);
    }

    public void eliminarReserva(String idReserva) {

        reservaService.eliminarReserva(idReserva);
    }

    public void reactivarReserva(String idReserva) {

        reservaService.reactivarReserva(idReserva);
    }
}
