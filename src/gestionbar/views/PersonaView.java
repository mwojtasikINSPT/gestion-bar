package gestionbar.views;

import gestionbar.utils.Consola;

public final class PersonaView implements VistaMenu {
    private final ClienteView clienteView;
    private final MeseroView meseroView;
    private final Consola consola;

    public PersonaView(ClienteView clienteView,
            MeseroView meseroView, Consola consola) {
        this.clienteView = clienteView;
        this.meseroView = meseroView;
        this.consola = consola;
    }

    public void mostrarMenu() {
        int opcion;
        do {
            consola.imprimirMenu("GESTIÓN DE PERSONAS",
                    "1. Gestionar clientes",
                    "2. Gestionar meseros",
                    "0. Volver");
            opcion = consola.leerEntero("Seleccione una opción: ");
            switch (opcion) {
                case 1: clienteView.mostrarMenu(); break;
                case 2: meseroView.mostrarMenu(); break;
                case 0:
                    consola.imprimirMensaje("Volviendo al menú principal...");
                    break;
                default:
                    consola.imprimirError("Opción inválida");
                    break;
            }
        } while (opcion != 0);
    }
}
