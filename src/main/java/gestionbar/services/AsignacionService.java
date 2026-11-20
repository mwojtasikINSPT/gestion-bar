package gestionbar.services;

import gestionbar.DAOs.IPersonaDAO;
import gestionbar.DAOs.ICrud;
import gestionbar.DAOs.IAsignacionDAO;

import gestionbar.models.Persona;
import gestionbar.models.Rol;
import gestionbar.models.Mesa;
import gestionbar.models.Asignacion;

import gestionbar.DTOs.AsignacionDTO;
import gestionbar.DAOs.IReservaDAO;

import gestionbar.utils.GeneradorCodigo;
import gestionbar.utils.Validaciones;

import gestionbar.exceptions.EntidadNoEncontradaException;
import gestionbar.exceptions.OperacionNoPermitidaException;

import java.util.ArrayList;
import java.util.List;

public final class AsignacionService implements IAsignacionService {

    private final IPersonaDAO personaDAO;
    private final ICrud<Mesa, String> mesaDAO;
    private final IAsignacionDAO asignacionDAO;
    private final IReservaDAO reservaDAO;

    public AsignacionService(IPersonaDAO personaDAO,
            ICrud<Mesa, String> mesaDAO,
            IAsignacionDAO asignacionDAO,
            IReservaDAO reservaDAO) {

        this.personaDAO = personaDAO;
        this.mesaDAO = mesaDAO;
        this.asignacionDAO = asignacionDAO;
        this.reservaDAO = reservaDAO;
    }

    private Persona obtenerMeseroActivo(String idMesero) {

        idMesero = Validaciones.validarCodigo(idMesero, "MSR");

        Persona persona = personaDAO.buscarPorId(idMesero);

        if (persona == null) {
            throw new EntidadNoEncontradaException(
                    "Mesero no encontrado: " + idMesero);
        }

        if (persona.getRol() != Rol.MESERO) {
            throw new OperacionNoPermitidaException(
                    "El ID no corresponde a un mesero");
        }

        if (!persona.isActivo()) {
            throw new OperacionNoPermitidaException(
                    "El mesero está inactivo");
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

    private Asignacion obtenerAsignacion(String id) {

        id = Validaciones.validarCodigo(id, "ASG");

        Asignacion asignacion = asignacionDAO.buscarPorId(id);

        if (asignacion == null) {
            throw new EntidadNoEncontradaException(
                    "Asignación no encontrada: " + id);
        }

        return asignacion;
    }

    private String generarId() {

        List<String> ids = new ArrayList<>();

        for (Asignacion asignacion : asignacionDAO.listar()) {
            ids.add(asignacion.getId());
        }

        return GeneradorCodigo.generarSiguienteId(ids, "ASG");
    }

    private AsignacionDTO convertirDTO(Asignacion asignacion) {

        Persona mesero = personaDAO.buscarPorId(asignacion.getIdMesero());
        String nombreMesero = mesero == null
                ? "No disponible"
                : mesero.getNombre() + " " + mesero.getApellido();

        return new AsignacionDTO(
                asignacion.getId(),
                asignacion.getIdMesero(),
                nombreMesero,
                asignacion.getIdMesa(),
                asignacion.isActivo()
        );
    }

    public String agregarAsignacion(String idMesero, String idMesa) {

        Persona mesero = obtenerMeseroActivo(idMesero);
        Mesa mesa = obtenerMesaActiva(idMesa);

        String id = generarId();

        Asignacion asignacion = new Asignacion(
                id,
                mesero.getId(),
                mesa.getId()
        );

        asignacionDAO.agregar(asignacion);

        return id;
    }

    public AsignacionDTO buscarPorId(String id) {

        return convertirDTO(obtenerAsignacion(id));
    }

    public List<AsignacionDTO> listarAsignaciones() {

        List<AsignacionDTO> resultado = new ArrayList<>();

        for (Asignacion asignacion : asignacionDAO.listar()) {
            resultado.add(convertirDTO(asignacion));
        }

        return resultado;
    }

    public List<AsignacionDTO> listarPorMesero(String idMesero) {

        idMesero = Validaciones.validarCodigo(idMesero, "MSR");

        if (personaDAO.buscarPorId(idMesero) == null) {
            throw new EntidadNoEncontradaException(
                    "Mesero no encontrado: " + idMesero);
        }

        List<AsignacionDTO> resultado = new ArrayList<>();

        for (Asignacion asignacion
                : asignacionDAO.listarPorMesero(idMesero)) {

            resultado.add(convertirDTO(asignacion));
        }

        return resultado;
    }

    public List<AsignacionDTO> listarPorMesa(String idMesa) {

        idMesa = Validaciones.validarCodigo(idMesa, "MTA");

        if (mesaDAO.buscarPorId(idMesa) == null) {
            throw new EntidadNoEncontradaException(
                    "Mesa no encontrada: " + idMesa);
        }

        List<AsignacionDTO> resultado = new ArrayList<>();

        for (Asignacion asignacion
                : asignacionDAO.listarPorMesa(idMesa)) {

            resultado.add(convertirDTO(asignacion));
        }

        return resultado;
    }

    public void cambiarMesero(String idAsignacion,
            String idNuevoMesero) {

        Asignacion asignacion = obtenerAsignacion(idAsignacion);

        if (!asignacion.isActivo()) {
            throw new OperacionNoPermitidaException(
                    "La asignación está inactiva");
        }

        obtenerMesaActiva(asignacion.getIdMesa());

        Persona nuevoMesero = obtenerMeseroActivo(idNuevoMesero);

        if (asignacion.getIdMesero()
                .equalsIgnoreCase(nuevoMesero.getId())) {
            throw new OperacionNoPermitidaException(
                    "La mesa ya está asignada a ese mesero");
        }

        asignacion.setIdMesero(nuevoMesero.getId());

        asignacionDAO.actualizar(asignacion);
    }

    public void eliminarAsignacion(String idAsignacion) {

        Asignacion asignacion = obtenerAsignacion(idAsignacion);

        if (!asignacion.isActivo()) {
            throw new OperacionNoPermitidaException(
                    "La asignación ya está inactiva");
        }

        int reservasActivas
                = reservaDAO.contarReservasActivasPorMesa(
                        asignacion.getIdMesa());

        if (reservasActivas > 0) {
            throw new OperacionNoPermitidaException(
                    "No se puede eliminar la asignación porque "
                    + "la mesa tiene " + reservasActivas
                    + " reserva(s) activa(s). "
                    + "Primero debe cancelar o trasladar las reservas, "
                    + "o cambiar el mesero asignado.");
        }

        asignacionDAO.eliminar(asignacion.getId());
    }

    public void reactivarAsignacion(String idAsignacion) {

        Asignacion asignacion = obtenerAsignacion(idAsignacion);

        if (asignacion.isActivo()) {
            throw new OperacionNoPermitidaException(
                    "La asignación ya está activa");
        }

        obtenerMeseroActivo(asignacion.getIdMesero());
        obtenerMesaActiva(asignacion.getIdMesa());

        asignacionDAO.reactivar(asignacion.getId());
    }
}
