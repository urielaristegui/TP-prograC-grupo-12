import java.nio.file.Paths;
import java.util.ArrayList;
import javax.swing.*;

import Controlador.AutenticacionController;
import Controlador.MainController;
import modelo.Persistencia.ArchivoUsuarioDao;
import modelo.Persistencia.AutenticacionService;
import modelo.Persistencia.UsuarioDAO;
import modelo.elementos.Asteroide;
import modelo.elementos.ElementoDelUniverso;
import modelo.elementos.EstacionDeCarga;
import modelo.nave.Nave;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;
import modelo.universo.Universo;
import vista.VistaPrincipal.JFrameMain;

public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainApp::run);


    private static void run() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Capa de Persistencia y Servicios
        UsuarioDAO usuarioDAO = new ArchivoUsuarioDao(Paths.get("target/usuarios.txt"));
        AutenticacionService authService = new AutenticacionService(usuarioDAO);

        // Capa de Controladores
        MainController mainController = new MainController(authService);
        AutenticacionController authController = new AutenticacionController(authService);

        Nave jugador = NaveFactory.crearNave(
                TipoNave.EXPLORADORA, 1
        );

        ArrayList<ElementoDelUniverso> elementos =
                new ArrayList<>();

        // Crear asteroides
        for (int i = 0; i < 20; i++) {
            elementos.add(new Asteroide(10));
        }

        // Crear estaciones
        for (int i = 0; i < 6; i++) {
            elementos.add(new EstacionDeCarga(20, 20));
        }

        // Crear naves enemigas
        elementos.add(NaveFactory.crearNave(
                TipoNave.EXPLORADORA, 2
        ));

        elementos.add(NaveFactory.crearNave(
                TipoNave.CARGUERO, 3
        ));

        elementos.add(NaveFactory.crearNave(
                TipoNave.COMBATE, 4
        ));
        try {
            Universo universo = new Universo(jugador, elementos);

            universo.elegirObjetivo();
        } catch (Exception DimensionesDeTableroInconsistentesException) {
            System.out.println("no");
        }

        SwingUtilities.invokeLater(() -> {

            JFrame ventana =
                    new JFrame("Prueba del tablero espacial");
            // Vista Principal
            JFrameMain ventanaPrincipal = new JFrameMain(mainController, authController, universo);
            ventanaPrincipal.setVisible(true);
        });
    }
}