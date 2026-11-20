package gestionbar.services;

import gestionbar.DAOs.IAsignacionDAO;
import gestionbar.DAOs.IPersonaDAO;
import gestionbar.DAOs.IReservaDAO;
import gestionbar.exceptions.OperacionNoPermitidaException;
import gestionbar.models.Asignacion;
import gestionbar.models.Mesa;
import gestionbar.models.Persona;
import gestionbar.models.Rol;

public class DisponibilidadMesa {

    private final IReservaDAO reservaDAO;
    private final IAsignacionDAO asignacionDAO;
    private final IPersonaDAO personaDAO;

    public DisponibilidadMesa(IReservaDAO reservaDAO,
            IAsignacionDAO asignacionDAO, IPersonaDAO personaDAO) {
        this.reservaDAO = reservaDAO;
        this.asignacionDAO = asignacionDAO;
        this.personaDAO = personaDAO;
    }

    public int lugaresLibres(Mesa mesa) {
        int ocupados = reservaDAO.contarReservasActivasPorMesa(mesa.getId());
        return Math.max(0, mesa.getCapacidad() - ocupados);
    }

    public int lugaresDisponiblesParaReserva(Mesa mesa) {
        return mesa.isActivo() && tieneMeseroActivo(asignacionActiva(mesa))
                ? lugaresLibres(mesa) : 0;
    }

    public void validarMeseroActivo(Mesa mesa) {
        Asignacion asignacion = asignacionActiva(mesa);
        if (asignacion == null) {
            throw new OperacionNoPermitidaException(
                    "La mesa no tiene un mesero asignado");
        }
        if (!tieneMeseroActivo(asignacion)) {
            throw new OperacionNoPermitidaException(
                    "La mesa no tiene un mesero activo asignado");
        }
    }

    public void validarLugarParaReserva(Mesa mesa) {
        validarMeseroActivo(mesa);
        if (lugaresLibres(mesa) == 0) {
            throw new OperacionNoPermitidaException(
                    "La mesa no tiene capacidad disponible");
        }
    }

    private Asignacion asignacionActiva(Mesa mesa) {
        return asignacionDAO.buscarAsignacionActivaPorMesa(mesa.getId());
    }

    private boolean tieneMeseroActivo(Asignacion asignacion) {
        if (asignacion == null) {
            return false;
        }
        Persona persona = personaDAO.buscarPorId(asignacion.getIdMesero());
        return persona != null && persona.getRol() == Rol.MESERO && persona.isActivo();
    }
}
