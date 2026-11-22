package Pruebas;

import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import Controlador.AutenticacionController;
import Controlador.MainController;

import modelo.Persistencia.ArchivoUsuarioDao;
import modelo.Persistencia.AutenticacionService;
import modelo.Persistencia.UsuarioDAO;

import modelo.destinoytablero.CantidadDeElementosSuperaCasillaException;
import modelo.destinoytablero.DimensionesDeTableroInconsistentesException;

import modelo.elementos.Asteroide;
import modelo.elementos.ElementoDelUniverso;
import modelo.elementos.EstacionDeCarga;

import modelo.nave.Nave;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;

import modelo.universo.Universo;
import vista.VistaPrincipal.JFrameMain;

/**
 * Clase principal de la aplicacion.
 *
 * Pre: se dispone del entorno de ejecucion Java.
 * Post: inicializa el modelo, los controladores
 * y la interfaz grafica principal.
 */
public class MainApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainApp::run);
    }

    /**
     * Inicializa los componentes de la aplicacion.
     *
     * Pre: se ejecuta en el hilo de Swing.
     * Post: muestra el JFrame principal
     * si la inicializacion es exitosa.
     */
    private static void run() {

        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception ignored) {
            // Se utiliza el aspecto predeterminado.
        }

        try {

            // PERSISTENCIA

            UsuarioDAO usuarioDAO = new ArchivoUsuarioDao(
                    Paths.get("target/usuarios.txt")
            );

            AutenticacionService authService =
                    new AutenticacionService(usuarioDAO);

            // CONTROLADORES

            MainController mainController =
                    new MainController(authService);

            AutenticacionController authController =
                    new AutenticacionController(authService);

            // CREACION DE NAVE

            Nave jugador = NaveFactory.crearNave(
                    TipoNave.EXPLORADORA, 1
            );

            // ELEMENTOS DEL UNIVERSO

            ArrayList<ElementoDelUniverso> elementos =
                    new ArrayList<>();

            for (int i = 0; i < 20; i++) {
                elementos.add(new Asteroide(10));
            }

            for (int i = 0; i < 6; i++) {
                elementos.add(
                        new EstacionDeCarga(20, 20)
                );
            }

            // NAVES ENEMIGAS

            elementos.add(NaveFactory.crearNave(
                    TipoNave.EXPLORADORA, 2
            ));

            elementos.add(NaveFactory.crearNave(
                    TipoNave.CARGUERO, 3
            ));

            elementos.add(NaveFactory.crearNave(
                    TipoNave.COMBATE, 4
            ));
            elementos.add(NaveFactory.crearNave(
                    TipoNave.EXPLORADORA, 5
            ));

            elementos.add(NaveFactory.crearNave(
                    TipoNave.CARGUERO, 6
            ));

            elementos.add(NaveFactory.crearNave(
                    TipoNave.COMBATE, 7
            ));

            // INICIALIZAR UNIVERSO

            Universo universo = new Universo(
                    jugador, elementos
            );

            universo.elegirObjetivo();

            // INTERFAZ PRINCIPAL

            JFrameMain ventanaPrincipal = new JFrameMain(
                    mainController,
                    authController,
                    universo
            );

            ventanaPrincipal.setVisible(true);

        } catch (
                DimensionesDeTableroInconsistentesException |
                CantidadDeElementosSuperaCasillaException e
        ) {

            JOptionPane.showMessageDialog(
                    null,
                    "Error al crear el universo: "
                            + e.getMessage(),
                    "Error de inicializacion",
                    JOptionPane.ERROR_MESSAGE
            );

            e.printStackTrace();
        }
    }
}
