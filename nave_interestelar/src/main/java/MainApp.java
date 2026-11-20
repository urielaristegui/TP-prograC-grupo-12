import java.nio.file.Paths;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import Controlador.AutenticacionController;
import Controlador.MainController;
import modelo.Persistencia.ArchivoUsuarioDao;
import modelo.Persistencia.AutenticacionService;
import modelo.Persistencia.UsuarioDAO;
import vista.VistaPrincipal.JFrameMain;

public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            // Capa de Persistencia y Servicios
            UsuarioDAO usuarioDAO = new ArchivoUsuarioDao(Paths.get("target/usuarios.txt"));
            AutenticacionService authService = new AutenticacionService(usuarioDAO);

            // Capa de Controladores
            MainController mainController = new MainController(authService);
            AutenticacionController authController = new AutenticacionController(authService);

            // Vista Principal
            JFrameMain ventanaPrincipal = new JFrameMain(mainController, authController);
            ventanaPrincipal.setVisible(true);
        });
    }
}