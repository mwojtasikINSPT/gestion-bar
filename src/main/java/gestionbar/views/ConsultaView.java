package gestionbar.views;

import gestionbar.controllers.ConsultaController;
import gestionbar.DTOs.ClienteMesa;
import gestionbar.DTOs.PersonaDTO;
import gestionbar.utils.Consola;

import java.util.List;

public class ConsultaView implements VistaMenu {

    private final ConsultaController consultaController;
    private final Consola consola;

    public ConsultaView(ConsultaController consultaController, Consola consola) {
        this.consultaController = consultaController;
        this.consola = consola;
    }

    public void mostrarMenu() {

        int opcion;

        do {
            consola.imprimirMenu(
                    "CONSULTAS",
                    "1. Clientes con su mesa reservada",
                    "2. Clientes atendidos por un mesero",
                    "3. Mesero responsable de una mesa",
                    "0. Volver"
            );

            opcion = consola.leerEntero(
                    "Seleccione una opción: ");

            try {
                switch (opcion) {
                    case 1:
                        consultarClientesConMesa();
                        break;
                    case 2:
                        consultarClientesPorMesero();
                        break;
                    case 3:
                        consultarMeseroPorMesa();
                        break;
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

    // CONSULTA 1:
    // Clientes con su mesa reservada.

    private void consultarClientesConMesa() {

        consola.imprimirTitulo(
                "CLIENTES CON SU MESA RESERVADA");

        List<ClienteMesa> resultados =
                consultaController.consultarClientesConMesa();

        if (resultados.isEmpty()) {
            consola.imprimirMensaje(
                    "No hay clientes con reservas activas");
            return;
        }

        for (ClienteMesa resultado : resultados) {
            imprimirClienteMesa(resultado);
        }

        consola.imprimirCantidad(resultados.size());
    }

    // CONSULTA 2:
    // Clientes atendidos por un mesero.

    private void consultarClientesPorMesero() {

        consola.imprimirTitulo(
                "CLIENTES ATENDIDOS POR UN MESERO");

        String idMesero = consola.leerTexto(
                "ID del mesero (ejemplo: MSR0001, 0 para cancelar): ");

        if (consola.esCancelacion(idMesero)) {
            return;
        }

        PersonaDTO mesero =
                consultaController.consultarMesero(idMesero);

        consola.imprimirMensaje(
                "Mesero: " + mesero.getId()
                + " - " + mesero.getNombre()
                + " " + mesero.getApellido());

        List<ClienteMesa> resultados =
                consultaController.consultarClientesPorMesero(
                        idMesero);

        if (resultados.isEmpty()) {
            consola.imprimirMensaje(
                    "Este mesero no tiene clientes "
                    + "con reservas activas");
            return;
        }

        consola.imprimirTitulo(
                "CLIENTES ATENDIDOS");

        for (ClienteMesa resultado : resultados) {
            imprimirClienteMesa(resultado);
        }

        consola.imprimirCantidad(resultados.size());
    }

    // CONSULTA 3:
    // Mesero responsable de una mesa.

    private void consultarMeseroPorMesa() {

        consola.imprimirTitulo(
                "MESERO RESPONSABLE DE UNA MESA");

        String idMesa = consola.leerTexto(
                "ID de la mesa (ejemplo: MTA0001, 0 para cancelar): ");

        if (consola.esCancelacion(idMesa)) {
            return;
        }

        PersonaDTO mesero =
                consultaController.consultarMeseroPorMesa(
                        idMesa);

        if (mesero == null) {
            consola.imprimirMensaje(
                    "La mesa no tiene un mesero activo asignado");
            return;
        }

        consola.imprimirMensaje(
                "Mesa: " + idMesa.toUpperCase());

        consola.imprimirMensaje(
                "Mesero responsable: "
                + mesero.getNombre()
                + " " + mesero.getApellido());

        consola.imprimirMensaje(
                "ID del mesero: " + mesero.getId());
    }

    // MÉTODO AUXILIAR:
    // Mostrar cliente y mesa reservada.

    private void imprimirClienteMesa(ClienteMesa resultado) {

        PersonaDTO cliente = resultado.getCliente();

        consola.imprimirMensaje(
                "Cliente: " + cliente.getNombre()
                + " " + cliente.getApellido()
                + " | ID: " + cliente.getId()
                + " | Mesa: " + resultado.getIdMesa());
    }
}

