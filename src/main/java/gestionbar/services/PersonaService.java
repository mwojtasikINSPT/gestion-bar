package gestionbar.services;

import gestionbar.DAOs.IPersonaDAO;
import gestionbar.DAOs.IReservaDAO;
import gestionbar.DAOs.IAsignacionDAO;

import gestionbar.models.Persona;
import gestionbar.models.Rol;
import gestionbar.models.Reserva;
import gestionbar.models.Asignacion;

import gestionbar.DTOs.PersonaDTO;

import gestionbar.utils.GeneradorCodigo;

import gestionbar.exceptions.EntidadNoEncontradaException;
import gestionbar.exceptions.OperacionNoPermitidaException;
import gestionbar.exceptions.PersistenciaException;

import java.util.ArrayList;
import java.util.List;

public final class PersonaService implements IPersonaService {

    private final IPersonaDAO personaDAO;
    private final IReservaDAO reservaDAO;
    private final IAsignacionDAO asignacionDAO;

    public PersonaService(IPersonaDAO personaDAO,
            IReservaDAO reservaDAO,
            IAsignacionDAO asignacionDAO) {

        this.personaDAO = personaDAO;
        this.reservaDAO = reservaDAO;
        this.asignacionDAO = asignacionDAO;
    }

    public String agregarCliente(String nombre, String apellido) {

        String id = generarId("CLI");

        Persona cliente = new Persona(id, Rol.CLIENTE, nombre, apellido);

        personaDAO.agregar(cliente);

        return id;
    }

    public String agregarMesero(String nombre, String apellido) {

        String id = generarId("MSR");

        Persona mesero = new Persona(id, Rol.MESERO, nombre, apellido);

        personaDAO.agregar(mesero);

        return id;
    }

    private String generarId(String prefijo) {

        List<String> ids = new ArrayList<>();

        for (Persona persona : personaDAO.listar()) {
            ids.add(persona.getId());
        }

        return GeneradorCodigo.generarSiguienteId(ids, prefijo);
    }

    private Persona buscarEntidadPorId(String id) {

        Persona persona = personaDAO.buscarPorId(id);

        if (persona == null) {
            throw new EntidadNoEncontradaException(
                    "Persona no encontrada: " + id);
        }

        return persona;
    }

    public PersonaDTO buscarClientePorId(String id) {
        Persona persona = buscarEntidadPorId(id);

        if (persona.getRol() != Rol.CLIENTE) {
            throw new OperacionNoPermitidaException(
                    "El ID no corresponde a un cliente");
        }

        return convertirDTO(persona);
    }

    public PersonaDTO buscarMeseroPorId(String id) {
        Persona persona = buscarEntidadPorId(id);

        if (persona.getRol() != Rol.MESERO) {
            throw new OperacionNoPermitidaException(
                    "El ID no corresponde a un mesero");
        }

        return convertirDTO(persona);
    }

    private PersonaDTO convertirDTO(Persona persona) {
        return new PersonaDTO(persona.getId(), persona.getRol(),
                persona.getNombre(), persona.getApellido(),
                persona.isActivo());
    }

    public List<PersonaDTO> listarClientes() {

        List<PersonaDTO> resultado = new ArrayList<>();

        for (Persona persona : personaDAO.listarClientes()) {

            resultado.add(convertirDTO(persona));
        }

        return resultado;
    }

    public List<PersonaDTO> listarMeseros() {

        List<PersonaDTO> resultado = new ArrayList<>();

        for (Persona persona : personaDAO.listarMeseros()) {

            resultado.add(convertirDTO(persona));
        }

        return resultado;
    }

    public List<PersonaDTO> listarMeserosActivos() {

        List<PersonaDTO> resultado = new ArrayList<>();

        for (PersonaDTO mesero : listarMeseros()) {

            if (mesero.isActivo()) {
                resultado.add(mesero);
            }
        }

        return resultado;
    }

    public List<PersonaDTO> listarReemplazantes(String idMesero) {

        Persona saliente = buscarEntidadPorId(idMesero);

        if (saliente.getRol() != Rol.MESERO) {
            throw new OperacionNoPermitidaException(
                    "El ID no corresponde a un mesero");
        }

        List<PersonaDTO> resultado = new ArrayList<>();

        for (PersonaDTO mesero : listarMeserosActivos()) {

            if (!mesero.getId().equalsIgnoreCase(idMesero)) {
                resultado.add(mesero);
            }
        }

        return resultado;
    }

