package gestionbar.DAOs;
import gestionbar.models.Reserva;
import gestionbar.exceptions.*;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
class ReservaDAOTest {
 @TempDir Path temp;
 private ReservaDAO nuevo(){ return new ReservaDAO(temp.resolve("reservas.txt")); }
 @Test void altaConsultaModificacionYPersistencia(){ ReservaDAO dao=nuevo(); Reserva m=new Reserva("RES0001","CLI0001","MTA0001"); dao.agregar(m); assertEquals(1,nuevo().listar().size()); m.setIdMesa("MTA0002"); dao.actualizar(m); assertEquals("MTA0002",nuevo().buscarPorId("RES0001").getIdMesa()); }
 @Test void bajaLogicaYReactivacion(){ ReservaDAO dao=nuevo(); dao.agregar(new Reserva("RES0001","CLI0001","MTA0001")); dao.eliminar("RES0001"); assertFalse(nuevo().buscarPorId("RES0001").isActivo()); dao.reactivar("RES0001"); assertTrue(nuevo().buscarPorId("RES0001").isActivo()); }
 @Test void rechazaDuplicadoEInexistente(){ ReservaDAO dao=nuevo(); dao.agregar(new Reserva("RES0001","CLI0001","MTA0001")); assertThrows(EntidadDuplicadaException.class,()->dao.agregar(new Reserva("RES0001","CLI0001","MTA0001"))); assertThrows(EntidadNoEncontradaException.class,()->dao.eliminar("RES9999")); }
}
