package gestionbar.DTOs;

public class ClienteMesa {

    private final PersonaDTO cliente;
    private final String idMesa;

    public ClienteMesa(PersonaDTO cliente, String idMesa) {
        this.cliente = cliente;
        this.idMesa = idMesa;
    }

    public PersonaDTO getCliente() {
        return cliente;
    }

    public String getIdMesa() {
        return idMesa;
    }
}
