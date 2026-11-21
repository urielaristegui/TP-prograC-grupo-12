
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
import Controlador.CreacionNaveController;

import modelo.UniversoAlterno.PlataformaDeCarga;
import modelo.Persistencia.UsuarioDTO;
import modelo.universo.Universo;
import modelo.bitacora.Bitacora;
import modelo.asistenteDeComandos.AsistenteDeComandos;
import modelo.sensores.ReglasNavegacion;
import modelo.sensores.Sensores;

import vista.Misiones.PanelMisiones;
import vista.JPanelTablero;
import vista.PanelBitacoraYSensores;
import vista.Persistencia.NavegacionAccesoObserver;
import vista.Persistencia.AutenticacionObserver;
import vista.Persistencia.JPanelLogin;
import vista.Persistencia.JPanelRegistro;
import vista.Tripulantes.JPanelTripulantes;
import vista.VisorRecondito.JPanelVisorRecondito;
import vista.Creacion.JPanelCreacionNave;

/**
 * Ventana principal del sistema.
 *
 * Coordina las pantallas de autenticacion
 * y las secciones de la nave.
 *
 * Invariantes:
 * - mainController != null.
 * - authController != null.
 * - universo != null.
 * - Las secciones utilizan el mismo universo.
 */
public class JFrameMain extends JFrame
        implements AutenticacionObserver,
        NavegacionAccesoObserver {

    private static final String CARD_ACCESO =
            "ACCESO";

    private static final String CARD_SISTEMA =
            "SISTEMA";

    private final MainController mainController;
    private final AutenticacionController authController;

    private final CardLayout cardLayoutRaiz =
            new CardLayout();

    private final JPanel contenedorRaiz =
            new JPanel(cardLayoutRaiz);

    private final CardLayout cardLayoutAcceso =
            new CardLayout();

    private final JPanel panelAccesoContainer =
            new JPanel(cardLayoutAcceso);

    private JTabbedPane tabbedPanePrincipal;
    private JLabel lblUsuarioHeader;

    private final Universo universo;

    /**
     * Construye la ventana principal.
     *
     * Pre: controladores y universo no nulos.
     *
     * Post:
     * - Registra observadores.
     * - Inicializa login y registro.
     * - Inicializa las secciones del sistema.
     *
     * @param mainController controlador principal
     * @param authController controlador de autenticacion
     * @param universo universo del jugador
     */
    public JFrameMain(
            MainController mainController,
            AutenticacionController authController,
            Universo universo) {

        super(
                "Software Embarcado - Nave Interestelar"
        );

        if (mainController == null
                || authController == null
                || universo == null) {

            throw new IllegalArgumentException(
                    "Controladores y universo son obligatorios"
            );
        }

        this.mainController = mainController;
        this.authController = authController;
        this.universo = universo;

        this.authController.agregarObservador(this);

        this.authController.agregarObservador(
                this.mainController
        );

        this.authController.setNavegacionObserver(
                this
        );

        inicializarUI();
    }

    /**
     * Inicializa los componentes principales.
     *
     * Pre: referencias del constructor validas.
     *
     * Post:
     * - Configura la ventana.
     * - Crea las pantallas.
     * - Muestra el acceso.
     */
    private void inicializarUI() {

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setMinimumSize(
                new Dimension(1024, 720)
        );

        // ==========================================
        // LOGIN Y REGISTRO
        // ==========================================

        JPanelLogin panelLogin =
                new JPanelLogin(authController);

        JPanelRegistro panelRegistro =
                new JPanelRegistro(authController);

        panelAccesoContainer.add(
                panelLogin,
                "LOGIN"
        );

        panelAccesoContainer.add(
                panelRegistro,
                "REGISTRO"
        );

        cardLayoutAcceso.show(
                panelAccesoContainer,
                "LOGIN"
        );

        // ==========================================
        // SISTEMA
        // ==========================================

        JPanel panelSistema =
                construirPanelSistema();

        contenedorRaiz.add(
                panelAccesoContainer,
                CARD_ACCESO
        );

        contenedorRaiz.add(
                panelSistema,
                CARD_SISTEMA
        );

        add(contenedorRaiz);

        cardLayoutRaiz.show(
                contenedorRaiz,
                CARD_ACCESO
        );

        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Construye el panel principal.
     *
     * Pre: universo inicializado.
     *
     * Post:
     * - Crea cinco pestañas.
     * - Comparte el mismo universo.
     * - Conecta creacion, misiones y tablero.
     *
     * @return panel principal
     */
    private JPanel construirPanelSistema() {

        JPanel panelBase =
                new JPanel(new BorderLayout());

        // ==========================================
        // ENCABEZADO
        // ==========================================

        JPanel headerPanel =
                new JPanel(new BorderLayout());

        headerPanel.setBackground(
                new Color(30, 35, 45)
        );

        headerPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        10, 15, 10, 15
                )
        );

        JLabel lblTitulo = new JLabel(
                "PANEL DE CONTROL INTERESTELAR"
        );

        lblTitulo.setForeground(Color.WHITE);

        lblTitulo.setFont(
                lblTitulo.getFont().deriveFont(
                        Font.BOLD,
                        16f
                )
        );

        JPanel userPanel = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT, 10, 0
                )
        );

        userPanel.setOpaque(false);

        lblUsuarioHeader = new JLabel(
                "Operador: ---"
        );

        lblUsuarioHeader.setForeground(
                new Color(0, 230, 120)
        );

        lblUsuarioHeader.setFont(
                lblUsuarioHeader.getFont().deriveFont(
                        Font.BOLD,
                        12f
                )
        );

        JButton btnCerrarSesion =
                new JButton("Cerrar Sesión");

        btnCerrarSesion.addActionListener(
                e -> authController.cerrarSesion()
        );

        userPanel.add(lblUsuarioHeader);
        userPanel.add(btnCerrarSesion);

        headerPanel.add(
                lblTitulo,
                BorderLayout.WEST
        );

        headerPanel.add(
                userPanel,
                BorderLayout.EAST
        );

        panelBase.add(
                headerPanel,
                BorderLayout.NORTH
        );

        // ==========================================
        // PESTAÑAS DEL SISTEMA
        // ==========================================

        tabbedPanePrincipal = new JTabbedPane();

        // Controlador compartido de tripulantes.
        TripulantesController tripulantesController =
                new TripulantesController();

        // Controlador de creacion.
        CreacionNaveController creacionController =
                new CreacionNaveController(
                        universo,
                        tripulantesController
                );

        // ==========================================
        // PESTAÑA 1 - CREACION
        // ==========================================

        JPanelCreacionNave panelCreacion =
                new JPanelCreacionNave(
                        creacionController
                );

        tabbedPanePrincipal.addTab(
                "Configuracion & Creacion",
                panelCreacion
        );

        // ==========================================
        // PESTAÑA 2 - TRIPULANTES
        // ==========================================

        JPanelTripulantes panelTripulantes =
                new JPanelTripulantes(
                        tripulantesController
                );

        tabbedPanePrincipal.addTab(
                "Tripulantes & Haberes",
                panelTripulantes
        );

        // ==========================================
        // PESTAÑA 3 - TABLERO
        // ==========================================

        JPanel tableroControl = new JPanel(
                new BorderLayout(10, 0)
        );

        tableroControl.add(
                new JPanelTablero(universo),
                BorderLayout.CENTER
        );

        tableroControl.add(
                new PanelBitacoraYSensores(),
                BorderLayout.EAST
        );

        tabbedPanePrincipal.addTab(
                "Control de Nave & Tablero",
                tableroControl
        );

        // ==========================================
        // PESTAÑA 4 - MISIONES
        // ==========================================

        // Compartir la misma Bitacora.
        Bitacora bitacora =
                Bitacora.getInstance();

        // Crear asistente sobre el universo existente.

        AsistenteDeComandos asistente =
                new AsistenteDeComandos(
                        universo.getNave(),
                        bitacora,
                        universo.getTablero()
                );

        // NUEVO: conectar los objetivos de las misiones
        // con el universo que utiliza JPanelTablero.
        asistente.setNotificadorObjetivo(
                universo::actualizarObjetivoMision
        );

        // Mantener las reglas de navegacion existentes.
        asistente.setReglasNavegacion(
                new ReglasNavegacion(
                        new Sensores(universo.getTablero())
                )
        );


        // El proveedor devuelve la nave creada
        // por el usuario, no la nave temporal.
        PanelMisiones panelMisiones =
                new PanelMisiones(
                        asistente,
                        bitacora,
                        creacionController::getNave
                );

        tabbedPanePrincipal.addTab(
                "Misiones",
                panelMisiones
        );

        // Actualizar el estado al abrir la pestaña.
        tabbedPanePrincipal.addChangeListener(e -> {

            if (tabbedPanePrincipal.getSelectedComponent()
                    == panelMisiones) {

                panelMisiones.refrescarEstadoNave();
            }
        });

        // ==========================================
        // PESTAÑA 5 - CONCURRENCIA
        // ==========================================

        PlataformaDeCarga plataformaModelo =
                new PlataformaDeCarga(5);

        JPanelVisorRecondito panelVisor =
                new JPanelVisorRecondito(
                        plataformaModelo.getCapacidadMaxima()
                );

        plataformaModelo.registrarObservador(
                panelVisor
        );

        new Controlador.VisorReconditoController(
                plataformaModelo,
                panelVisor
        );

        tabbedPanePrincipal.addTab(
                "Visor Recondito (Concurrencia)",
                panelVisor
        );

        // ==========================================
        // FINALIZAR PANEL
        // ==========================================

        panelBase.add(
                tabbedPanePrincipal,
                BorderLayout.CENTER
        );

        return panelBase;
    }

    /**
     * Notifica una autenticacion exitosa.
     *
     * Pre: usuario != null.
     * Post: muestra el sistema principal.
     */
    @Override
    public void onAutenticacionExitosa(
            UsuarioDTO usuario) {

        SwingUtilities.invokeLater(() -> {

            lblUsuarioHeader.setText(
                    "Operador: "
                            + usuario.getNombreUsuario()
            );

            cardLayoutRaiz.show(
                    contenedorRaiz,
                    CARD_SISTEMA
            );
        });
    }

    /**
     * Notifica el cierre de sesion.
     *
     * Pre: ninguna.
     * Post: vuelve al formulario de login.
     */
    @Override
    public void onSesionCerrada() {

        SwingUtilities.invokeLater(() -> {

            lblUsuarioHeader.setText(
                    "Operador: ---"
            );

            cardLayoutAcceso.show(
                    panelAccesoContainer,
                    "LOGIN"
            );

            cardLayoutRaiz.show(
                    contenedorRaiz,
                    CARD_ACCESO
            );
        });
    }

    /**
     * Cambia a la pantalla de registro.
     *
     * Pre: ninguna.
     * Post: muestra el formulario de registro.
     */
    @Override
    public void mostrarRegistro() {

        SwingUtilities.invokeLater(() -> {

            cardLayoutAcceso.show(
                    panelAccesoContainer,
                    "REGISTRO"
            );
        });
    }

    /**
     * Cambia a la pantalla de login.
     *
     * Pre: ninguna.
     * Post: muestra el formulario de login.
     */
    @Override
    public void mostrarLogin() {

        SwingUtilities.invokeLater(() -> {

            cardLayoutAcceso.show(
                    panelAccesoContainer,
                    "LOGIN"
            );
        });
    }

    @Override
    public void onRegistroResultado(
            boolean registrado,
            String mensaje) {
    }

    @Override
    public void onErrorAutenticacion(
            String mensaje) {
    }
}
