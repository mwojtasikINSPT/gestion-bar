package gestionbar.DTOs;

public final class MesaDTO {

    private final String id;
    private final int capacidad;
    private final boolean activo;

    public MesaDTO(String id, int capacidad, boolean activo) {
        this.id = id;
        this.capacidad = capacidad;
        this.activo = activo;
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

}
