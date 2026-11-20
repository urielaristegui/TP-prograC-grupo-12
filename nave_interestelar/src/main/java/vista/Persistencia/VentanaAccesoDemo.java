package vista.Persistencia;

import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import Controlador.AutenticacionController;
import modelo.Persistencia.ArchivoUsuarioDao;
import modelo.Persistencia.AutenticacionService;
import modelo.Persistencia.UsuarioDAO;
import modelo.Persistencia.UsuarioDTO;

public class VentanaAccesoDemo extends JFrame implements NavegacionAccesoObserver, AutenticacionObserver {

    private static final String CARD_LOGIN = "LOGIN";
    private static final String CARD_REGISTRO = "REGISTRO";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contenedor = new JPanel(cardLayout);

    public VentanaAccesoDemo() {
        super("Acceso al Sistema - Nave Interestelar");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        UsuarioDAO dao = new ArchivoUsuarioDao();
        AutenticacionService service = new AutenticacionService(dao);
        AutenticacionController controller = new AutenticacionController(service);
        controller.setNavegacionObserver(this);
        controller.agregarObservador(this);

        JPanelLogin panelLogin = new JPanelLogin(controller);
        JPanelRegistro panelRegistro = new JPanelRegistro(controller);

        contenedor.add(panelLogin, CARD_LOGIN);
        contenedor.add(panelRegistro, CARD_REGISTRO);

        add(contenedor);
        mostrarLogin();
        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void mostrarRegistro() {
        cardLayout.show(contenedor, CARD_REGISTRO);
    }

    @Override
    public void mostrarLogin() {
        cardLayout.show(contenedor, CARD_LOGIN);
    }

    @Override
    public void onAutenticacionExitosa(UsuarioDTO usuario) {
        // Acá se puede abrir o habilitar el JFrameMain del sistema
    }

    @Override
    public void onRegistroResultado(boolean registrado, String mensaje) {
    }

    @Override
    public void onSesionCerrada() {
        mostrarLogin();
    }

    @Override
    public void onErrorAutenticacion(String mensaje) {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaAccesoDemo().setVisible(true));
    }
}
