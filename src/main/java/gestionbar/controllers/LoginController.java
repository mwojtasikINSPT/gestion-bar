package gestionbar.controllers;

import gestionbar.services.IAutenticacionService;

public final class LoginController {

    private final IAutenticacionService autenticacionService;

    public LoginController(IAutenticacionService autenticacionService) {
        this.autenticacionService = autenticacionService;
    }

    public boolean iniciarSesion(String usuario, String clave) {
        return autenticacionService.autenticar(usuario, clave);
    }
}
