package gestionbar.DAOs;

import gestionbar.models.Reserva;
import gestionbar.exceptions.EntidadDuplicadaException;
import gestionbar.exceptions.EntidadNoEncontradaException;
import gestionbar.exceptions.OperacionNoPermitidaException;
import gestionbar.exceptions.RegistroInvalidoException;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO implements IReservaDAO {

    private final Path archivo;
    private final List<Reserva> reservas;

    public ReservaDAO(Path archivo) {
        this.archivo = archivo;
        reservas = new ArrayList<>();

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
                            "Registro inválido en reservas.txt");
                }

                String id = datos[0].trim();
                String idCliente = datos[1].trim();
                String idMesa = datos[2].trim();
                String estado = datos[3].trim();

                if (id.isEmpty() || idCliente.isEmpty()
                        || idMesa.isEmpty()) {
                    throw new RegistroInvalidoException(
                            "Datos incompletos en reservas.txt");
                }

                if (!estado.equals("true")
                        && !estado.equals("false")) {
                    throw new RegistroInvalidoException(
                            "Estado inválido para la reserva: " + id);
                }

                if (buscarInterno(id) != null) {
                    throw new EntidadDuplicadaException(
                            "ID duplicado en reservas.txt: " + id);
                }

                Reserva reserva = new Reserva(
                        id, idCliente, idMesa);

                reserva.setActivo(Boolean.parseBoolean(estado));

                reservas.add(reserva);
        }
    }

    private Reserva copiar(Reserva reserva) {

        Reserva copia = new Reserva(
                reserva.getId(),
                reserva.getIdCliente(),
                reserva.getIdMesa()
        );

        copia.setActivo(reserva.isActivo());

        return copia;
    }

    private Reserva buscarInterno(String id) {

        if (id == null) {
            return null;
        }

        for (Reserva reserva : reservas) {
            if (reserva.getId().equalsIgnoreCase(id)) {
                return reserva;
            }
        }

        return null;
    }

    private void guardarArchivo(List<Reserva> datos) {

        List<String> lineas = new ArrayList<>();

        for (Reserva reserva : datos) {

            lineas.add(
                    reserva.getId() + ","
                    + reserva.getIdCliente() + ","
                    + reserva.getIdMesa() + ","
                    + reserva.isActivo()
            );
        }

        ArchivoTexto.reemplazar(archivo, lineas);
    }

    private void guardarCambios(List<Reserva> nuevosDatos) {

        guardarArchivo(nuevosDatos);

        reservas.clear();

        for (Reserva reserva : nuevosDatos) {
            reservas.add(copiar(reserva));
        }
    }

    @Override
    public void agregar(Reserva entidad) {

        if (entidad == null) {
            throw new IllegalArgumentException(
                    "La reserva no puede ser nula");
        }

        if (buscarInterno(entidad.getId()) != null) {
            throw new EntidadDuplicadaException(
                    "Ya existe una reserva con ID: "
                    + entidad.getId());
        }

        List<Reserva> nuevosDatos = listar();
        nuevosDatos.add(copiar(entidad));

        guardarCambios(nuevosDatos);
    }

    @Override
    public List<Reserva> listar() {

        List<Reserva> resultado = new ArrayList<>();

        for (Reserva reserva : reservas) {
            resultado.add(copiar(reserva));
        }

        return resultado;
    }

    @Override
    public Reserva buscarPorId(String id) {

        Reserva reserva = buscarInterno(id);

        return reserva == null ? null : copiar(reserva);
    }

    @Override
    public void actualizar(Reserva entidad) {

        actualizarTodas(java.util.Collections.singletonList(entidad));
    }

    @Override
    public void actualizarTodas(List<Reserva> entidades) {
        if (entidades.isEmpty()) {
            return;
        }

        List<Reserva> nuevosDatos = listar();
        for (Reserva entidad : entidades) {
            actualizarEnLista(entidad, nuevosDatos);
        }
        guardarCambios(nuevosDatos);
    }

    private void actualizarEnLista(Reserva entidad, List<Reserva> nuevosDatos) {
        if (entidad == null) {
            throw new IllegalArgumentException("La reserva no puede ser nula");
        }

        for (int i = 0; i < nuevosDatos.size(); i++) {
            if (nuevosDatos.get(i).getId().equalsIgnoreCase(entidad.getId())) {
                nuevosDatos.set(i, copiar(entidad));
                return;
            }
        }

        throw new EntidadNoEncontradaException(
                "Reserva no encontrada: " + entidad.getId());
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

        Reserva existente = buscarInterno(id);

        if (existente == null) {
            throw new EntidadNoEncontradaException(
                    "Reserva no encontrada: " + id);
        }

        if (existente.isActivo() == activo) {
            throw new OperacionNoPermitidaException(
                    "La reserva ya tiene ese estado");
        }

        List<Reserva> nuevosDatos = listar();

        for (Reserva reserva : nuevosDatos) {

            if (reserva.getId().equalsIgnoreCase(id)) {
                reserva.setActivo(activo);
                break;
            }
        }

        guardarCambios(nuevosDatos);
    }

    @Override
    public List<Reserva> listarPorCliente(String idCliente) {

        List<Reserva> resultado = new ArrayList<>();

        for (Reserva reserva : reservas) {

            if (reserva.getIdCliente()
                    .equalsIgnoreCase(idCliente)) {
                resultado.add(copiar(reserva));
            }
        }

        return resultado;
    }

    @Override
    public List<Reserva> listarPorMesa(String idMesa) {

        List<Reserva> resultado = new ArrayList<>();

        for (Reserva reserva : reservas) {

            if (reserva.getIdMesa()
                    .equalsIgnoreCase(idMesa)) {
                resultado.add(copiar(reserva));
            }
        }

        return resultado;
    }

    @Override
    public int contarReservasActivasPorMesa(String idMesa) {

        int cantidad = 0;

        for (Reserva reserva : reservas) {

            if (reserva.isActivo()
                    && reserva.getIdMesa()
                            .equalsIgnoreCase(idMesa)) {
                cantidad++;
            }
        }

        return cantidad;
    }

    @Override
    public boolean tieneReservaActiva(String idCliente) {

        for (Reserva reserva : reservas) {

            if (reserva.isActivo()
                    && reserva.getIdCliente()
                            .equalsIgnoreCase(idCliente)) {
                return true;
            }
        }

        return false;
    }
}
