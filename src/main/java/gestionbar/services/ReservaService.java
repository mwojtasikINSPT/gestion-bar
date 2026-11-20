package gestionbar.services;

import gestionbar.DAOs.IPersonaDAO;
import gestionbar.DAOs.ICrud;
import gestionbar.DAOs.IReservaDAO;

import gestionbar.models.Persona;
import gestionbar.models.Rol;
import gestionbar.models.Mesa;
import gestionbar.models.Reserva;

import gestionbar.DTOs.ReservaDTO;

import gestionbar.utils.GeneradorCodigo;
import gestionbar.utils.Validaciones;

import gestionbar.exceptions.EntidadNoEncontradaException;
import gestionbar.exceptions.OperacionNoPermitidaException;

import java.util.ArrayList;
import java.util.List;

public final class ReservaService implements IReservaService {

    private final IPersonaDAO personaDAO;
    private final ICrud<Mesa, String> mesaDAO;
    private final IReservaDAO reservaDAO;
    private final DisponibilidadMesa disponibilidadMesa;

    public ReservaService(IPersonaDAO personaDAO,
            ICrud<Mesa, String> mesaDAO,
            IReservaDAO reservaDAO,
            DisponibilidadMesa disponibilidadMesa) {

        this.personaDAO = personaDAO;
        this.mesaDAO = mesaDAO;
        this.reservaDAO = reservaDAO;
        this.disponibilidadMesa = disponibilidadMesa;
    }

    private Persona obtenerClienteActivo(String idCliente) {

        idCliente = Validaciones.validarCodigo(idCliente, "CLI");

        Persona persona = personaDAO.buscarPorId(idCliente);

        if (persona == null) {
            throw new EntidadNoEncontradaException(
                    "Cliente no encontrado: " + idCliente);
        }

        if (persona.getRol() != Rol.CLIENTE) {
            throw new OperacionNoPermitidaException(
                    "El ID no corresponde a un cliente");
        }

        if (!persona.isActivo()) {
            throw new OperacionNoPermitidaException(
                    "El cliente está inactivo");
        }

        return persona;
    }

    private Mesa obtenerMesaActiva(String idMesa) {

        idMesa = Validaciones.validarCodigo(idMesa, "MTA");

        Mesa mesa = mesaDAO.buscarPorId(idMesa);

        if (mesa == null) {
            throw new EntidadNoEncontradaException(
                    "Mesa no encontrada: " + idMesa);
        }

        if (!mesa.isActivo()) {
            throw new OperacionNoPermitidaException(
                    "La mesa está inactiva");
        }

        return mesa;
    }

    private Reserva obtenerReserva(String idReserva) {

        idReserva = Validaciones.validarCodigo(idReserva, "RES");

        Reserva reserva = reservaDAO.buscarPorId(idReserva);

        if (reserva == null) {
            throw new EntidadNoEncontradaException(
                    "Reserva no encontrada: " + idReserva);
        }

        return reserva;
    }

    private void validarClienteSinReserva(String idCliente) {

        if (reservaDAO.tieneReservaActiva(idCliente)) {
            throw new OperacionNoPermitidaException(
                    "El cliente ya tiene una reserva activa");
        }
    }

    private String generarId() {

        List<String> ids = new ArrayList<>();

        for (Reserva reserva : reservaDAO.listar()) {
            ids.add(reserva.getId());
        }

        return GeneradorCodigo.generarSiguienteId(ids, "RES");
    }

    private ReservaDTO convertirDTO(Reserva reserva) {

        Persona cliente = personaDAO.buscarPorId(reserva.getIdCliente());
        String nombreCliente = cliente == null
                ? "No disponible"
                : cliente.getNombre() + " " + cliente.getApellido();

        return new ReservaDTO(
                reserva.getId(),
                reserva.getIdCliente(),
                nombreCliente,
                reserva.getIdMesa(),
                reserva.isActivo()
        );
    }

    public String agregarReserva(String idCliente, String idMesa) {

        Persona cliente = obtenerClienteActivo(idCliente);
        Mesa mesa = obtenerMesaActiva(idMesa);

        validarClienteSinReserva(cliente.getId());
        disponibilidadMesa.validarLugarParaReserva(mesa);

        String id = generarId();

        Reserva reserva = new Reserva(
                id,
                cliente.getId(),
                mesa.getId()
        );

        reservaDAO.agregar(reserva);

        return id;
    }

