package gestionbar.services;

import gestionbar.DAOs.ICrud;
import gestionbar.DAOs.IAsignacionDAO;
import gestionbar.DAOs.IReservaDAO;

import gestionbar.models.Asignacion;
import gestionbar.models.Mesa;
import gestionbar.models.Reserva;

import gestionbar.DTOs.MesaDTO;

import gestionbar.utils.GeneradorCodigo;
import gestionbar.utils.Validaciones;

import gestionbar.exceptions.EntidadNoEncontradaException;
import gestionbar.exceptions.OperacionNoPermitidaException;
import gestionbar.exceptions.PersistenciaException;

import java.util.ArrayList;
import java.util.List;

public final class MesaService implements IMesaService {

    private final ICrud<Mesa, String> mesaDAO;
    private final IReservaDAO reservaDAO;
    private final IAsignacionDAO asignacionDAO;
    private final DisponibilidadMesa disponibilidadMesa;

    public MesaService(ICrud<Mesa, String> mesaDAO,
            IReservaDAO reservaDAO,
            IAsignacionDAO asignacionDAO,
            DisponibilidadMesa disponibilidadMesa) {

        this.mesaDAO = mesaDAO;
        this.reservaDAO = reservaDAO;
        this.asignacionDAO = asignacionDAO;
        this.disponibilidadMesa = disponibilidadMesa;
    }

    private Mesa obtenerMesa(String idMesa) {

        idMesa = Validaciones.validarCodigo(idMesa, "MTA");

        Mesa mesa = mesaDAO.buscarPorId(idMesa);

        if (mesa == null) {
            throw new EntidadNoEncontradaException(
                    "Mesa no encontrada: " + idMesa);
        }

        return mesa;
    }

    private Mesa obtenerMesaActiva(String idMesa) {

        Mesa mesa = obtenerMesa(idMesa);

        if (!mesa.isActivo()) {
            throw new OperacionNoPermitidaException(
                    "La mesa está inactiva");
        }

        return mesa;
    }

    private MesaDTO convertirDTO(Mesa mesa) {

        return new MesaDTO(
                mesa.getId(),
                mesa.getCapacidad(),
                mesa.isActivo()
        );
    }

    private String generarId() {

        List<String> ids = new ArrayList<>();

        for (Mesa mesa : mesaDAO.listar()) {
            ids.add(mesa.getId());
        }

        return GeneradorCodigo.generarSiguienteId(ids, "MTA");
    }

    public String agregarMesa(int capacidad) {

        String id = generarId();

        Mesa mesa = new Mesa(id, capacidad);

        mesaDAO.agregar(mesa);

        return id;
    }

    public MesaDTO buscarPorId(String idMesa) {

        return convertirDTO(obtenerMesa(idMesa));
    }

    public List<MesaDTO> listarMesas() {

        List<MesaDTO> resultado = new ArrayList<>();

        for (Mesa mesa : mesaDAO.listar()) {
            resultado.add(convertirDTO(mesa));
        }

        return resultado;
    }

    public List<MesaDTO> listarMesasActivas() {

        List<MesaDTO> resultado = new ArrayList<>();

        for (Mesa mesa : mesaDAO.listar()) {

            if (mesa.isActivo()) {
                resultado.add(convertirDTO(mesa));
            }
        }

        return resultado;
    }

    public List<MesaDTO> listarMesasDisponibles() {

        List<MesaDTO> resultado = new ArrayList<>();

        for (Mesa mesa : mesaDAO.listar()) {

            if (disponibilidadMesa.lugaresDisponiblesParaReserva(mesa) > 0) {
                resultado.add(convertirDTO(mesa));
            }
        }

        return resultado;
    }

    public int consultarLugaresDisponibles(String idMesa) {

        Mesa mesa = obtenerMesaActiva(idMesa);

        return disponibilidadMesa.lugaresDisponiblesParaReserva(mesa);
    }

