
package Pruebas;

import java.awt.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.swing.*;

import Controlador.AutenticacionController;
import modelo.Persistencia.ArchivoUsuarioDao;
import modelo.Persistencia.AutenticacionService;
import modelo.Persistencia.UsuarioDAO;
import modelo.Persistencia.UsuarioDTO;
import modelo.elementos.Asteroide;
import modelo.elementos.ElementoDelUniverso;
import modelo.elementos.EstacionDeCarga;
import modelo.nave.Nave;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;
import modelo.universo.Universo;
import vista.Tablero.JPanelTablero;
import vista.Persistencia.AutenticacionObserver;
import vista.Persistencia.JPanelLogin;
import vista.Persistencia.JPanelRegistro;
import vista.Persistencia.NavegacionAccesoObserver;

/**
 * Pruebas.PruebaPanelMisiones.Prueba visual del tablero del universo.
 */
public class PruebaJPanelTablero {

    /**
     * Inicia la simulacion grafica.
     *
     * Pre: ninguna.
     * Post:
     * - Crea un universo de 20x20.
     * - Distribuye elementos aleatoriamente.
     * - Abre una ventana Swing.
     * - Ejecuta turnos del tablero cada 600 ms.
     *
     * @param args no utilizado
     * @throws Exception si el tablero rechaza la configuracion
     */
    public static void main(String[] args) throws Exception {

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

        Universo universo = new Universo(jugador, elementos);

        universo.elegirObjetivo();

        SwingUtilities.invokeLater(() -> {

            JFrame ventana =
                    new JFrame("Pruebas.PruebaPanelMisiones.Prueba del tablero espacial");

            JPanelTablero panel =
                    new JPanelTablero(universo);

            ventana.setDefaultCloseOperation(
                    JFrame.EXIT_ON_CLOSE
            );

            ventana.add(panel);

            ventana.pack();
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);

            // Simular turnos del tablero
            Timer timer = new Timer(600, e -> {
                universo.avanzarTurnoDestino();
            });

            timer.start();
        });
    }

    public static class PruebaPersistencia {

        private static final String CARD_LOGIN = "LOGIN";
        private static final String CARD_REGISTRO = "REGISTRO";

        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> {
                JFrame frame = new JFrame("Pruebas.PruebaPanelMisiones.Prueba - Login y Registro");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                CardLayout cardLayout = new CardLayout();
                JPanel contenedor = new JPanel(cardLayout);

                // Archivo local exclusivo para pruebas de login/registro
                UsuarioDAO usuarioDAO = new ArchivoUsuarioDao(Paths.get("target/prueba_usuarios.txt"));
                AutenticacionService autenticacionService = new AutenticacionService(usuarioDAO);
                AutenticacionController controller = new AutenticacionController(autenticacionService);

                JPanelLogin panelLogin = new JPanelLogin(controller);
                JPanelRegistro panelRegistro = new JPanelRegistro(controller);

                contenedor.add(panelLogin, CARD_LOGIN);
                contenedor.add(panelRegistro, CARD_REGISTRO);

                controller.setNavegacionObserver(new NavegacionAccesoObserver() {
                    @Override
                    public void mostrarRegistro() {
                        cardLayout.show(contenedor, CARD_REGISTRO);
                    }

                    @Override
                    public void mostrarLogin() {
                        cardLayout.show(contenedor, CARD_LOGIN);
                    }
                });

                controller.agregarObservador(new AutenticacionObserver() {
                    @Override
                    public void onAutenticacionExitosa(UsuarioDTO usuario) {
                        JOptionPane.showMessageDialog(
                                frame,
                                "Login correcto. Usuario activo: " + usuario.getNombreUsuario(),
                                "Autenticación Exitosa",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }

                    @Override
                    public void onRegistroResultado(boolean registrado, String mensaje) {
                        if (registrado) {
                            cardLayout.show(contenedor, CARD_LOGIN);
                        }
                    }

                    @Override
                    public void onSesionCerrada() {
                        cardLayout.show(contenedor, CARD_LOGIN);
                    }

                    @Override
                    public void onErrorAutenticacion(String mensaje) {
                        // Mostrado en el panel de login mediante el observador
                    }
                });

                frame.add(contenedor);
                frame.setMinimumSize(new Dimension(420, 280));
                frame.pack();
                frame.setLocationRelativeTo(null);
                cardLayout.show(contenedor, CARD_LOGIN);
                frame.setVisible(true);
            });
        }
    }
}
