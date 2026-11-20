package gestionbar.models;

import gestionbar.utils.Validaciones;

public final class Reserva {

    private final String id;
    private String idCliente;
    private String idMesa;
    private boolean activo;

    public Reserva(String id, String idCliente, String idMesa) {
        this.id = Validaciones.validarCodigo(id, "RES");
        this.idCliente = Validaciones.validarCodigo(idCliente, "CLI");
        this.idMesa = Validaciones.validarCodigo(idMesa, "MTA");
        this.activo = true;
    }

    public String getId() {
        return id;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public String getIdMesa() {
        return idMesa;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = Validaciones.validarCodigo(idCliente, "CLI");
    }

    public void setIdMesa(String idMesa) {
        this.idMesa = Validaciones.validarCodigo(idMesa, "MTA");
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
