package gestionbar.utils;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Objects;
import java.util.Scanner;

/**
 * Administra la entrada y salida de la aplicación de consola.
 * Recibe los flujos para poder sustituir teclado y pantalla en pruebas.
 */
public final class Consola {

    private final Scanner entrada;
    private final PrintStream salida;
    private final PrintStream salidaError;
    private boolean cancelacionPendiente;

    public Consola(InputStream entrada, PrintStream salida,
            PrintStream salidaError) {
        this.entrada = new Scanner(Objects.requireNonNull(entrada));
        this.salida = Objects.requireNonNull(salida);
        this.salidaError = Objects.requireNonNull(salidaError);
    }

    public void imprimirMenu(String titulo, String... opciones) {
        imprimirTitulo(titulo);
        for (String opcion : opciones) {
            salida.println(opcion);
        }
        salida.println();
    }

    public void imprimirMensaje(String mensaje) {
        salida.println(mensaje);
    }

    public void imprimirError(String mensaje) {
        salidaError.println("ERROR: " + mensaje);
    }

    public void imprimirTitulo(String titulo) {
        salida.println();
        salida.println("========== " + titulo + " ==========");
    }

    public String leerTexto(String mensaje) {
        salida.print(mensaje + ": ");
        return entrada.nextLine().trim();
    }

    public boolean esCancelacion(String texto) {
        boolean cancelacion = texto != null && texto.trim().equals("0");
        if (cancelacion) {
            cancelacionPendiente = true;
        }
        return cancelacion;
    }

    public void imprimirCantidad(int cantidad) {
        imprimirMensaje("Cantidad de resultados: " + cantidad);
    }

    public int leerEntero(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje);
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                imprimirError("Debe ingresar un número entero válido.");
            }
        }
    }

    public boolean confirmar(String mensaje) {
        while (true) {
            String respuesta = leerTexto(mensaje + " (S/N)");
            if (respuesta.equalsIgnoreCase("S")) {
                return true;
            }
            if (respuesta.equalsIgnoreCase("N")) {
                return false;
            }
            imprimirError("Ingrese S o N.");
        }
    }

    public void pausar() {
        if (cancelacionPendiente) {
            cancelacionPendiente = false;
            return;
        }
        leerTexto("Presioná ENTER para continuar...");
    }

    public void imprimirRegistroCreado(String tipo, String id) {
        imprimirMensaje(tipo + " creada correctamente. ID asignado: " + id);
    }
}
