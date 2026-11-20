package gestionbar.DTOs;

public final class ReservaDTO {

    private final String id;
    private final String idCliente;
    private final String nombreCliente;
    private final String idMesa;
    private final boolean activo;

    public ReservaDTO(String id, String idCliente, String nombreCliente,
            String idMesa, boolean activo) {
        this.id = id;
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.idMesa = idMesa;
        this.activo = activo;
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

    public String getNombreCliente() {
        return nombreCliente;
    }

    public boolean isActivo() {
        return activo;
    }

}
