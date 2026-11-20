package gestionbar.services;

import gestionbar.DTOs.ClienteMesa;
import gestionbar.DTOs.PersonaDTO;
import java.util.List;

public interface IConsultaService {
    List<ClienteMesa> consultarClientesConMesa();
    List<ClienteMesa> consultarClientesPorMesero(String idMesero);
    PersonaDTO consultarMeseroPorMesa(String idMesa);
    PersonaDTO consultarMesero(String idMesero);
}
