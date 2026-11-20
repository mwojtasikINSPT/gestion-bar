package gestionbar.DAOs;
import gestionbar.models.Asignacion;
import gestionbar.exceptions.*;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
class AsignacionDAOTest {
 @TempDir Path temp;
 private AsignacionDAO nuevo(){ return new AsignacionDAO(temp.resolve("asignacions.txt")); }
 @Test void altaConsultaModificacionYPersistencia(){ AsignacionDAO dao=nuevo(); Asignacion m=new Asignacion("ASG0001","MSR0001","MTA0001"); dao.agregar(m); assertEquals(1,nuevo().listar().size()); m.setIdMesa("MTA0002"); dao.actualizar(m); assertEquals("MTA0002",nuevo().buscarPorId("ASG0001").getIdMesa()); }
 @Test void bajaLogicaYReactivacion(){ AsignacionDAO dao=nuevo(); dao.agregar(new Asignacion("ASG0001","MSR0001","MTA0001")); dao.eliminar("ASG0001"); assertFalse(nuevo().buscarPorId("ASG0001").isActivo()); dao.reactivar("ASG0001"); assertTrue(nuevo().buscarPorId("ASG0001").isActivo()); }
 @Test void rechazaDuplicadoEInexistente(){ AsignacionDAO dao=nuevo(); dao.agregar(new Asignacion("ASG0001","MSR0001","MTA0001")); assertThrows(EntidadDuplicadaException.class,()->dao.agregar(new Asignacion("ASG0001","MSR0001","MTA0001"))); assertThrows(EntidadNoEncontradaException.class,()->dao.eliminar("ASG9999")); }
}
