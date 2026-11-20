package gestionbar.views;

import gestionbar.controllers.ReservaController;
import gestionbar.DTOs.ReservaDTO;
import gestionbar.utils.Consola;

import java.util.List;

public class ReservaView implements VistaMenu {

    private final ReservaController reservaController;
    private final Consola consola;

    public ReservaView(ReservaController reservaController, Consola consola) {
        this.reservaController = reservaController;
        this.consola = consola;
    }

    public void mostrarMenu() {

        int opcion;

        do {
            consola.imprimirMenu(
                    "GESTIÓN DE RESERVAS",
                    "1. Agregar reserva",
                    "2. Listar reservas",
                    "3. Buscar reserva",
                    "4. Listar reservas por cliente",
                    "5. Listar reservas por mesa",
                    "6. Consultar lugares disponibles",
                    "7. Cambiar mesa de una reserva",
                    "8. Cambiar cliente de una reserva",
                    "9. Dar de baja reserva",
                    "10. Reactivar reserva",
                    "0. Volver"
            );

            opcion = consola.leerEntero(
                    "Seleccione una opción: ");

            try {
                switch (opcion) {
                    case 1: agregarReserva(); break;
                    case 2: listarReservas(); break;
                    case 3: buscarReserva(); break;
                    case 4: listarPorCliente(); break;
                    case 5: listarPorMesa(); break;
                    case 6: consultarLugaresDisponibles(); break;
                    case 7: cambiarMesa(); break;
                    case 8: cambiarCliente(); break;
                    case 9: eliminarReserva(); break;
                    case 10: reactivarReserva(); break;
                    case 0:
                        consola.imprimirMensaje(
                                "Volviendo al menú principal...");
                        break;
                    default:
                        consola.imprimirError("Opción inválida");
                        break;
                }

            } catch (RuntimeException e) {
                consola.imprimirError(e.getMessage());
            }

            if (opcion != 0) {
                consola.pausar();
            }

        } while (opcion != 0);
    }

    private void agregarReserva() {

        consola.imprimirTitulo("AGREGAR RESERVA");

        String idCliente = consola.leerTexto(
                "ID del cliente (ejemplo: CLI0001, 0 para cancelar): ");

        if (consola.esCancelacion(idCliente)) {
            return;
        }

        String idMesa = consola.leerTexto(
                "ID de la mesa (ejemplo: MTA0001, 0 para cancelar): ");

        if (consola.esCancelacion(idMesa)) {
            return;
        }

        consola.imprimirMensaje(
                "Resumen: cliente " + idCliente + " | mesa " + idMesa);

        if (!consola.confirmar("¿Confirma la nueva reserva?")) {
            return;
        }

        String id = reservaController.agregarReserva(
                idCliente, idMesa);

        consola.imprimirRegistroCreado("Reserva", id);
    }

    private void listarReservas() {

        consola.imprimirTitulo("LISTADO DE RESERVAS");

        List<ReservaDTO> reservas =
                reservaController.listarReservas();

        imprimirListado(reservas);
    }

    private void buscarReserva() {

        consola.imprimirTitulo("BUSCAR RESERVA");

        String id = consola.leerTexto(
                "ID de la reserva (ejemplo: RES0001, 0 para cancelar): ");

        if (consola.esCancelacion(id)) {
            return;
        }

        ReservaDTO reserva = reservaController.buscarPorId(id);

        if (reserva == null) {
            consola.imprimirError(
                    "No se encontró la reserva");
            return;
        }

        imprimirReserva(reserva);
    }

    private void listarPorCliente() {

        consola.imprimirTitulo(
                "RESERVAS POR CLIENTE");

        String idCliente = consola.leerTexto(
                "ID del cliente (ejemplo: CLI0001, 0 para cancelar): ");

        if (consola.esCancelacion(idCliente)) {
            return;
        }

        List<ReservaDTO> reservas =
                reservaController.listarPorCliente(idCliente);

        imprimirListado(reservas);
    }

    private void listarPorMesa() {

        consola.imprimirTitulo(
                "RESERVAS POR MESA");

        String idMesa = consola.leerTexto(
                "ID de la mesa (ejemplo: MTA0001, 0 para cancelar): ");

        if (consola.esCancelacion(idMesa)) {
            return;
        }

        List<ReservaDTO> reservas =
                reservaController.listarPorMesa(idMesa);

        imprimirListado(reservas);
    }

    private void consultarLugaresDisponibles() {

        consola.imprimirTitulo(
                "CONSULTAR LUGARES DISPONIBLES");

        String idMesa = consola.leerTexto(
                "ID de la mesa (ejemplo: MTA0001, 0 para cancelar): ");

        if (consola.esCancelacion(idMesa)) {
            return;
        }

        int disponibles =
                reservaController.consultarLugaresDisponibles(
                        idMesa);

        consola.imprimirMensaje(
                "Lugares disponibles: " + disponibles);
    }

