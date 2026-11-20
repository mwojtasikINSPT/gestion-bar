package gestionbar.DAOs;
import gestionbar.models.Mesa;
import gestionbar.exceptions.*;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
class MesaDAOTest {
 @TempDir Path temp;
 private MesaDAO nuevo(){ return new MesaDAO(temp.resolve("mesas.txt")); }
 @Test void altaConsultaModificacionYPersistencia(){ MesaDAO dao=nuevo(); Mesa m=new Mesa("MTA0001",4); dao.agregar(m); assertEquals(1,nuevo().listar().size()); m.setCapacidad(8); dao.actualizar(m); assertEquals(8,nuevo().buscarPorId("MTA0001").getCapacidad()); }
 @Test void bajaLogicaYReactivacion(){ MesaDAO dao=nuevo(); dao.agregar(new Mesa("MTA0001",4)); dao.eliminar("MTA0001"); assertFalse(nuevo().buscarPorId("MTA0001").isActivo()); dao.reactivar("MTA0001"); assertTrue(nuevo().buscarPorId("MTA0001").isActivo()); }
 @Test void rechazaDuplicadoEInexistente(){ MesaDAO dao=nuevo(); dao.agregar(new Mesa("MTA0001",4)); assertThrows(EntidadDuplicadaException.class,()->dao.agregar(new Mesa("MTA0001",4))); assertThrows(EntidadNoEncontradaException.class,()->dao.eliminar("MTA9999")); }
}
