package gestionbar.views;

import gestionbar.controllers.AsignacionController;
import gestionbar.DTOs.AsignacionDTO;
import gestionbar.utils.Consola;

import java.util.List;

public class AsignacionView implements VistaMenu {

    private final AsignacionController asignacionController;
    private final Consola consola;

    public AsignacionView(AsignacionController asignacionController, Consola consola) {
        this.asignacionController = asignacionController;
        this.consola = consola;
    }

    public void mostrarMenu() {

        int opcion;

        do {
            consola.imprimirMenu(
                    "GESTIÓN DE ASIGNACIONES",
                    "1. Agregar asignación",
                    "2. Listar asignaciones",
                    "3. Buscar asignación",
                    "4. Listar asignaciones por mesero",
                    "5. Listar asignaciones por mesa",
                    "6. Cambiar mesero de una asignación",
                    "7. Dar de baja asignación",
                    "8. Reactivar asignación",
                    "0. Volver"
            );

            opcion = consola.leerEntero(
                    "Seleccione una opción: ");

            try {
                switch (opcion) {
                    case 1: agregarAsignacion(); break;
                    case 2: listarAsignaciones(); break;
                    case 3: buscarAsignacion(); break;
                    case 4: listarPorMesero(); break;
                    case 5: listarPorMesa(); break;
                    case 6: cambiarMesero(); break;
                    case 7: eliminarAsignacion(); break;
                    case 8: reactivarAsignacion(); break;
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

    private void agregarAsignacion() {

        consola.imprimirTitulo("AGREGAR ASIGNACIÓN");

        String idMesero = consola.leerTexto(
                "ID del mesero (ejemplo: MSR0001, 0 para cancelar): ");

        if (consola.esCancelacion(idMesero)) {
            return;
        }

        String idMesa = consola.leerTexto(
                "ID de la mesa (ejemplo: MTA0001, 0 para cancelar): ");

        if (consola.esCancelacion(idMesa)) {
            return;
        }

        consola.imprimirMensaje(
                "Resumen: mesero " + idMesero + " | mesa " + idMesa);

        if (!consola.confirmar("¿Confirma la nueva asignación?")) {
            return;
        }

        String id = asignacionController.agregarAsignacion(
                idMesero, idMesa);

        consola.imprimirRegistroCreado("Asignación", id);
    }

    private void listarAsignaciones() {

        consola.imprimirTitulo("LISTADO DE ASIGNACIONES");

        List<AsignacionDTO> asignaciones =
                asignacionController.listarAsignaciones();

        imprimirListado(asignaciones);
    }

    private void buscarAsignacion() {

        consola.imprimirTitulo("BUSCAR ASIGNACIÓN");

        String id = consola.leerTexto(
                "ID de la asignación (ejemplo: ASG0001, 0 para cancelar): ");

        if (consola.esCancelacion(id)) {
            return;
        }

        AsignacionDTO asignacion =
                asignacionController.buscarPorId(id);

        if (asignacion == null) {
            consola.imprimirError(
                    "No se encontró la asignación");
            return;
        }

        imprimirAsignacion(asignacion);
    }

    private void listarPorMesero() {

        consola.imprimirTitulo(
                "ASIGNACIONES POR MESERO");

        String idMesero = consola.leerTexto(
                "ID del mesero (ejemplo: MSR0001, 0 para cancelar): ");

        if (consola.esCancelacion(idMesero)) {
            return;
        }

        List<AsignacionDTO> asignaciones =
                asignacionController.listarPorMesero(idMesero);

        imprimirListado(asignaciones);
    }

    private void listarPorMesa() {

        consola.imprimirTitulo(
                "ASIGNACIONES POR MESA");

        String idMesa = consola.leerTexto(
                "ID de la mesa (ejemplo: MTA0001, 0 para cancelar): ");

        if (consola.esCancelacion(idMesa)) {
            return;
        }

        List<AsignacionDTO> asignaciones =
                asignacionController.listarPorMesa(idMesa);

        imprimirListado(asignaciones);
    }

    private void cambiarMesero() {

        consola.imprimirTitulo(
                "CAMBIAR MESERO DE UNA ASIGNACIÓN");

        String idAsignacion = consola.leerTexto(
                "ID de la asignación (ejemplo: ASG0001, 0 para cancelar): ");

        if (consola.esCancelacion(idAsignacion)) {
            return;
        }

        AsignacionDTO asignacion =
                asignacionController.buscarPorId(idAsignacion);

        if (asignacion == null) {
            consola.imprimirError(
                    "No se encontró la asignación");
            return;
        }

        imprimirAsignacion(asignacion);

        String idNuevoMesero = consola.leerTexto(
                "ID del nuevo mesero (ejemplo: MSR0002, 0 para cancelar): ");

        if (consola.esCancelacion(idNuevoMesero)) {
            return;
        }

        if (!consola.confirmar(
                "¿Confirma el cambio de mesero?")) {
            return;
        }

        asignacionController.cambiarMesero(
                idAsignacion, idNuevoMesero);

        consola.imprimirMensaje(
                "Mesero de la asignación actualizado correctamente");
    }

    private void eliminarAsignacion() {

        consola.imprimirTitulo(
                "DAR DE BAJA ASIGNACIÓN");

        String idAsignacion = consola.leerTexto(
                "ID de la asignación (ejemplo: ASG0001, 0 para cancelar): ");

        if (consola.esCancelacion(idAsignacion)) {
            return;
        }

        AsignacionDTO asignacion =
                asignacionController.buscarPorId(idAsignacion);

        if (asignacion == null) {
            consola.imprimirError(
                    "No se encontró la asignación");
            return;
        }

        imprimirAsignacion(asignacion);

        consola.imprimirMensaje(
                "ATENCIÓN: No se puede dar de baja una asignación "
                + "si su mesa tiene reservas activas.");

        if (!consola.confirmar(
                "¿Confirma la baja de la asignación?")) {
            return;
        }

        asignacionController.eliminarAsignacion(idAsignacion);

        consola.imprimirMensaje(
                "Asignación dada de baja correctamente");
    }

    private void reactivarAsignacion() {

        consola.imprimirTitulo(
                "REACTIVAR ASIGNACIÓN");

        String idAsignacion = consola.leerTexto(
                "ID de la asignación (ejemplo: ASG0001, 0 para cancelar): ");

        if (consola.esCancelacion(idAsignacion)) {
            return;
        }

        AsignacionDTO asignacion =
                asignacionController.buscarPorId(idAsignacion);

        if (asignacion == null) {
            consola.imprimirError(
                    "No se encontró la asignación");
            return;
        }

        imprimirAsignacion(asignacion);

        if (!consola.confirmar(
                "¿Confirma la reactivación?")) {
            return;
        }

        asignacionController.reactivarAsignacion(idAsignacion);

        consola.imprimirMensaje(
                "Asignación reactivada correctamente");
    }

    private void imprimirListado(
            List<AsignacionDTO> asignaciones) {

        if (asignaciones.isEmpty()) {
            consola.imprimirMensaje(
                    "No se encontraron asignaciones");
            return;
        }

        for (AsignacionDTO asignacion : asignaciones) {
            imprimirAsignacion(asignacion);
        }

        consola.imprimirCantidad(asignaciones.size());
    }

    private void imprimirAsignacion(AsignacionDTO asignacion) {

        consola.imprimirMensaje(
                "ID: " + asignacion.getId()
                + " | Mesero: " + asignacion.getIdMesero()
                + " - " + asignacion.getNombreMesero()
                + " | Mesa: " + asignacion.getIdMesa()
                + " | Estado: "
                + (asignacion.isActivo() ? "ACTIVA" : "INACTIVA")
        );
    }
}

