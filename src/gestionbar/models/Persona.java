package gestionbar.models;

import gestionbar.utils.Validaciones;

public final class Persona {

    private final String id;
    private final Rol rol;
    private String nombre;
    private String apellido;
    private boolean activo;

    public Persona(String id, Rol rol, String nombre, String apellido) {
        if (rol == null) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }
        this.rol = rol;
        String prefijo = rol == Rol.CLIENTE ? "CLI" : "MSR";
        this.id = Validaciones.validarCodigo(id, prefijo);
        this.nombre = Validaciones.validarTextoObligatorio(nombre, "nombre");
        this.apellido = Validaciones.validarTextoObligatorio(apellido, "apellido");
        this.activo = true;
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

    public void setNombre(String nombre) {
        this.nombre = Validaciones.validarTextoObligatorio(nombre, "nombre");
    }

    public void setApellido(String apellido) {
        this.apellido = Validaciones.validarTextoObligatorio(apellido, "apellido");
    }

    public void modificar(String nombre, String apellido) {
        String nombreValidado =
                Validaciones.validarTextoObligatorio(nombre, "nombre");
        String apellidoValidado =
                Validaciones.validarTextoObligatorio(apellido, "apellido");
        this.nombre = nombreValidado;
        this.apellido = apellidoValidado;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "Persona {"
                + "id=" + id
                + ", rol=" + rol
                + ", nombre=" + nombre
                + ", apellido=" + apellido
                + ", activo=" + activo
                + '}';
    }
}
