package gestionbar.DAOs;
import gestionbar.models.*;
import gestionbar.exceptions.*;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
class PersonaDAOTest {
 @TempDir Path temp;
 private PersonaDAO nuevo(){return new PersonaDAO(temp.resolve("clientes.txt"),temp.resolve("meseros.txt"));}
 @Test void crudClienteYPersistencia(){ PersonaDAO dao=nuevo(); dao.agregar(new Persona("CLI0001",Rol.CLIENTE,"Ana","Lopez")); assertEquals(1,dao.listarClientes().size()); Persona p=dao.buscarPorId("CLI0001"); p.modificar("Maria","Perez"); dao.actualizar(p); assertEquals("MARIA",nuevo().buscarPorId("CLI0001").getNombre()); dao.eliminar("CLI0001"); assertFalse(nuevo().buscarPorId("CLI0001").isActivo()); dao.reactivar("CLI0001"); assertTrue(nuevo().buscarPorId("CLI0001").isActivo()); }
 @Test void separaClientesYMeseros(){ PersonaDAO dao=nuevo(); dao.agregar(new Persona("CLI0001",Rol.CLIENTE,"Ana","Lopez")); dao.agregar(new Persona("MSR0001",Rol.MESERO,"Juan","Perez")); assertEquals(1,dao.listarClientes().size()); assertEquals(1,dao.listarMeseros().size()); assertEquals(2,nuevo().listar().size()); }
 @Test void rechazaDuplicadosYEliminacionInexistente(){ PersonaDAO dao=nuevo(); dao.agregar(new Persona("CLI0001",Rol.CLIENTE,"Ana","Lopez")); assertThrows(EntidadDuplicadaException.class,()->dao.agregar(new Persona("CLI0001",Rol.CLIENTE,"Otro","Nombre"))); assertThrows(EntidadNoEncontradaException.class,()->dao.eliminar("CLI9999")); }
}