    public void actualizarPersona(String id,
            String nombre, String apellido) {

        Persona persona = buscarEntidadPorId(id);

        if (!persona.isActivo()) {
            throw new OperacionNoPermitidaException(
                    "No se puede modificar una persona inactiva");
        }

        persona.modificar(nombre, apellido);

        personaDAO.actualizar(persona);
    }

    public boolean clienteTieneReservaActiva(String idCliente) {

        Persona persona = buscarEntidadPorId(idCliente);

        if (persona.getRol() != Rol.CLIENTE) {
            throw new OperacionNoPermitidaException(
                    "El ID no corresponde a un cliente");
        }

        return reservaDAO.tieneReservaActiva(idCliente);
    }

    public void eliminarCliente(String idCliente) {

        Persona persona = buscarEntidadPorId(idCliente);

        if (persona.getRol() != Rol.CLIENTE) {
            throw new OperacionNoPermitidaException(
                    "El ID no corresponde a un cliente");
        }

        if (!persona.isActivo()) {
            throw new OperacionNoPermitidaException(
                    "El cliente ya está inactivo");
        }

        List<Reserva> reservasActivas = new ArrayList<>();

        for (Reserva reserva : reservaDAO.listarPorCliente(idCliente)) {

            if (reserva.isActivo()) {
                reservasActivas.add(reserva);
            }
        }

        List<Reserva> canceladas = new ArrayList<>();
        for (Reserva reserva : reservasActivas) {
            Reserva cancelada = new Reserva(reserva.getId(),
                    reserva.getIdCliente(), reserva.getIdMesa());
            cancelada.setActivo(false);
            canceladas.add(cancelada);
        }
        reservaDAO.actualizarTodas(canceladas);

        try {
            personaDAO.eliminar(idCliente);
        } catch (RuntimeException errorBaja) {
            if (!reservasActivas.isEmpty()) {
                try {
                    reservaDAO.actualizarTodas(reservasActivas);
                } catch (RuntimeException errorReversion) {
                    errorBaja.addSuppressed(errorReversion);
                    throw new PersistenciaException(
                            "Falló la baja y no se pudieron restaurar las reservas",
                            errorBaja);
                }
            }
            throw errorBaja;
        }
    }

    public void eliminarMesero(String idMesero,
            String idReemplazante) {

        Persona saliente = buscarEntidadPorId(idMesero);

        if (saliente.getRol() != Rol.MESERO) {
            throw new OperacionNoPermitidaException(
                    "El ID no corresponde a un mesero");
        }

        if (!saliente.isActivo()) {
            throw new OperacionNoPermitidaException(
                    "El mesero ya está inactivo");
        }

        List<Asignacion> asignacionesActivas = new ArrayList<>();

        for (Asignacion asignacion :
                asignacionDAO.listarPorMesero(idMesero)) {

            if (asignacion.isActivo()) {
                asignacionesActivas.add(asignacion);
            }
        }

        if (!asignacionesActivas.isEmpty()) {

            if (idReemplazante == null
                    || idReemplazante.isBlank()) {

                throw new OperacionNoPermitidaException(
                        "Debe seleccionar un mesero reemplazante");
            }

            Persona reemplazante = buscarEntidadPorId(idReemplazante);

            if (reemplazante == null || reemplazante.getRol() != Rol.MESERO
                    || !reemplazante.isActivo()
                    || reemplazante.getId()
                            .equalsIgnoreCase(idMesero)) {

                throw new OperacionNoPermitidaException(
                        "Debe seleccionar otro mesero activo");
            }

            List<Asignacion> reasignadas = new ArrayList<>();
            for (Asignacion asignacion : asignacionesActivas) {
                reasignadas.add(new Asignacion(asignacion.getId(),
                        reemplazante.getId(), asignacion.getIdMesa()));
            }
            asignacionDAO.actualizarTodas(reasignadas);
        }

        try {
            personaDAO.eliminar(idMesero);
        } catch (RuntimeException errorBaja) {
            if (!asignacionesActivas.isEmpty()) {
                try {
                    asignacionDAO.actualizarTodas(asignacionesActivas);
                } catch (RuntimeException errorReversion) {
                    errorBaja.addSuppressed(errorReversion);
                    throw new PersistenciaException(
                            "Falló la baja y no se pudieron restaurar las asignaciones",
                            errorBaja);
                }
            }
            throw errorBaja;
        }
    }

    public void reactivarPersona(String id) {

        Persona persona = buscarEntidadPorId(id);

        if (persona.isActivo()) {
            throw new OperacionNoPermitidaException(
                    "La persona ya está activa");
        }

        personaDAO.reactivar(id);
    }
}
