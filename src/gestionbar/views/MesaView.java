package gestionbar.views;

import gestionbar.controllers.MesaController;
import gestionbar.DTOs.MesaDTO;
import gestionbar.utils.Consola;

import java.util.List;

public class MesaView implements VistaMenu {

    private final MesaController mesaController;
    private final Consola consola;

    public MesaView(MesaController mesaController, Consola consola) {
        this.mesaController = mesaController;
        this.consola = consola;
    }

    public void mostrarMenu() {

        int opcion;

        do {
            consola.imprimirMenu(
                    "GESTIÓN DE MESAS",
                    "1. Agregar mesa",
                    "2. Listar mesas",
                    "3. Buscar mesa",
                    "4. Modificar capacidad",
                    "5. Consultar lugares disponibles",
                    "6. Listar mesas disponibles",
                    "7. Dar de baja mesa",
                    "8. Reactivar mesa",
                    "0. Volver"
            );

            opcion = consola.leerEntero(
                    "Seleccione una opción: ");

            try {
                switch (opcion) {
                    case 1: agregarMesa(); break;
                    case 2: listarMesas(); break;
                    case 3: buscarMesa(); break;
                    case 4: modificarCapacidad(); break;
                    case 5: consultarLugaresDisponibles(); break;
                    case 6: listarMesasDisponibles(); break;
                    case 7: eliminarMesa(); break;
                    case 8: reactivarMesa(); break;
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

    private void agregarMesa() {

        consola.imprimirTitulo("AGREGAR MESA");

        int capacidad = consola.leerEntero(
                "Capacidad de la mesa (0 para cancelar): ");

        if (capacidad == 0) {
            return;
        }

        String id = mesaController.agregarMesa(capacidad);

        consola.imprimirRegistroCreado("Mesa", id);
    }

    private void listarMesas() {

        consola.imprimirTitulo("LISTADO DE MESAS");

        List<MesaDTO> mesas = mesaController.listarMesas();

        if (mesas.isEmpty()) {
            consola.imprimirMensaje(
                    "No hay mesas registradas");
            return;
        }

        for (MesaDTO mesa : mesas) {
            imprimirMesa(mesa);
        }

        consola.imprimirCantidad(mesas.size());
    }

    private void buscarMesa() {

        consola.imprimirTitulo("BUSCAR MESA");

        String id = consola.leerTexto(
                "ID de la mesa (ejemplo: MTA0001, 0 para cancelar): ");

        if (consola.esCancelacion(id)) {
            return;
        }

        MesaDTO mesa = mesaController.buscarPorId(id);

        if (mesa == null) {
            consola.imprimirError(
                    "No se encontró la mesa");
            return;
        }

        imprimirMesa(mesa);
    }

    private void modificarCapacidad() {

        consola.imprimirTitulo("MODIFICAR CAPACIDAD");

        String id = consola.leerTexto(
                "ID de la mesa (ejemplo: MTA0001, 0 para cancelar): ");

        if (consola.esCancelacion(id)) {
            return;
        }

        MesaDTO mesa = mesaController.buscarPorId(id);

        if (mesa == null) {
            consola.imprimirError(
                    "No se encontró la mesa");
            return;
        }

        imprimirMesa(mesa);

        int nuevaCapacidad = consola.leerEntero(
                "Nueva capacidad (0 para cancelar): ");

        if (nuevaCapacidad == 0) {
            return;
        }

        if (!consola.confirmar(
                "¿Confirma el cambio de capacidad?")) {
            return;
        }

        mesaController.actualizarCapacidad(
                id, nuevaCapacidad);

        consola.imprimirMensaje(
                "Capacidad actualizada correctamente");
    }

    private void consultarLugaresDisponibles() {

        consola.imprimirTitulo(
                "CONSULTAR LUGARES DISPONIBLES");

        String id = consola.leerTexto(
                "ID de la mesa (ejemplo: MTA0001, 0 para cancelar): ");

        if (consola.esCancelacion(id)) {
            return;
        }

        int disponibles =
                mesaController.consultarLugaresDisponibles(id);

        consola.imprimirMensaje(
                "Lugares disponibles: " + disponibles);
    }

    private void listarMesasDisponibles() {

        consola.imprimirTitulo(
                "MESAS CON LUGARES DISPONIBLES");

        List<MesaDTO> mesas =
                mesaController.listarMesasDisponibles();

        if (mesas.isEmpty()) {
            consola.imprimirMensaje(
                    "No hay mesas disponibles");
            return;
        }

        for (MesaDTO mesa : mesas) {

            imprimirMesa(mesa);

            int disponibles =
                    mesaController.consultarLugaresDisponibles(
                            mesa.getId());

            consola.imprimirMensaje(
                    "Lugares disponibles: " + disponibles);
        }

        consola.imprimirCantidad(mesas.size());
    }

    private void eliminarMesa() {

        consola.imprimirTitulo("DAR DE BAJA MESA");

        String id = consola.leerTexto(
                "ID de la mesa (ejemplo: MTA0001, 0 para cancelar): ");

        if (consola.esCancelacion(id)) {
            return;
        }

        MesaDTO mesa = mesaController.buscarPorId(id);

        if (mesa == null) {
            consola.imprimirError(
                    "No se encontró la mesa");
            return;
        }

        imprimirMesa(mesa);

        consola.imprimirMensaje(
                "ATENCIÓN: Si la mesa tiene reservas activas, "
                + "el sistema intentará trasladarlas "
                + "automáticamente a otras mesas.");

        consola.imprimirMensaje(
                "Si no hay capacidad suficiente, "
                + "la baja será rechazada.");

        if (!consola.confirmar(
                "¿Confirma la baja de la mesa?")) {
            return;
        }

        mesaController.eliminarMesa(id);

        consola.imprimirMensaje(
                "Mesa dada de baja correctamente");
    }

    private void reactivarMesa() {

        consola.imprimirTitulo("REACTIVAR MESA");

        String id = consola.leerTexto(
                "ID de la mesa (ejemplo: MTA0001, 0 para cancelar): ");

        if (consola.esCancelacion(id)) {
            return;
        }

        MesaDTO mesa = mesaController.buscarPorId(id);

        if (mesa == null) {
            consola.imprimirError(
                    "No se encontró la mesa");
            return;
        }

        imprimirMesa(mesa);

        if (!consola.confirmar(
                "¿Confirma la reactivación?")) {
            return;
        }

        mesaController.reactivarMesa(id);

        consola.imprimirMensaje(
                "Mesa reactivada correctamente");
    }

    private void imprimirMesa(MesaDTO mesa) {

        consola.imprimirMensaje(
                "ID: " + mesa.getId()
                + " | Capacidad: " + mesa.getCapacidad()
                + " | Estado: "
                + (mesa.isActivo() ? "ACTIVA" : "INACTIVA")
        );
    }
}

