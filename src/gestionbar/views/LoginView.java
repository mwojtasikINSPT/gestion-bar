package gestionbar.views;

import gestionbar.controllers.LoginController;
import gestionbar.utils.Consola;
import java.util.NoSuchElementException;

public class LoginView {

    private final LoginController loginController;
    private final Consola consola;

    public LoginView(LoginController loginController, Consola consola) {
        this.loginController = loginController;
        this.consola = consola;
    }

    public boolean solicitarAcceso() {
        consola.imprimirTitulo("INICIO DE SESIÓN");

        try {
            while (true) {
                String usuario = consola.leerTexto("Usuario (0 para salir)");
                if ("0".equals(usuario)) {
                    return false;
                }

                String clave = consola.leerTexto("Contraseña");
                if (loginController.iniciarSesion(usuario, clave)) {
                    return true;
                }

                consola.imprimirError("Usuario o contraseña incorrectos.");
            }
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}

