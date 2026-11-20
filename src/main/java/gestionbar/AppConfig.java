package gestionbar;

import gestionbar.DAOs.IPersonaDAO;
import gestionbar.DAOs.PersonaDAO;
import gestionbar.DAOs.ICrud;
import gestionbar.DAOs.MesaDAO;
import gestionbar.models.Mesa;
import gestionbar.DAOs.IReservaDAO;
import gestionbar.DAOs.ReservaDAO;
import gestionbar.DAOs.IAsignacionDAO;
import gestionbar.DAOs.AsignacionDAO;

import gestionbar.services.PersonaService;
import gestionbar.services.AutenticacionService;
import gestionbar.services.DisponibilidadMesa;
import gestionbar.services.MesaService;
import gestionbar.services.ReservaService;
import gestionbar.services.AsignacionService;
import gestionbar.services.ConsultaService;

import gestionbar.controllers.PersonaController;
import gestionbar.controllers.LoginController;
import gestionbar.controllers.MesaController;
import gestionbar.controllers.ReservaController;
import gestionbar.controllers.AsignacionController;
import gestionbar.controllers.ConsultaController;

import gestionbar.views.PersonaView;
import gestionbar.views.ClienteView;
import gestionbar.views.MeseroView;
import gestionbar.views.LoginView;
import gestionbar.views.MesaView;
import gestionbar.views.ReservaView;
import gestionbar.views.AsignacionView;
import gestionbar.views.ConsultaView;
import gestionbar.views.MenuPrincipalView;
import gestionbar.views.OpcionMenu;
import gestionbar.utils.Consola;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

public class AppConfig {

    private static final String USUARIO_ADMIN = "admin";
    private static final String CLAVE_ADMIN = "1234";

    public void iniciar() {
        // Configuración de codificación UTF-8 para consola
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

        Consola consola = new Consola(System.in, System.out, System.err);

        AutenticacionService autenticacionService =
                new AutenticacionService(USUARIO_ADMIN, CLAVE_ADMIN);
        LoginController loginController = new LoginController(autenticacionService);
        LoginView loginView = new LoginView(loginController, consola);
        if (!loginView.solicitarAcceso()) {
            return;
        }

        // 1. Crear los DAO
        Path carpetaDatos = Path.of(".");
        IPersonaDAO personaDAO = new PersonaDAO(
                carpetaDatos.resolve("clientes.txt"),
                carpetaDatos.resolve("meseros.txt"));
        ICrud<Mesa, String> mesaDAO = new MesaDAO(
                carpetaDatos.resolve("mesas.txt"));
        IReservaDAO reservaDAO = new ReservaDAO(
                carpetaDatos.resolve("reservas.txt"));
        IAsignacionDAO asignacionDAO = new AsignacionDAO(
                carpetaDatos.resolve("asignaciones.txt"));

        // 2. Crear los Services
        DisponibilidadMesa disponibilidadMesa = new DisponibilidadMesa(
                reservaDAO, asignacionDAO, personaDAO);

        PersonaService personaService = new PersonaService(
                personaDAO,
                reservaDAO,
                asignacionDAO
        );

        MesaService mesaService = new MesaService(
                mesaDAO,
                reservaDAO,
                asignacionDAO,
                disponibilidadMesa
        );

        ReservaService reservaService = new ReservaService(
                personaDAO,
                mesaDAO,
                reservaDAO,
                disponibilidadMesa
        );

        AsignacionService asignacionService = new AsignacionService(
                personaDAO,
                mesaDAO,
                asignacionDAO,
                reservaDAO
        );

        ConsultaService consultaService = new ConsultaService(
                personaDAO,
                mesaDAO,
                reservaDAO,
                asignacionDAO
        );

        // 3. Crear los Controllers
        PersonaController personaController =
                new PersonaController(personaService);

        MesaController mesaController =
                new MesaController(mesaService);

        ReservaController reservaController =
                new ReservaController(reservaService);

        AsignacionController asignacionController =
                new AsignacionController(asignacionService);

        ConsultaController consultaController =
                new ConsultaController(consultaService);

        // 4. Crear las Views
        ClienteView clienteView =
                new ClienteView(personaController, consola);

        MeseroView meseroView =
                new MeseroView(personaController, consola);

        PersonaView personaView =
                new PersonaView(clienteView, meseroView, consola);

        MesaView mesaView =
                new MesaView(mesaController, consola);

        ReservaView reservaView =
                new ReservaView(reservaController, consola);

        AsignacionView asignacionView =
                new AsignacionView(asignacionController, consola);

        ConsultaView consultaView =
                new ConsultaView(consultaController, consola);

        // 5. Crear el menú principal
        MenuPrincipalView menuPrincipalView =
                new MenuPrincipalView(List.of(
                        new OpcionMenu(1, "Gestionar personas",
                                personaView),
                        new OpcionMenu(2, "Gestionar mesas",
                                mesaView),
                        new OpcionMenu(3, "Gestionar reservas",
                                reservaView),
                        new OpcionMenu(4, "Gestionar asignaciones",
                                asignacionView),
                        new OpcionMenu(5, "Consultas",
                                consultaView)
                ), consola);

        // 6. Iniciar el sistema
        menuPrincipalView.mostrarMenu();
    }
}
