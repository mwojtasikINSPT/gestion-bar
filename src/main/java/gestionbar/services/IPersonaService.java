package gestionbar.services;

import gestionbar.DTOs.PersonaDTO;
import java.util.List;

public interface IPersonaService {
    String agregarCliente(String nombre, String apellido);
    String agregarMesero(String nombre, String apellido);
    PersonaDTO buscarClientePorId(String id);
    PersonaDTO buscarMeseroPorId(String id);
    List<PersonaDTO> listarClientes();
    List<PersonaDTO> listarMeseros();
    List<PersonaDTO> listarMeserosActivos();
    List<PersonaDTO> listarReemplazantes(String idMesero);
    void actualizarPersona(String id, String nombre, String apellido);
    boolean clienteTieneReservaActiva(String idCliente);
    void eliminarCliente(String idCliente);
    void eliminarMesero(String idMesero, String idReemplazante);
    void reactivarPersona(String id);
}
