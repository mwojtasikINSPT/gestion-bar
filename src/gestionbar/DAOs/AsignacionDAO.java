package gestionbar.DAOs;

import gestionbar.models.Asignacion;
import gestionbar.exceptions.EntidadDuplicadaException;
import gestionbar.exceptions.EntidadNoEncontradaException;
import gestionbar.exceptions.OperacionNoPermitidaException;
import gestionbar.exceptions.RegistroInvalidoException;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AsignacionDAO implements IAsignacionDAO {

    private final Path archivo;
    private final List<Asignacion> asignaciones;

    public AsignacionDAO(Path archivo) {
        this.archivo = archivo;
        asignaciones = new ArrayList<>();

        cargarArchivo();
    }

    private void cargarArchivo() {
        for (String linea : ArchivoTexto.leerLineas(archivo)) {

                if (linea.isBlank()) {
                    continue;
                }

                String[] datos = linea.split(",", -1);

                if (datos.length != 4) {
                    throw new RegistroInvalidoException(
                            "Registro inválido en asignaciones.txt");
                }

                String id = datos[0].trim();
                String idMesero = datos[1].trim();
                String idMesa = datos[2].trim();
                String estado = datos[3].trim();

                if (id.isEmpty() || idMesero.isEmpty()
                        || idMesa.isEmpty()) {
                    throw new RegistroInvalidoException(
                            "Datos incompletos en asignaciones.txt");
                }

                if (!estado.equals("true")
                        && !estado.equals("false")) {
                    throw new RegistroInvalidoException(
                            "Estado inválido para la asignación: " + id);
                }

                if (buscarInterno(id) != null) {
                    throw new EntidadDuplicadaException(
                            "ID duplicado en asignaciones.txt: " + id);
                }

                Asignacion asignacion = new Asignacion(
                        id, idMesero, idMesa);

                asignacion.setActivo(Boolean.parseBoolean(estado));

                asignaciones.add(asignacion);
        }
    }

    private Asignacion copiar(Asignacion asignacion) {

        Asignacion copia = new Asignacion(
                asignacion.getId(),
                asignacion.getIdMesero(),
                asignacion.getIdMesa()
        );

        copia.setActivo(asignacion.isActivo());

        return copia;
    }

    private Asignacion buscarInterno(String id) {

        if (id == null) {
            return null;
        }

        for (Asignacion asignacion : asignaciones) {
            if (asignacion.getId().equalsIgnoreCase(id)) {
                return asignacion;
            }
        }

        return null;
    }

    private void guardarArchivo(List<Asignacion> datos) {

        List<String> lineas = new ArrayList<>();

        for (Asignacion asignacion : datos) {

            lineas.add(
                    asignacion.getId() + ","
                    + asignacion.getIdMesero() + ","
                    + asignacion.getIdMesa() + ","
                    + asignacion.isActivo()
            );
        }

        ArchivoTexto.reemplazar(archivo, lineas);
    }

    private void guardarCambios(List<Asignacion> nuevosDatos) {

        guardarArchivo(nuevosDatos);

        asignaciones.clear();

        for (Asignacion asignacion : nuevosDatos) {
            asignaciones.add(copiar(asignacion));
        }
    }

    @Override
    public void agregar(Asignacion entidad) {

        if (entidad == null) {
            throw new IllegalArgumentException(
                    "La asignación no puede ser nula");
        }

        if (entidad.getId() == null
                || entidad.getId().isBlank()
                || entidad.getIdMesero() == null
                || entidad.getIdMesero().isBlank()
                || entidad.getIdMesa() == null
                || entidad.getIdMesa().isBlank()) {
            throw new RegistroInvalidoException(
                    "Los datos de la asignación son obligatorios");
        }

        if (buscarInterno(entidad.getId()) != null) {
            throw new EntidadDuplicadaException(
                    "Ya existe una asignación con ID: "
                    + entidad.getId());
        }

        validarMesaDisponible(entidad, asignaciones);

        List<Asignacion> nuevosDatos = listar();
        nuevosDatos.add(copiar(entidad));

        guardarCambios(nuevosDatos);
    }

    @Override
    public List<Asignacion> listar() {

        List<Asignacion> resultado = new ArrayList<>();

        for (Asignacion asignacion : asignaciones) {
            resultado.add(copiar(asignacion));
        }

        return resultado;
    }

    @Override
    public Asignacion buscarPorId(String id) {

        Asignacion asignacion = buscarInterno(id);

        return asignacion == null ? null : copiar(asignacion);
    }

    @Override
    public void actualizar(Asignacion entidad) {

        actualizarTodas(java.util.Collections.singletonList(entidad));
    }

    @Override
    public void actualizarTodas(List<Asignacion> entidades) {
        if (entidades.isEmpty()) {
            return;
        }

        List<Asignacion> nuevosDatos = listar();
        for (Asignacion entidad : entidades) {
            actualizarEnLista(entidad, nuevosDatos);
        }
        guardarCambios(nuevosDatos);
    }

    private void actualizarEnLista(Asignacion entidad, List<Asignacion> nuevosDatos) {
        if (entidad == null) {
            throw new IllegalArgumentException(
                    "La asignación no puede ser nula");
        }

        Asignacion existente = buscarInterno(entidad.getId());

        if (existente == null) {
            throw new EntidadNoEncontradaException(
                    "Asignación no encontrada: " + entidad.getId());
        }

        if (entidad.getIdMesero() == null
                || entidad.getIdMesero().isBlank()
                || entidad.getIdMesa() == null
                || entidad.getIdMesa().isBlank()) {
            throw new RegistroInvalidoException(
                    "Los datos de la asignación son obligatorios");
        }

        validarMesaDisponible(entidad, nuevosDatos);

        reemplazarEnLista(entidad, nuevosDatos);
    }

    private void reemplazarEnLista(Asignacion entidad, List<Asignacion> nuevosDatos) {
        for (int i = 0; i < nuevosDatos.size(); i++) {

            if (nuevosDatos.get(i).getId()
                    .equalsIgnoreCase(entidad.getId())) {

                nuevosDatos.set(i, copiar(entidad));
                break;
            }
        }

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

        Asignacion existente = buscarInterno(id);

        if (existente == null) {
            throw new EntidadNoEncontradaException(
                    "Asignación no encontrada: " + id);
        }

        if (existente.isActivo() == activo) {
            throw new OperacionNoPermitidaException(
                    "La asignación ya tiene ese estado");
        }

        Asignacion modificada = copiar(existente);
        modificada.setActivo(activo);
        List<Asignacion> nuevosDatos = listar();
        validarMesaDisponible(modificada, nuevosDatos);
        reemplazarEnLista(modificada, nuevosDatos);
        guardarCambios(nuevosDatos);
    }

    private void validarMesaDisponible(Asignacion entidad, List<Asignacion> datos) {
        if (!entidad.isActivo()) {
            return;
        }

        for (Asignacion asignacion : datos) {
            if (asignacion.isActivo()
                    && !asignacion.getId().equalsIgnoreCase(entidad.getId())
                    && asignacion.getIdMesa().equalsIgnoreCase(entidad.getIdMesa())) {
                throw new OperacionNoPermitidaException(
                        "La mesa ya tiene un mesero asignado");
            }
        }
    }

    @Override
    public List<Asignacion> listarPorMesero(String idMesero) {

        List<Asignacion> resultado = new ArrayList<>();

        if (idMesero == null) {
            return resultado;
        }

        for (Asignacion asignacion : asignaciones) {

            if (asignacion.getIdMesero()
                    .equalsIgnoreCase(idMesero)) {
                resultado.add(copiar(asignacion));
            }
        }

        return resultado;
    }

    @Override
    public List<Asignacion> listarPorMesa(String idMesa) {

        List<Asignacion> resultado = new ArrayList<>();

        if (idMesa == null) {
            return resultado;
        }

        for (Asignacion asignacion : asignaciones) {

            if (asignacion.getIdMesa()
                    .equalsIgnoreCase(idMesa)) {
                resultado.add(copiar(asignacion));
            }
        }

        return resultado;
    }

    @Override
    public boolean tieneAsignacionActiva(String idMesa) {

        if (idMesa == null) {
            return false;
        }

        for (Asignacion asignacion : asignaciones) {

            if (asignacion.isActivo()
                    && asignacion.getIdMesa()
                            .equalsIgnoreCase(idMesa)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Asignacion buscarAsignacionActivaPorMesa(String idMesa) {

        if (idMesa == null) {
            return null;
        }

        for (Asignacion asignacion : asignaciones) {

            if (asignacion.isActivo()
                    && asignacion.getIdMesa()
                            .equalsIgnoreCase(idMesa)) {
                return copiar(asignacion);
            }
        }

        return null;
    }
}
