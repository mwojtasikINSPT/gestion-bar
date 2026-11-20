package gestionbar.services;

import java.util.Objects;

public final class AutenticacionService implements IAutenticacionService {

    private final String usuarioConfigurado;
    private final String claveConfigurada;

    public AutenticacionService(String usuarioConfigurado,
            String claveConfigurada) {
        this.usuarioConfigurado = Objects.requireNonNull(usuarioConfigurado);
        this.claveConfigurada = Objects.requireNonNull(claveConfigurada);
    }

    @Override
    public boolean autenticar(String usuario, String clave) {
        return usuarioConfigurado.equals(usuario)
                && claveConfigurada.equals(clave);
    }
}