    private void cambiarMesa() {

        consola.imprimirTitulo(
                "CAMBIAR MESA DE UNA RESERVA");

        String idReserva = consola.leerTexto(
                "ID de la reserva (ejemplo: RES0001, 0 para cancelar): ");

        if (consola.esCancelacion(idReserva)) {
            return;
        }

        ReservaDTO reserva =
                reservaController.buscarPorId(idReserva);

        if (reserva == null) {
            consola.imprimirError(
                    "No se encontró la reserva");
            return;
        }

        imprimirReserva(reserva);

        String idNuevaMesa = consola.leerTexto(
                "ID de la nueva mesa (ejemplo: MTA0002, 0 para cancelar): ");

        if (consola.esCancelacion(idNuevaMesa)) {
            return;
        }

        if (!consola.confirmar(
                "¿Confirma el cambio de mesa?")) {
            return;
        }

        reservaController.cambiarMesa(
                idReserva, idNuevaMesa);

        consola.imprimirMensaje(
                "Mesa de la reserva actualizada correctamente");
    }

    private void cambiarCliente() {

        consola.imprimirTitulo(
                "CAMBIAR CLIENTE DE UNA RESERVA");

        String idReserva = consola.leerTexto(
                "ID de la reserva (ejemplo: RES0001, 0 para cancelar): ");

        if (consola.esCancelacion(idReserva)) {
            return;
        }

        ReservaDTO reserva =
                reservaController.buscarPorId(idReserva);

        if (reserva == null) {
            consola.imprimirError(
                    "No se encontró la reserva");
            return;
        }

        imprimirReserva(reserva);

        String idNuevoCliente = consola.leerTexto(
                "ID del nuevo cliente (ejemplo: CLI0002, 0 para cancelar): ");

        if (consola.esCancelacion(idNuevoCliente)) {
            return;
        }

        if (!consola.confirmar(
                "¿Confirma el cambio de cliente?")) {
            return;
        }

        reservaController.cambiarCliente(
                idReserva, idNuevoCliente);

        consola.imprimirMensaje(
                "Cliente de la reserva actualizado correctamente");
    }

    private void eliminarReserva() {

        consola.imprimirTitulo("DAR DE BAJA RESERVA");

        String idReserva = consola.leerTexto(
                "ID de la reserva (ejemplo: RES0001, 0 para cancelar): ");

        if (consola.esCancelacion(idReserva)) {
            return;
        }

        ReservaDTO reserva =
                reservaController.buscarPorId(idReserva);

        if (reserva == null) {
            consola.imprimirError(
                    "No se encontró la reserva");
            return;
        }

        imprimirReserva(reserva);

        if (!consola.confirmar(
                "¿Confirma la baja de la reserva?")) {
            return;
        }

        reservaController.eliminarReserva(idReserva);

        consola.imprimirMensaje(
                "Reserva dada de baja correctamente");
    }

    private void reactivarReserva() {

        consola.imprimirTitulo("REACTIVAR RESERVA");

        String idReserva = consola.leerTexto(
                "ID de la reserva (ejemplo: RES0001, 0 para cancelar): ");

        if (consola.esCancelacion(idReserva)) {
            return;
        }

        ReservaDTO reserva =
                reservaController.buscarPorId(idReserva);

        if (reserva == null) {
            consola.imprimirError(
                    "No se encontró la reserva");
            return;
        }

        imprimirReserva(reserva);

        if (!consola.confirmar(
                "¿Confirma la reactivación de la reserva?")) {
            return;
        }

        reservaController.reactivarReserva(idReserva);

        consola.imprimirMensaje(
                "Reserva reactivada correctamente");
    }

    private void imprimirListado(List<ReservaDTO> reservas) {

        if (reservas.isEmpty()) {
            consola.imprimirMensaje(
                    "No se encontraron reservas");
            return;
        }

        for (ReservaDTO reserva : reservas) {
            imprimirReserva(reserva);
        }

        consola.imprimirCantidad(reservas.size());
    }

    private void imprimirReserva(ReservaDTO reserva) {

        consola.imprimirMensaje(
                "ID: " + reserva.getId()
                + " | Cliente: " + reserva.getIdCliente()
                + " - " + reserva.getNombreCliente()
                + " | Mesa: " + reserva.getIdMesa()
                + " | Estado: "
                + (reserva.isActivo() ? "ACTIVA" : "INACTIVA")
        );
    }
}

