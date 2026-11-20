package gestionbar.DAOs;

import gestionbar.models.Persona;
import gestionbar.models.Rol;

import gestionbar.exceptions.EntidadDuplicadaException;
import gestionbar.exceptions.EntidadNoEncontradaException;
import gestionbar.exceptions.OperacionNoPermitidaException;
import gestionbar.exceptions.RegistroInvalidoException;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PersonaDAO implements IPersonaDAO {

    private final Path archivoClientes;
    private final Path archivoMeseros;
    private final List<Persona> personas;

    public PersonaDAO(Path archivoClientes, Path archivoMeseros) {
        this.archivoClientes = archivoClientes;
        this.archivoMeseros = archivoMeseros;
        personas = new ArrayList<>();

        cargarArchivo(archivoClientes, Rol.CLIENTE);
        cargarArchivo(archivoMeseros, Rol.MESERO);
    }

    private void cargarArchivo(Path archivo, Rol tipo) {
        for (String linea : ArchivoTexto.leerLineas(archivo)) {

                if (linea.isBlank()) {
                    continue;
                }

                String[] datos = linea.split(",", -1);

                if (datos.length != 5) {
                    throw new RegistroInvalidoException(
                            "Registro inválido en " + archivo);
                }

                String id = datos[0];
                Rol rol;
                String nombre = datos[2];
                String apellido = datos[3];

                try {
                    rol = Rol.valueOf(
                            datos[1].trim().toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new RegistroInvalidoException(
                            "Rol inválido en " + archivo
                            + ": " + datos[1]);
                }

                if (rol != tipo) {
                    throw new RegistroInvalidoException(
                            "El rol " + rol
                            + " no corresponde al archivo " + archivo);
                }

                if (!datos[4].equals("true")
                        && !datos[4].equals("false")) {
                    throw new RegistroInvalidoException(
                            "Estado inválido en " + archivo);
                }

                boolean activo = Boolean.parseBoolean(datos[4]);

                Persona persona = new Persona(id, rol, nombre, apellido);
                persona.setActivo(activo);

                if (buscarInterno(id) != null) {
                    throw new EntidadDuplicadaException(
                            "ID duplicado en archivos: " + id);
                }

                personas.add(persona);
        }
    }

    private Persona copiar(Persona persona) {

        Persona copia = new Persona(persona.getId(), persona.getRol(),
                persona.getNombre(), persona.getApellido());
        copia.setActivo(persona.isActivo());

        return copia;
    }

    private Persona buscarInterno(String id) {

        if (id == null) {
            return null;
        }

        for (Persona persona : personas) {
            if (persona.getId().equalsIgnoreCase(id)) {
                return persona;
            }
        }

        return null;
    }

    private Path obtenerArchivo(Persona persona) {

        if (persona.getRol() == Rol.CLIENTE) {
            return archivoClientes;
        }

        if (persona.getRol() == Rol.MESERO) {
            return archivoMeseros;
        }

        throw new IllegalArgumentException(
                "Tipo de persona no permitido");
    }

    private void guardarArchivo(Path archivo, List<Persona> datos) {

        List<String> lineas = new ArrayList<>();

        for (Persona persona : datos) {

            if (!obtenerArchivo(persona).equals(archivo)) {
                continue;
            }

            lineas.add(
                    persona.getId() + ","
                    + persona.getRol().name() + ","
                    + persona.getNombre() + ","
                    + persona.getApellido() + ","
                    + persona.isActivo()
            );
        }

        ArchivoTexto.reemplazar(archivo, lineas);
    }

    private void guardarCambios(List<Persona> nuevosDatos, Persona persona) {

        guardarArchivo(obtenerArchivo(persona), nuevosDatos);

        personas.clear();

        for (Persona dato : nuevosDatos) {
            personas.add(copiar(dato));
        }
    }

    @Override
    public void agregar(Persona entidad) {

        if (entidad == null) {
            throw new IllegalArgumentException(
                    "La persona no puede ser nula");
        }

        if (buscarInterno(entidad.getId()) != null) {
            throw new EntidadDuplicadaException(
                    "Ya existe una persona con ID: " + entidad.getId());
        }

        List<Persona> nuevosDatos = listar();
        nuevosDatos.add(copiar(entidad));

        guardarCambios(nuevosDatos, entidad);
    }

    @Override
    public List<Persona> listar() {

        List<Persona> resultado = new ArrayList<>();

        for (Persona persona : personas) {
            resultado.add(copiar(persona));
        }

        return resultado;
    }

    @Override
    public Persona buscarPorId(String id) {

        Persona persona = buscarInterno(id);

        return persona == null ? null : copiar(persona);
    }

    @Override
    public void actualizar(Persona entidad) {

        if (entidad == null) {
            throw new IllegalArgumentException(
                    "La persona no puede ser nula");
        }

        Persona existente = buscarInterno(entidad.getId());

        if (existente == null) {
            throw new EntidadNoEncontradaException(
                    "Persona no encontrada: " + entidad.getId());
        }

        if (!existente.getClass().equals(entidad.getClass())) {
            throw new OperacionNoPermitidaException(
                    "No se puede cambiar el tipo de persona");
        }

        List<Persona> nuevosDatos = listar();

        for (int i = 0; i < nuevosDatos.size(); i++) {

            if (nuevosDatos.get(i).getId()
                    .equalsIgnoreCase(entidad.getId())) {

                nuevosDatos.set(i, copiar(entidad));
                break;
            }
        }

        guardarCambios(nuevosDatos, entidad);
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

        Persona existente = buscarInterno(id);

        if (existente == null) {
            throw new EntidadNoEncontradaException(
                    "Persona no encontrada: " + id);
        }

        if (existente.isActivo() == activo) {
            throw new OperacionNoPermitidaException(
                    "La persona ya tiene ese estado");
        }

        List<Persona> nuevosDatos = listar();

        for (Persona persona : nuevosDatos) {

            if (persona.getId().equalsIgnoreCase(id)) {
                persona.setActivo(activo);
                break;
            }
        }

        guardarCambios(nuevosDatos, existente);
    }

    @Override
    public List<Persona> listarClientes() {

        List<Persona> resultado = new ArrayList<>();

        for (Persona persona : personas) {
            if (persona.getRol() == Rol.CLIENTE) {
                resultado.add(copiar(persona));
            }
        }

        return resultado;
    }

    @Override
    public List<Persona> listarMeseros() {

        List<Persona> resultado = new ArrayList<>();

        for (Persona persona : personas) {
            if (persona.getRol() == Rol.MESERO) {
                resultado.add(copiar(persona));
            }
        }

        return resultado;
    }
}
