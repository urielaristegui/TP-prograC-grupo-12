package vista.VistaPrincipal;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import Controlador.AutenticacionController;
import Controlador.MainController;
import Controlador.TripulantesController;
import modelo.UniversoAlterno.PlataformaDeCarga;
import modelo.Persistencia.UsuarioDTO;
import modelo.universo.Universo;
import vista.JPanelTablero;
import vista.PanelBitacoraYSensores;
import vista.Persistencia.NavegacionAccesoObserver;
import vista.Tripulantes.JPanelTripulantes;
import vista.Persistencia.AutenticacionObserver;
import vista.Persistencia.JPanelLogin;
import vista.VisorRecondito.JPanelVisorRecondito;
import vista.Persistencia.JPanelRegistro;
import Controlador.CreacionNaveController;
import vista.Creacion.JPanelCreacionNave;

public class JFrameMain extends JFrame implements AutenticacionObserver, NavegacionAccesoObserver {

    private static final String CARD_ACCESO = "ACCESO";
    private static final String CARD_SISTEMA = "SISTEMA";

    private final MainController mainController;
    private final AutenticacionController authController;

    private final CardLayout cardLayoutRaiz = new CardLayout();
    private final JPanel contenedorRaiz = new JPanel(cardLayoutRaiz);

    // Contenedor de Login/Registro
    private final CardLayout cardLayoutAcceso = new CardLayout();
    private final JPanel panelAccesoContainer = new JPanel(cardLayoutAcceso);

    // Panel Central del Juego
    private JTabbedPane tabbedPanePrincipal;
    private JLabel lblUsuarioHeader;

    private final Universo universo;

    public JFrameMain(MainController mainController, AutenticacionController authController, Universo universo) {
        super("Software Embarcado — Nave Interestelar (Panel Principal)");
        if (mainController == null || authController == null) {
            throw new IllegalArgumentException("Los Controladores no pueden ser nulos.");
        }
        this.mainController = mainController;
        this.authController = authController;

        this.authController.agregarObservador(this);
        this.authController.agregarObservador(this.mainController);
        this.authController.setNavegacionObserver(this);

        this.universo= universo;

        inicializarUI();
    }

    private void inicializarUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1024, 720));

        // 1. Configurar vistas de Acceso (Login y Registro)
        JPanelLogin panelLogin = new JPanelLogin(authController);
        JPanelRegistro panelRegistro = new JPanelRegistro(authController);

        panelAccesoContainer.add(panelLogin, "LOGIN");
        panelAccesoContainer.add(panelRegistro, "REGISTRO");
        cardLayoutAcceso.show(panelAccesoContainer, "LOGIN");

        // 2. Configurar Vista Principal del Juego con Pestañas
        JPanel panelSistemaConNavegacion = construirPanelSistema();

        // 3. Agregar pantallas al CardLayout raíz
        contenedorRaiz.add(panelAccesoContainer, CARD_ACCESO);
        contenedorRaiz.add(panelSistemaConNavegacion, CARD_SISTEMA);

        add(contenedorRaiz);
        cardLayoutRaiz.show(contenedorRaiz, CARD_ACCESO);
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel construirPanelSistema() {
        JPanel panelBase = new JPanel(new BorderLayout());

        // Header Superior
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(30, 35, 45));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lblTitulo = new JLabel("PANEL DE CONTROL INTERESTELAR");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 16f));

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setOpaque(false);

        lblUsuarioHeader = new JLabel("Operador: ---");
        lblUsuarioHeader.setForeground(new Color(0, 230, 120));
        lblUsuarioHeader.setFont(lblUsuarioHeader.getFont().deriveFont(Font.BOLD, 12f));

        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.addActionListener(e -> authController.cerrarSesion());

        userPanel.add(lblUsuarioHeader);
        userPanel.add(btnCerrarSesion);

        headerPanel.add(lblTitulo, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);
        panelBase.add(headerPanel, BorderLayout.NORTH);

        // TabbedPane con todas las ventanas de desarrollo
        tabbedPanePrincipal = new JTabbedPane();


// CONTROLADOR COMPARTIDO DE TRIPULANTES

        TripulantesController tripulantesController =
                new TripulantesController();

// CONTROLADOR DE CREACION DE NAVE

        CreacionNaveController creacionController =
                new CreacionNaveController(
                        universo,
                        tripulantesController
                );

// PESTANA 1 - CREACION DE NAVE

        JPanelCreacionNave panelCreacion =
                new JPanelCreacionNave(creacionController);

        tabbedPanePrincipal.addTab(
                "Configuracion & Creacion",
                panelCreacion
        );

// PESTANA 2 - TRIPULANTES Y HABERES

        JPanelTripulantes panelTripulantes =
                new JPanelTripulantes(
                        tripulantesController
                );

        tabbedPanePrincipal.addTab(
                "Tripulantes & Haberes",
                panelTripulantes
        );


        // Pestaña 3: Tablero y Control de Nave (Santi / Uriel / Agustín)
        JPanel TableroControl = new JPanel(new BorderLayout(10, 0));

        TableroControl.add(
                new JPanelTablero(universo),
                BorderLayout.CENTER
        );

        TableroControl.add(
                new PanelBitacoraYSensores(),
                BorderLayout.EAST
        );

        tabbedPanePrincipal.addTab(
                "🚀 Control de Nave & Tablero",
                TableroControl
        );

        // Pestaña 4: Visor Recóndito - Concurrencia (Jere)
        PlataformaDeCarga plataformaModelo = new PlataformaDeCarga(5);
        JPanelVisorRecondito panelVisor = new JPanelVisorRecondito(plataformaModelo.getCapacidadMaxima());
        plataformaModelo.registrarObservador(panelVisor);

        // Conectar Controlador desacoplado del visor
        new Controlador.VisorReconditoController(plataformaModelo, panelVisor);
        tabbedPanePrincipal.addTab("🌀 Visor Recóndito (Concurrencia)", panelVisor);

        panelBase.add(tabbedPanePrincipal, BorderLayout.CENTER);
        return panelBase;
    }

    private JPanel crearPanelPlaceholder(String titulo) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel lbl = new JLabel(titulo, JLabel.CENTER);
        lbl.setFont(lbl.getFont().deriveFont(Font.ITALIC, 14f));
        panel.add(lbl, BorderLayout.CENTER);
        return panel;
    }

    // --- Implementación de Observadores ---

    @Override
    public void onAutenticacionExitosa(UsuarioDTO usuario) {
        SwingUtilities.invokeLater(() -> {
            lblUsuarioHeader.setText("Operador: " + usuario.getNombreUsuario());
            cardLayoutRaiz.show(contenedorRaiz, CARD_SISTEMA);
        });
    }

    @Override
    public void onSesionCerrada() {
        SwingUtilities.invokeLater(() -> {
            lblUsuarioHeader.setText("Operador: ---");
            cardLayoutAcceso.show(panelAccesoContainer, "LOGIN");
            cardLayoutRaiz.show(contenedorRaiz, CARD_ACCESO);
        });
    }

    @Override
    public void mostrarRegistro() {
        SwingUtilities.invokeLater(() -> cardLayoutAcceso.show(panelAccesoContainer, "REGISTRO"));
    }

    @Override
    public void mostrarLogin() {
        SwingUtilities.invokeLater(() -> cardLayoutAcceso.show(panelAccesoContainer, "LOGIN"));
    }

    @Override
    public void onRegistroResultado(boolean registrado, String mensaje) {}

    @Override
    public void onErrorAutenticacion(String mensaje) {}
}
