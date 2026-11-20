package gestionbar.DAOs;

import gestionbar.exceptions.PersistenciaException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

final class ArchivoTexto {

    private ArchivoTexto() {
    }

    static List<String> leerLineas(Path archivo) {
        if (!Files.exists(archivo)) {
            return List.of();
        }
        try {
            return Files.readAllLines(archivo, Charset.defaultCharset());
        } catch (IOException e) {
            throw new PersistenciaException(
                    "No se pudieron leer los datos de "
                    + archivo.getFileName(), e);
        }
    }

    static void reemplazar(Path destino, List<String> lineas) {
        try {
            escribirYReemplazar(destino, lineas);
        } catch (IOException e) {
            throw new PersistenciaException(
                    "No se pudieron guardar los datos en " + destino.getFileName(), e);
        }
    }

    private static void escribirYReemplazar(Path destino, List<String> lineas)
            throws IOException {
        Path ruta = destino.toAbsolutePath();
        Path temporal = Files.createTempFile(
                ruta.getParent(), ruta.getFileName().toString(), ".tmp");

        try {
            Files.write(temporal, lineas, Charset.defaultCharset());
            try {
                Files.move(temporal, ruta, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporal, ruta, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | RuntimeException e) {
            try {
                Files.deleteIfExists(temporal);
            } catch (IOException | RuntimeException errorLimpieza) {
                e.addSuppressed(errorLimpieza);
            }
            throw e;
        }
    }
}
