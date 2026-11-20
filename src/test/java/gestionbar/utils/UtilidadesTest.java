package gestionbar.utils;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class UtilidadesTest {
 @Test void normalizaYValidaTexto(){ assertEquals("HOLA",Validaciones.normalizarTexto(" hola ")); assertFalse(Validaciones.esTextoValido("  ")); assertThrows(IllegalArgumentException.class,()->Validaciones.validarTextoObligatorio("a,b","nombre")); }
 @Test void validaFormatoDeCodigos(){ assertTrue(Validaciones.esCodigoValido("CLI0001","CLI")); assertFalse(Validaciones.esCodigoValido("CLI001","CLI")); assertFalse(Validaciones.esCodigoValido("MSR0001","CLI")); }
 @Test void generaCodigoSiguienteAlMaximo(){ assertEquals("CLI0011",GeneradorCodigo.generarSiguienteId(List.of("CLI0002","CLI0010","MSR0040"),"CLI")); assertEquals("MTA0001",GeneradorCodigo.generarSiguienteId(List.of(),"MTA")); }
 @Test void rechazaPrefijoInvalidoYCodigosAgotados(){ assertThrows(IllegalArgumentException.class,()->GeneradorCodigo.generarSiguienteId(List.of(),"CLIENTE")); assertThrows(IllegalStateException.class,()->GeneradorCodigo.generarSiguienteId(List.of("CLI9999"),"CLI")); }
}
