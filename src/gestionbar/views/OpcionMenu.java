package gestionbar.views;

import java.util.Objects;

/**
 * Opción inmutable del menú. Usa polimorfismo clásico mediante VistaMenu,
 * sin expresiones lambda ni referencias a métodos.
 */
public final class OpcionMenu {

    private final int numero;
    private final String descripcion;
    private final VistaMenu vista;

    public OpcionMenu(int numero, String descripcion, VistaMenu vista) {
        if (numero <= 0) {
            throw new IllegalArgumentException("El número debe ser positivo");
        }
        this.numero = numero;
        this.descripcion = Objects.requireNonNull(descripcion);
        this.vista = Objects.requireNonNull(vista);
    }

    public int getNumero() {
        return numero;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void ejecutar() {
        vista.mostrarMenu();
    }
}
