package gestionbar.DTOs;

public final class AsignacionDTO {

    private final String id;
    private final String idMesero;
    private final String nombreMesero;
    private final String idMesa;
    private final boolean activo;

    public AsignacionDTO(String id, String idMesero, String nombreMesero,
            String idMesa, boolean activo) {
        this.id = id;
        this.idMesero = idMesero;
        this.nombreMesero = nombreMesero;
        this.idMesa = idMesa;
        this.activo = activo;
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

    public String getNombreMesero() {
        return nombreMesero;
    }

    public boolean isActivo() {
        return activo;
    }

}
