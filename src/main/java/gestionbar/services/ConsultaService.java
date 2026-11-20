package gestionbar.services;

import gestionbar.DAOs.IPersonaDAO;
import gestionbar.DAOs.ICrud;
import gestionbar.DAOs.IReservaDAO;
import gestionbar.DAOs.IAsignacionDAO;

import gestionbar.DTOs.ClienteMesa;
import gestionbar.DTOs.PersonaDTO;

import gestionbar.models.Persona;
import gestionbar.models.Rol;
import gestionbar.models.Mesa;
import gestionbar.models.Reserva;
import gestionbar.models.Asignacion;

import gestionbar.exceptions.EntidadNoEncontradaException;
import gestionbar.exceptions.OperacionNoPermitidaException;

import java.util.ArrayList;
import java.util.List;

public final class ConsultaService implements IConsultaService {

    private final IPersonaDAO personaDAO;
    private final ICrud<Mesa, String> mesaDAO;
    private final IReservaDAO reservaDAO;
    private final IAsignacionDAO asignacionDAO;

    public ConsultaService(
            IPersonaDAO personaDAO,
            ICrud<Mesa, String> mesaDAO,
            IReservaDAO reservaDAO,
            IAsignacionDAO asignacionDAO) {

        this.personaDAO = personaDAO;
        this.mesaDAO = mesaDAO;
        this.reservaDAO = reservaDAO;
        this.asignacionDAO = asignacionDAO;
    }

    // CONSULTA 1:
    // Clientes con su mesa reservada.

    public List<ClienteMesa> consultarClientesConMesa() {

        List<ClienteMesa> resultados = new ArrayList<>();

        for (Reserva reserva : reservaDAO.listar()) {

            if (!reserva.isActivo()) {
                continue;
            }

            Persona persona = personaDAO.buscarPorId(
                    reserva.getIdCliente());

            Mesa mesa = mesaDAO.buscarPorId(
                    reserva.getIdMesa());

            if (persona == null || persona.getRol() != Rol.CLIENTE
                    || !persona.isActivo()
                    || mesa == null
                    || !mesa.isActivo()) {
                continue;
            }

            Asignacion asignacion =
                    asignacionDAO.buscarAsignacionActivaPorMesa(
                            mesa.getId());

            if (!tieneMeseroActivo(asignacion)) {
                continue;
            }

            PersonaDTO clienteDTO = crearPersonaDTO(persona);

            resultados.add(
                    new ClienteMesa(clienteDTO, mesa.getId()));
        }

        return resultados;
    }

    // CONSULTA 2:
    // Clientes atendidos por un mesero.

    public List<ClienteMesa> consultarClientesPorMesero(
            String idMesero) {

        Persona mesero = obtenerMeseroActivo(idMesero);

        List<ClienteMesa> resultados = new ArrayList<>();

        List<Asignacion> asignaciones =
                asignacionDAO.listarPorMesero(mesero.getId());

        for (Asignacion asignacion : asignaciones) {

            if (!asignacion.isActivo()) {
                continue;
            }

            Mesa mesa = mesaDAO.buscarPorId(
                    asignacion.getIdMesa());

            if (mesa == null || !mesa.isActivo()) {
                continue;
            }

            List<Reserva> reservas =
                    reservaDAO.listarPorMesa(mesa.getId());

            for (Reserva reserva : reservas) {

                if (!reserva.isActivo()) {
                    continue;
                }

                Persona persona = personaDAO.buscarPorId(
                        reserva.getIdCliente());

                if (persona == null || persona.getRol() != Rol.CLIENTE
                        || !persona.isActivo()) {
                    continue;
                }

                resultados.add(
                        new ClienteMesa(
                                crearPersonaDTO(persona),
                                mesa.getId()));
            }
        }

        return resultados;
    }

    // CONSULTA 3:
    // Mesero responsable de una mesa.

    public PersonaDTO consultarMeseroPorMesa(String idMesa) {

        Mesa mesa = mesaDAO.buscarPorId(idMesa);

        if (mesa == null) {
            throw new EntidadNoEncontradaException(
                    "No existe la mesa: " + idMesa);
        }

        if (!mesa.isActivo()) {
            throw new OperacionNoPermitidaException(
                    "La mesa está inactiva");
        }

        Asignacion asignacion =
                asignacionDAO.buscarAsignacionActivaPorMesa(
                        mesa.getId());

        if (asignacion == null) {
            return null;
        }

        Persona persona = personaDAO.buscarPorId(
                asignacion.getIdMesero());

        if (persona == null || persona.getRol() != Rol.MESERO
                || !persona.isActivo()) {
            return null;
        }

        return crearPersonaDTO(persona);
    }

    // CONSULTA AUXILIAR:
    // Obtener los datos del mesero consultado.

    public PersonaDTO consultarMesero(String idMesero) {

        Persona mesero = obtenerMeseroActivo(idMesero);

        return crearPersonaDTO(mesero);
    }

    // MÉTODOS PRIVADOS

    private Persona obtenerMeseroActivo(String idMesero) {

        Persona persona = personaDAO.buscarPorId(idMesero);

        if (persona == null || persona.getRol() != Rol.MESERO) {
            throw new EntidadNoEncontradaException(
                    "No existe el mesero: " + idMesero);
        }

        if (!persona.isActivo()) {
            throw new OperacionNoPermitidaException(
                    "El mesero está inactivo");
        }

        return persona;
    }

    private boolean tieneMeseroActivo(Asignacion asignacion) {

        if (asignacion == null || !asignacion.isActivo()) {
            return false;
        }

        Persona persona = personaDAO.buscarPorId(
                asignacion.getIdMesero());

        return persona != null && persona.getRol() == Rol.MESERO
                && persona.isActivo();
    }

    private PersonaDTO crearPersonaDTO(Persona persona) {

        return new PersonaDTO(
                persona.getId(),
                persona.getRol(),
                persona.getNombre(),
                persona.getApellido(),
                persona.isActivo()
        );
    }
}