    public void actualizarCapacidad(String idMesa, int nuevaCapacidad) {

        Mesa mesa = obtenerMesaActiva(idMesa);

        Mesa.validarCapacidad(nuevaCapacidad);

        int ocupados =
                reservaDAO.contarReservasActivasPorMesa(
                        mesa.getId());

        if (nuevaCapacidad < ocupados) {
            throw new OperacionNoPermitidaException(
                    "No se puede reducir la capacidad por debajo "
                    + "de las reservas activas");
        }

        mesa.setCapacidad(nuevaCapacidad);

        mesaDAO.actualizar(mesa);
    }

    public void eliminarMesa(String idMesa) {

        Mesa mesa = obtenerMesaActiva(idMesa);

        List<Reserva> reservasActivas = new ArrayList<>();

        for (Reserva reserva :
                reservaDAO.listarPorMesa(mesa.getId())) {

            if (reserva.isActivo()) {
                reservasActivas.add(reserva);
            }
        }

        // Mesas que pueden recibir reservas.
        List<Mesa> mesasDestino = new ArrayList<>();

        // Lugares disponibles de cada mesa destino.
        // Cada posición corresponde a la misma posición
        // de la lista mesasDestino.
        List<Integer> lugaresDisponibles = new ArrayList<>();

        for (Mesa candidata : mesaDAO.listar()) {

            if (candidata.getId().equalsIgnoreCase(mesa.getId())) {
                continue;
            }

            int disponibles = disponibilidadMesa.lugaresDisponiblesParaReserva(candidata);

            if (disponibles > 0) {
                mesasDestino.add(candidata);
                lugaresDisponibles.add(disponibles);
            }
        }

        // Planificar todos los traslados antes de modificar
        // los archivos.
        List<String> planReasignacion = new ArrayList<>();

        for (Reserva reserva : reservasActivas) {

            boolean asignada = false;

            for (int i = 0; i < mesasDestino.size(); i++) {

                int disponibles = lugaresDisponibles.get(i);

                if (disponibles > 0) {

                    planReasignacion.add(
                            mesasDestino.get(i).getId());

                    lugaresDisponibles.set(i, disponibles - 1);

                    asignada = true;
                    break;
                }
            }

            if (!asignada) {
                throw new OperacionNoPermitidaException(
                        "No hay capacidad suficiente para "
                        + "reasignar todas las reservas. "
                        + "La mesa no fue dada de baja");
            }
        }

        List<Reserva> trasladadas = new ArrayList<>();
        for (int i = 0; i < reservasActivas.size(); i++) {
            Reserva original = reservasActivas.get(i);
            trasladadas.add(new Reserva(original.getId(),
                    original.getIdCliente(), planReasignacion.get(i)));
        }
        reservaDAO.actualizarTodas(trasladadas);

        Asignacion asignacionActiva =
                asignacionDAO.buscarAsignacionActivaPorMesa(mesa.getId());
        boolean asignacionDesactivada = false;

        try {
            if (asignacionActiva != null) {
                asignacionDAO.eliminar(asignacionActiva.getId());
                asignacionDesactivada = true;
            }
            mesaDAO.eliminar(mesa.getId());
        } catch (RuntimeException errorBaja) {
            if (asignacionDesactivada) {
                try {
                    asignacionDAO.reactivar(asignacionActiva.getId());
                } catch (RuntimeException errorReversion) {
                    errorBaja.addSuppressed(errorReversion);
                }
            }
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
            if (errorBaja.getSuppressed().length > 0) {
                throw new PersistenciaException(
                        "Falló la baja y no se pudo restaurar todo el estado",
                        errorBaja);
            }
            throw errorBaja;
        }
    }

    public void reactivarMesa(String idMesa) {

        Mesa mesa = obtenerMesa(idMesa);

        if (mesa.isActivo()) {
            throw new OperacionNoPermitidaException(
                    "La mesa ya está activa");
        }

        mesaDAO.reactivar(mesa.getId());
    }
}
