package gestionbar.controllers;

import gestionbar.services.IPersonaService;

import gestionbar.DTOs.PersonaDTO;

import java.util.List;

public final class PersonaController {

    private final IPersonaService personaService;

    public PersonaController(IPersonaService personaService) {
        this.personaService = personaService;
    }

    public String agregarCliente(String nombre, String apellido) {

        return personaService.agregarCliente(nombre, apellido);
    }

    public String agregarMesero(String nombre, String apellido) {

        return personaService.agregarMesero(nombre, apellido);
    }

    public PersonaDTO buscarClientePorId(String id) {
        return personaService.buscarClientePorId(id);
    }

    public PersonaDTO buscarMeseroPorId(String id) {
        return personaService.buscarMeseroPorId(id);
    }

    public List<PersonaDTO> listarClientes() {

        return personaService.listarClientes();
    }

    public List<PersonaDTO> listarMeseros() {

        return personaService.listarMeseros();
    }

    public List<PersonaDTO> listarMeserosActivos() {

        return personaService.listarMeserosActivos();
    }

    public List<PersonaDTO> listarReemplazantes(String idMesero) {

        return personaService.listarReemplazantes(idMesero);
    }

    public void actualizarPersona(String id,
            String nombre, String apellido) {

        personaService.actualizarPersona(id, nombre, apellido);
    }

    public boolean clienteTieneReservaActiva(String idCliente) {

        return personaService.clienteTieneReservaActiva(idCliente);
    }

    public void eliminarCliente(String idCliente) {

        personaService.eliminarCliente(idCliente);
    }

    public void eliminarMesero(String idMesero,
            String idReemplazante) {

        personaService.eliminarMesero(idMesero, idReemplazante);
    }

    public void reactivarPersona(String id) {

        personaService.reactivarPersona(id);
    }
}
