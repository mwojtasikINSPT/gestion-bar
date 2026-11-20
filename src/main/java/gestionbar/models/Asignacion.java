package gestionbar.models;

import gestionbar.utils.Validaciones;

public final class Asignacion {

    private final String id;
    private String idMesero;
    private String idMesa;
    private boolean activo;

    public Asignacion(String id, String idMesero, String idMesa) {
        this.id = Validaciones.validarCodigo(id, "ASG");
        this.idMesero = Validaciones.validarCodigo(idMesero, "MSR");
        this.idMesa = Validaciones.validarCodigo(idMesa, "MTA");
        this.activo = true;
    }

    public String getId() {
        return id;
    }

    public String getIdMesero() {
        return idMesero;
    }

    public String getIdMesa() {
        return idMesa;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setIdMesero(String idMesero) {
        this.idMesero = Validaciones.validarCodigo(idMesero, "MSR");
    }

    public void setIdMesa(String idMesa) {
        this.idMesa = Validaciones.validarCodigo(idMesa, "MTA");
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
