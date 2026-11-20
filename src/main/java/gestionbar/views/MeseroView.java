package gestionbar.views;

import gestionbar.DTOs.PersonaDTO;
import gestionbar.controllers.PersonaController;
import gestionbar.utils.Consola;
import java.util.List;

public final class MeseroView implements VistaMenu {
    private final PersonaController controller;
    private final Consola consola;

    public MeseroView(PersonaController controller, Consola consola) {
        this.controller = controller;
        this.consola = consola;
    }

    public void mostrarMenu() {
        int opcion;
        do {
            consola.imprimirMenu("GESTIÓN DE MESEROS",
                    "1. Agregar mesero", "2. Listar meseros",
                    "3. Buscar mesero", "4. Modificar mesero",
                    "5. Dar de baja mesero", "6. Reactivar mesero",
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
        consola.imprimirTitulo("AGREGAR MESERO");
        String nombre = consola.leerTexto("Nombre (0 para cancelar): ");
        if (consola.esCancelacion(nombre)) return;
        String apellido = consola.leerTexto("Apellido (0 para cancelar): ");
        if (consola.esCancelacion(apellido)) return;
        String id = controller.agregarMesero(nombre, apellido);
        consola.imprimirRegistroCreado("Mesero", id);
    }

    private void listar() {
        consola.imprimirTitulo("LISTADO DE MESEROS");
        List<PersonaDTO> personas = controller.listarMeseros();
        if (personas.isEmpty()) {
            consola.imprimirMensaje("No hay meseros registrados");
            return;
        }
        for (PersonaDTO persona : personas) imprimir(persona);
        consola.imprimirCantidad(personas.size());
    }

    private void buscar() {
        consola.imprimirTitulo("BUSCAR MESERO");
        String id = leerId();
        if (id == null) return;
        imprimir(controller.buscarMeseroPorId(id));
    }

    private void modificar() {
        consola.imprimirTitulo("MODIFICAR MESERO");
        String id = leerId();
        if (id == null) return;
        imprimir(controller.buscarMeseroPorId(id));
        String nombre = consola.leerTexto("Nuevo nombre (0 para cancelar): ");
        if (consola.esCancelacion(nombre)) return;
        String apellido = consola.leerTexto("Nuevo apellido (0 para cancelar): ");
        if (consola.esCancelacion(apellido)) return;
        if (!consola.confirmar("¿Confirma la modificación?")) return;
        controller.actualizarPersona(id, nombre, apellido);
        consola.imprimirMensaje("Mesero modificado correctamente");
    }

    private void eliminar() {
        consola.imprimirTitulo("BAJA DE MESERO");
        String id = leerId();
        if (id == null) return;
        imprimir(controller.buscarMeseroPorId(id));
        String idReemplazante = null;
        List<PersonaDTO> reemplazantes = controller.listarReemplazantes(id);
        if (!reemplazantes.isEmpty()) {
            consola.imprimirTitulo("MESEROS DISPONIBLES COMO REEMPLAZANTES");
            for (PersonaDTO persona : reemplazantes) imprimir(persona);
            idReemplazante = consola.leerTexto(
                    "ID del reemplazante (ejemplo: MSR0002, 0 para omitir): ");
            if (consola.esCancelacion(idReemplazante)) {
                idReemplazante = null;
            }
        } else {
            consola.imprimirMensaje("No hay otros meseros activos disponibles.");
            consola.imprimirMensaje("La baja solo será posible si el mesero "
                    + "no tiene asignaciones activas.");
        }
        if (!consola.confirmar("¿Confirma la baja del mesero?")) return;
        controller.eliminarMesero(id, idReemplazante);
        consola.imprimirMensaje("Mesero dado de baja correctamente");
    }

    private void reactivar() {
        consola.imprimirTitulo("REACTIVAR MESERO");
        String id = leerId();
        if (id == null) return;
        imprimir(controller.buscarMeseroPorId(id));
        if (!consola.confirmar("¿Confirma la reactivación?")) return;
        controller.reactivarPersona(id);
        consola.imprimirMensaje("Mesero reactivado correctamente");
    }

    private String leerId() {
        String id = consola.leerTexto(
                "ID del mesero (ejemplo: MSR0001, 0 para cancelar): ");
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
