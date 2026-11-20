
import java.awt.CardLayout;
import java.awt.Dimension;
import java.nio.file.Paths;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import Controlador.AutenticacionController;
import modelo.Persistencia.ArchivoUsuarioDao;
import modelo.Persistencia.AutenticacionService;
import modelo.Persistencia.UsuarioDAO;
import modelo.Persistencia.UsuarioDTO;
import vista.Persistencia.AutenticacionObserver;
import vista.Persistencia.JPanelLogin;
import vista.Persistencia.JPanelRegistro;
import vista.Persistencia.NavegacionAccesoObserver;

public class PruebaPersistencia {

    private static final String CARD_LOGIN = "LOGIN";
    private static final String CARD_REGISTRO = "REGISTRO";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Prueba - Login y Registro");
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