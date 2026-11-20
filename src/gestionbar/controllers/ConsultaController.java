package gestionbar.controllers;

import gestionbar.services.IConsultaService;
import gestionbar.DTOs.ClienteMesa;
import gestionbar.DTOs.PersonaDTO;

import java.util.List;

public final class ConsultaController {

    private final IConsultaService consultaService;

    public ConsultaController(IConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    // CONSULTA 1:
    // Clientes con su mesa reservada.

    public List<ClienteMesa> consultarClientesConMesa() {

        return consultaService.consultarClientesConMesa();
    }

    // CONSULTA 2:
    // Clientes atendidos por un mesero.

    public List<ClienteMesa> consultarClientesPorMesero(
            String idMesero) {

        return consultaService.consultarClientesPorMesero(idMesero);
    }

    // CONSULTA 3:
    // Mesero responsable de una mesa.

    public PersonaDTO consultarMeseroPorMesa(String idMesa) {

        return consultaService.consultarMeseroPorMesa(idMesa);
    }

    // CONSULTA AUXILIAR:
    // Obtener los datos del mesero consultado.

    public PersonaDTO consultarMesero(String idMesero) {

        return consultaService.consultarMesero(idMesero);
    }
}