    public ReservaDTO buscarPorId(String idReserva) {

        return convertirDTO(obtenerReserva(idReserva));
    }

    public List<ReservaDTO> listarReservas() {

        List<ReservaDTO> resultado = new ArrayList<>();

        for (Reserva reserva : reservaDAO.listar()) {
            resultado.add(convertirDTO(reserva));
        }

        return resultado;
    }

    public List<ReservaDTO> listarPorCliente(String idCliente) {

        idCliente = Validaciones.validarCodigo(idCliente, "CLI");

        Persona persona = personaDAO.buscarPorId(idCliente);

        if (persona == null || persona.getRol() != Rol.CLIENTE) {
            throw new EntidadNoEncontradaException(
                    "Cliente no encontrado: " + idCliente);
        }

        List<ReservaDTO> resultado = new ArrayList<>();

        for (Reserva reserva :
                reservaDAO.listarPorCliente(idCliente)) {

            resultado.add(convertirDTO(reserva));
        }

        return resultado;
    }

    public List<ReservaDTO> listarPorMesa(String idMesa) {

        idMesa = Validaciones.validarCodigo(idMesa, "MTA");

        if (mesaDAO.buscarPorId(idMesa) == null) {
            throw new EntidadNoEncontradaException(
                    "Mesa no encontrada: " + idMesa);
        }

        List<ReservaDTO> resultado = new ArrayList<>();

        for (Reserva reserva : reservaDAO.listarPorMesa(idMesa)) {
            resultado.add(convertirDTO(reserva));
        }

        return resultado;
    }

    public int consultarLugaresDisponibles(String idMesa) {

        Mesa mesa = obtenerMesaActiva(idMesa);

        disponibilidadMesa.validarMeseroActivo(mesa);

        return disponibilidadMesa.lugaresLibres(mesa);
    }

    public void cambiarMesa(String idReserva, String idNuevaMesa) {

        Reserva reserva = obtenerReserva(idReserva);

        if (!reserva.isActivo()) {
            throw new OperacionNoPermitidaException(
                    "No se puede modificar una reserva inactiva");
        }

        obtenerClienteActivo(reserva.getIdCliente());

        Mesa nuevaMesa = obtenerMesaActiva(idNuevaMesa);

        if (reserva.getIdMesa()
                .equalsIgnoreCase(nuevaMesa.getId())) {

            throw new OperacionNoPermitidaException(
                    "El cliente ya tiene reservada esa mesa");
        }

        disponibilidadMesa.validarLugarParaReserva(nuevaMesa);

        reserva.setIdMesa(nuevaMesa.getId());

        reservaDAO.actualizar(reserva);
    }

    public void cambiarCliente(String idReserva, String idNuevoCliente) {

        Reserva reserva = obtenerReserva(idReserva);

        if (!reserva.isActivo()) {
            throw new OperacionNoPermitidaException(
                    "No se puede modificar una reserva inactiva");
        }

        Persona nuevoCliente = obtenerClienteActivo(idNuevoCliente);

        if (reserva.getIdCliente()
                .equalsIgnoreCase(nuevoCliente.getId())) {

            throw new OperacionNoPermitidaException(
                    "La reserva ya pertenece a ese cliente");
        }

        validarClienteSinReserva(nuevoCliente.getId());

        Mesa mesa = obtenerMesaActiva(reserva.getIdMesa());

        disponibilidadMesa.validarMeseroActivo(mesa);

        reserva.setIdCliente(nuevoCliente.getId());

        reservaDAO.actualizar(reserva);
    }

    public void eliminarReserva(String idReserva) {

        Reserva reserva = obtenerReserva(idReserva);

        if (!reserva.isActivo()) {
            throw new OperacionNoPermitidaException(
                    "La reserva ya está inactiva");
        }

        reservaDAO.eliminar(reserva.getId());
    }

    public void reactivarReserva(String idReserva) {

        Reserva reserva = obtenerReserva(idReserva);

        if (reserva.isActivo()) {
            throw new OperacionNoPermitidaException(
                    "La reserva ya está activa");
        }

        obtenerClienteActivo(reserva.getIdCliente());

        Mesa mesa = obtenerMesaActiva(reserva.getIdMesa());

        validarClienteSinReserva(reserva.getIdCliente());
        disponibilidadMesa.validarLugarParaReserva(mesa);

        reservaDAO.reactivar(reserva.getId());
    }
}
