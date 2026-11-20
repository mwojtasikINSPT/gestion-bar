package gestionbar.DAOs;

import gestionbar.models.Mesa;
import gestionbar.exceptions.EntidadDuplicadaException;
import gestionbar.exceptions.EntidadNoEncontradaException;
import gestionbar.exceptions.OperacionNoPermitidaException;
import gestionbar.exceptions.RegistroInvalidoException;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MesaDAO implements ICrud<Mesa, String> {

    private final Path archivo;
    private final List<Mesa> mesas;

    public MesaDAO(Path archivo) {
        this.archivo = archivo;
        mesas = new ArrayList<>();

        cargarArchivo();
    }

    private void cargarArchivo() {
        for (String linea : ArchivoTexto.leerLineas(archivo)) {

                if (linea.isBlank()) {
                    continue;
                }

                String[] datos = linea.split(",", -1);

                if (datos.length != 3) {
                    throw new RegistroInvalidoException(
                            "Registro inválido en mesas.txt");
                }

                String id = datos[0].trim();
                int capacidad;

                try {
                    capacidad = Integer.parseInt(datos[1].trim());
                } catch (NumberFormatException e) {
                    throw new RegistroInvalidoException(
                            "Capacidad inválida para la mesa: " + id);
                }

                if (capacidad <= 0) {
                    throw new RegistroInvalidoException(
                            "La capacidad debe ser positiva: " + id);
                }

                String estado = datos[2].trim();

                if (!estado.equals("true")
                        && !estado.equals("false")) {
                    throw new RegistroInvalidoException(
                            "Estado inválido para la mesa: " + id);
                }

                if (buscarInterno(id) != null) {
                    throw new EntidadDuplicadaException(
                            "ID duplicado en mesas.txt: " + id);
                }

                Mesa mesa = new Mesa(id, capacidad);
                mesa.setActivo(Boolean.parseBoolean(estado));

                mesas.add(mesa);
        }
    }

    private Mesa copiar(Mesa mesa) {

        Mesa copia = new Mesa(
                mesa.getId(),
                mesa.getCapacidad()
        );

        copia.setActivo(mesa.isActivo());

        return copia;
    }

    private Mesa buscarInterno(String id) {

        if (id == null) {
            return null;
        }

        for (Mesa mesa : mesas) {
            if (mesa.getId().equalsIgnoreCase(id)) {
                return mesa;
            }
        }

        return null;
    }

    private void guardarArchivo(List<Mesa> datos) {

        List<String> lineas = new ArrayList<>();

        for (Mesa mesa : datos) {

            lineas.add(
                    mesa.getId() + ","
                    + mesa.getCapacidad() + ","
                    + mesa.isActivo()
            );
        }

        ArchivoTexto.reemplazar(archivo, lineas);
    }

    private void guardarCambios(List<Mesa> nuevosDatos) {

        guardarArchivo(nuevosDatos);

        mesas.clear();

        for (Mesa mesa : nuevosDatos) {
            mesas.add(copiar(mesa));
        }
    }

    @Override
    public void agregar(Mesa entidad) {

        if (entidad == null) {
            throw new IllegalArgumentException(
                    "La mesa no puede ser nula");
        }

        if (entidad.getCapacidad() <= 0) {
            throw new RegistroInvalidoException(
                    "La capacidad debe ser mayor que cero");
        }

        if (buscarInterno(entidad.getId()) != null) {
            throw new EntidadDuplicadaException(
                    "Ya existe una mesa con ID: "
                    + entidad.getId());
        }

        List<Mesa> nuevosDatos = listar();
        nuevosDatos.add(copiar(entidad));

        guardarCambios(nuevosDatos);
    }

    @Override
    public List<Mesa> listar() {

        List<Mesa> resultado = new ArrayList<>();

        for (Mesa mesa : mesas) {
            resultado.add(copiar(mesa));
        }

        return resultado;
    }

    @Override
    public Mesa buscarPorId(String id) {

        Mesa mesa = buscarInterno(id);

        return mesa == null ? null : copiar(mesa);
    }

    @Override
    public void actualizar(Mesa entidad) {

        if (entidad == null) {
            throw new IllegalArgumentException(
                    "La mesa no puede ser nula");
        }

        if (entidad.getCapacidad() <= 0) {
            throw new RegistroInvalidoException(
                    "La capacidad debe ser mayor que cero");
        }

        if (buscarInterno(entidad.getId()) == null) {
            throw new EntidadNoEncontradaException(
                    "Mesa no encontrada: " + entidad.getId());
        }

        List<Mesa> nuevosDatos = listar();

        for (int i = 0; i < nuevosDatos.size(); i++) {

            if (nuevosDatos.get(i).getId()
                    .equalsIgnoreCase(entidad.getId())) {

                nuevosDatos.set(i, copiar(entidad));
                break;
            }
        }

        guardarCambios(nuevosDatos);
    }

    @Override
    public void eliminar(String id) {

        cambiarEstado(id, false);
    }

    @Override
    public void reactivar(String id) {

        cambiarEstado(id, true);
    }

    private void cambiarEstado(String id, boolean activo) {

        Mesa existente = buscarInterno(id);

        if (existente == null) {
            throw new EntidadNoEncontradaException(
                    "Mesa no encontrada: " + id);
        }

        if (existente.isActivo() == activo) {
            throw new OperacionNoPermitidaException(
                    "La mesa ya tiene ese estado");
        }

        List<Mesa> nuevosDatos = listar();

        for (Mesa mesa : nuevosDatos) {

            if (mesa.getId().equalsIgnoreCase(id)) {
                mesa.setActivo(activo);
                break;
            }
        }

        guardarCambios(nuevosDatos);
    }
}
