package gestionbar.controllers;

import gestionbar.services.IAsignacionService;
import gestionbar.DTOs.AsignacionDTO;

import java.util.List;

public final class AsignacionController {

    private final IAsignacionService asignacionService;

    public AsignacionController(IAsignacionService asignacionService) {
        this.asignacionService = asignacionService;
    }

    public String agregarAsignacion(String idMesero, String idMesa) {

        return asignacionService.agregarAsignacion(idMesero, idMesa);
    }

    public AsignacionDTO buscarPorId(String idAsignacion) {

        return asignacionService.buscarPorId(idAsignacion);
    }

    public List<AsignacionDTO> listarAsignaciones() {

        return asignacionService.listarAsignaciones();
    }

    public List<AsignacionDTO> listarPorMesero(String idMesero) {

        return asignacionService.listarPorMesero(idMesero);
    }

    public List<AsignacionDTO> listarPorMesa(String idMesa) {

        return asignacionService.listarPorMesa(idMesa);
    }

    public void cambiarMesero(String idAsignacion,
            String idNuevoMesero) {

        asignacionService.cambiarMesero(
                idAsignacion, idNuevoMesero);
    }

    public void eliminarAsignacion(String idAsignacion) {

        asignacionService.eliminarAsignacion(idAsignacion);
    }

    public void reactivarAsignacion(String idAsignacion) {

        asignacionService.reactivarAsignacion(idAsignacion);
    }
}
