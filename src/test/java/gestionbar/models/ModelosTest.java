package gestionbar.models;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class ModelosTest {
 @Test void mesaValidaYActualizaCapacidad(){ Mesa m=new Mesa("MTA0001",4); assertEquals(4,m.getCapacidad()); m.setCapacidad(6); assertEquals(6,m.getCapacidad()); assertTrue(m.isActivo()); }
 @Test void mesaRechazaCapacidadInvalida(){ assertThrows(IllegalArgumentException.class,()->new Mesa("MTA0001",0)); assertThrows(IllegalArgumentException.class,()->new Mesa("MTA0001",-1)); }
 @Test void personaValidaRolYNormalizaDatos(){ Persona p=new Persona("CLI0001",Rol.CLIENTE," ana "," lopez "); assertEquals("ANA",p.getNombre()); assertEquals("LOPEZ",p.getApellido()); assertThrows(IllegalArgumentException.class,()->new Persona("CLI0002",null,"Ana","Lopez")); }
 @Test void personaModificacionEsAtomica(){ Persona p=new Persona("CLI0001",Rol.CLIENTE,"Ana","Lopez"); assertThrows(IllegalArgumentException.class,()->p.modificar("Nuevo","")); assertEquals("ANA",p.getNombre()); }
 @Test void reservaValidaCodigosYPermiteCambio(){ Reserva r=new Reserva("RES0001","CLI0001","MTA0001"); r.setIdMesa("MTA0002"); assertEquals("MTA0002",r.getIdMesa()); assertThrows(IllegalArgumentException.class,()->r.setIdCliente("MSR0001")); }
 @Test void asignacionValidaCodigosYEstado(){ Asignacion a=new Asignacion("ASG0001","MSR0001","MTA0001"); a.setActivo(false); assertFalse(a.isActivo()); assertThrows(IllegalArgumentException.class,()->a.setIdMesa("CLI0001")); }
}
