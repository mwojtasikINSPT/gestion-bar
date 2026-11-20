package gestionbar.views;

import gestionbar.DTOs.PersonaDTO;
import gestionbar.controllers.PersonaController;
import gestionbar.utils.Consola;
import java.util.List;

public final class ClienteView implements VistaMenu {
    private final PersonaController controller;
    private final Consola consola;

    public ClienteView(PersonaController controller, Consola consola) {
        this.controller = controller;
        this.consola = consola;
    }

    public void mostrarMenu() {
        int opcion;
        do {
            consola.imprimirMenu("GESTIÓN DE CLIENTES",
                    "1. Agregar cliente", "2. Listar clientes",
                    "3. Buscar cliente", "4. Modificar cliente",
                    "5. Dar de baja cliente", "6. Reactivar cliente",
                    "0. Volver");
            opcion = consola.leerEntero("Seleccione una opción: ");
            try {
                switch (opcion) {
                    case 1: agregar(); break;
                    case 2: listar(); break;
                    case 3: buscar(); break;
                    case 4: modificar(); break;
                    case 5: eliminar(); break;
                    case 6: reactivar(); break;
                    case 0: consola.imprimirMensaje("Volviendo..."); break;
                    default: consola.imprimirError("Opción inválida"); break;
                }
            } catch (RuntimeException e) {
                consola.imprimirError(e.getMessage());
            }
            if (opcion != 0) {
                consola.pausar();
            }
        } while (opcion != 0);
    }

    private void agregar() {
        consola.imprimirTitulo("AGREGAR CLIENTE");
        String nombre = consola.leerTexto("Nombre (0 para cancelar): ");
        if (consola.esCancelacion(nombre)) return;
        String apellido = consola.leerTexto("Apellido (0 para cancelar): ");
        if (consola.esCancelacion(apellido)) return;
        String id = controller.agregarCliente(nombre, apellido);
        consola.imprimirRegistroCreado("Cliente", id);
    }

    private void listar() {
        consola.imprimirTitulo("LISTADO DE CLIENTES");
        List<PersonaDTO> personas = controller.listarClientes();
        if (personas.isEmpty()) {
            consola.imprimirMensaje("No hay clientes registrados");
            return;
        }
        for (PersonaDTO persona : personas) imprimir(persona);
        consola.imprimirCantidad(personas.size());
    }

    private void buscar() {
        consola.imprimirTitulo("BUSCAR CLIENTE");
        String id = leerId();
        if (id == null) return;
        imprimir(controller.buscarClientePorId(id));
    }

    private void modificar() {
        consola.imprimirTitulo("MODIFICAR CLIENTE");
        String id = leerId();
        if (id == null) return;
        imprimir(controller.buscarClientePorId(id));
        String nombre = consola.leerTexto("Nuevo nombre (0 para cancelar): ");
        if (consola.esCancelacion(nombre)) return;
        String apellido = consola.leerTexto("Nuevo apellido (0 para cancelar): ");
        if (consola.esCancelacion(apellido)) return;
        if (!consola.confirmar("¿Confirma la modificación?")) return;
        controller.actualizarPersona(id, nombre, apellido);
        consola.imprimirMensaje("Cliente modificado correctamente");
    }

    private void eliminar() {
        consola.imprimirTitulo("BAJA DE CLIENTE");
        String id = leerId();
        if (id == null) return;
        imprimir(controller.buscarClientePorId(id));
        if (controller.clienteTieneReservaActiva(id)) {
            consola.imprimirMensaje(
                    "ATENCIÓN: su reserva activa será cancelada.");
        }
        if (!consola.confirmar("¿Confirma la baja del cliente?")) return;
        controller.eliminarCliente(id);
        consola.imprimirMensaje("Cliente dado de baja correctamente");
    }

    private void reactivar() {
        consola.imprimirTitulo("REACTIVAR CLIENTE");
        String id = leerId();
        if (id == null) return;
        imprimir(controller.buscarClientePorId(id));
        if (!consola.confirmar("¿Confirma la reactivación?")) return;
        controller.reactivarPersona(id);
        consola.imprimirMensaje("Cliente reactivado correctamente");
    }

    private String leerId() {
        String id = consola.leerTexto(
                "ID del cliente (ejemplo: CLI0001, 0 para cancelar): ");
        return consola.esCancelacion(id) ? null : id;
    }

    private void imprimir(PersonaDTO persona) {
        consola.imprimirMensaje("ID: " + persona.getId()
                + " | Nombre: " + persona.getNombre() + " "
                + persona.getApellido() + " | Rol: " + persona.getRol()
                + " | Estado: "
                + (persona.isActivo() ? "ACTIVO" : "INACTIVO"));
    }
}
