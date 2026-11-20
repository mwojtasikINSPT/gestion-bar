package gestionbar.views;

import gestionbar.utils.Consola;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class MenuPrincipalView {

    private final Map<Integer, OpcionMenu> opciones;
    private final Consola consola;

    public MenuPrincipalView(List<OpcionMenu> opciones, Consola consola) {
        Objects.requireNonNull(opciones, "Las opciones son obligatorias");
        this.consola = Objects.requireNonNull(consola, "La consola es obligatoria");
        this.opciones = new LinkedHashMap<>();
        for (OpcionMenu opcion : opciones) {
            if (this.opciones.put(opcion.getNumero(), opcion) != null) {
                throw new IllegalArgumentException(
                        "Número de opción duplicado: " + opcion.getNumero());
            }
        }
    }

    public void mostrarMenu() {

        int opcion;

        do {
            String[] textosConSalida = new String[opciones.size() + 1];
            int indice = 0;
            for (OpcionMenu item : opciones.values()) {
                textosConSalida[indice] = item.getNumero() + ". "
                        + item.getDescripcion();
                indice++;
            }
            textosConSalida[indice] = "0. Salir";

            consola.imprimirMenu(
                    "SISTEMA DE GESTIÓN DE BAR", textosConSalida);

            opcion = consola.leerEntero(
                    "Seleccione una opción: ");

            if (opcion == 0) {
                consola.imprimirMensaje(
                        "Gracias por utilizar el sistema. ¡Hasta pronto!");
            } else {
                OpcionMenu seleccionada = opciones.get(opcion);
                if (seleccionada == null) {
                    consola.imprimirError(
                            "Opción inválida. Intente nuevamente.");
                    consola.pausar();
                } else {
                    seleccionada.ejecutar();
                }
            }

        } while (opcion != 0);
    }
}

