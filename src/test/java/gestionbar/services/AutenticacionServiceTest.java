package gestionbar.services;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class AutenticacionServiceTest {
 @Test void autenticaSoloCredencialesCorrectas(){ AutenticacionService s=new AutenticacionService("admin","clave123"); assertTrue(s.autenticar("admin","clave123")); assertFalse(s.autenticar("admin","incorrecta")); assertFalse(s.autenticar("otro","clave123")); assertFalse(s.autenticar(null,"clave123")); }
 @Test void rechazaConfiguracionNula(){ assertThrows(NullPointerException.class,()->new AutenticacionService(null,"clave")); }
}
