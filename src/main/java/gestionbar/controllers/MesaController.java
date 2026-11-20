package gestionbar.controllers;

import gestionbar.services.IMesaService;
import gestionbar.DTOs.MesaDTO;

import java.util.List;

public final class MesaController {

    private final IMesaService mesaService;

    public MesaController(IMesaService mesaService) {
        this.mesaService = mesaService;
    }

    public String agregarMesa(int capacidad) {
        return mesaService.agregarMesa(capacidad);
    }

    public MesaDTO buscarPorId(String idMesa) {
        return mesaService.buscarPorId(idMesa);
    }

    public List<MesaDTO> listarMesas() {
        return mesaService.listarMesas();
    }

    public List<MesaDTO> listarMesasActivas() {
        return mesaService.listarMesasActivas();
    }

    public List<MesaDTO> listarMesasDisponibles() {
        return mesaService.listarMesasDisponibles();
    }

    public int consultarLugaresDisponibles(String idMesa) {
        return mesaService.consultarLugaresDisponibles(idMesa);
    }

    public void actualizarCapacidad(String idMesa, int nuevaCapacidad) {

        mesaService.actualizarCapacidad(idMesa, nuevaCapacidad);
    }

    public void eliminarMesa(String idMesa) {
        mesaService.eliminarMesa(idMesa);
    }

    public void reactivarMesa(String idMesa) {
        mesaService.reactivarMesa(idMesa);
    }
}
