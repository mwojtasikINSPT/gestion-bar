package gestionbar.DAOs;

import gestionbar.models.Persona;
import java.util.List;

public interface IPersonaDAO extends ICrud<Persona, String> {


    List<Persona> listarClientes();

    List<Persona> listarMeseros();
}
