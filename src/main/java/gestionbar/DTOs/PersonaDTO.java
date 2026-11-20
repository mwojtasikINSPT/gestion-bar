package gestionbar.DTOs;

import gestionbar.models.Rol;

/**
 * Representa los datos de una persona expuestos a la presentación.
 * El rol distingue clientes y meseros sin duplicar estructuras.
 */
public final class PersonaDTO {

    private final String id;
    private final Rol rol;
    private final String nombre;
    private final String apellido;
    private final boolean activo;

    public PersonaDTO(String id, Rol rol, String nombre,
            String apellido, boolean activo) {
        this.id = id;
        this.rol = rol;
        this.nombre = nombre;
        this.apellido = apellido;
        this.activo = activo;
    }

    public String getId() {
        return id;
    }

    public Rol getRol() {
        return rol;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public boolean isActivo() {
        return activo;
    }
}
