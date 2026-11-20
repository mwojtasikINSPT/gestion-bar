package gestionbar.DAOs;

import java.util.List;

public interface ICrud<T, ID> {

    void agregar(T entidad);
    
    List<T> listar();

    T buscarPorId(ID id);

    void actualizar(T entidad);

    void eliminar(ID id);

    void reactivar(ID id);
}
