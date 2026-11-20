package gestionbar.models;

import gestionbar.utils.Validaciones;

public final class Mesa {

    private final String id;
    private int capacidad;
    private boolean activo;

    public Mesa(String id, int capacidad) {
        this.id = Validaciones.validarCodigo(id, "MTA");
        this.capacidad = validarCapacidad(capacidad);
        this.activo = true;
    }

    public String getId() {
        return id;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = validarCapacidad(capacidad);
    }

    public static int validarCapacidad(int capacidad) {
        if (capacidad <= 0) {
            throw new IllegalArgumentException(
                    "La capacidad debe ser mayor que cero");
        }
        return capacidad;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "Mesa {" + "id=" + id
                + ", capacidad=" + capacidad
                + ", activo=" + activo + '}';
    }
}
