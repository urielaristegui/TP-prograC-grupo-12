
package vista.Bitacora;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import modelo.bitacora.Bitacora;
import modelo.bitacora.IObservadorBitacora;
import modelo.destinoytablero.Observador;
import modelo.destinoytablero.Tablero;
import modelo.nave.Nave;

/**
 * Panel de control que muestra los recursos de la nave,
 * los sensores y la bitacora de eventos.
 *
 * Implementa Observer para actualizar automaticamente
 * los recursos durante las misiones.
 *
 * Invariantes:
 * - proveedorNave != null.
 * - Los componentes Swing estan inicializados.
 * - Las actualizaciones graficas se realizan en el EDT.
 */
public class PanelBitacoraYSensores extends JPanel
        implements IObservadorBitacora, Observador {

    private final Supplier<Nave> proveedorNave;

    private final JLabel lblNave;
    private final JLabel lblGemas;

    private final JProgressBar barraCombustible;
    private final JProgressBar barraEnergia;
    private final JProgressBar barraDesgaste;

    private final JLabel lblEstadoSensor;
    private final JLabel lblLecturaProximaCelda;

    private final JTextArea txtBitacora;

    /**
     * Construye el panel de control.
     *
     * Pre:
     * - tablero != null.
     * - proveedorNave != null.
     *
     * Post:
     * - Inicializa los componentes visuales.
     * - Se registra como observador del tablero.
     * - Se registra como observador de la bitacora.
     * - Muestra el estado inicial de la nave.
     *
     * @param tablero tablero compartido del universo
     * @param proveedorNave obtiene la nave del jugador
     */
    public PanelBitacoraYSensores(
            Tablero tablero,
            Supplier<Nave> proveedorNave) {

        if (tablero == null || proveedorNave == null) {

            throw new IllegalArgumentException(
                    "Tablero y proveedor de nave son obligatorios"
            );
        }

        this.proveedorNave = proveedorNave;

        setLayout(new BorderLayout(8, 8));

        setPreferredSize(
                new Dimension(320, 600)
        );

        setBorder(
                BorderFactory.createTitledBorder(
                        "Control de nave, bitacora y sensores"
                )
        );

        // =====================================
        // PANEL DE RECURSOS
        // =====================================

        JPanel panelRecursos = new JPanel(
                new GridLayout(5, 1, 0, 5)
        );

        panelRecursos.setBorder(
                BorderFactory.createTitledBorder(
                        "Recursos en tiempo real"
                )
        );

        lblNave = new JLabel(
                "Primero crea una nave"
        );

        lblNave.setFont(
                lblNave.getFont().deriveFont(Font.BOLD)
        );

        barraCombustible = crearBarra();
        barraEnergia = crearBarra();
        barraDesgaste = crearBarra();

        lblGemas = new JLabel("Gemas: -");

        panelRecursos.add(lblNave);
        panelRecursos.add(barraCombustible);
        panelRecursos.add(barraEnergia);
        panelRecursos.add(barraDesgaste);
        panelRecursos.add(lblGemas);

        // =====================================
        // PANEL DE SENSORES
        // =====================================

        JPanel panelSensores = new JPanel(
                new GridLayout(2, 1, 5, 5)
        );

        panelSensores.setBorder(
                BorderFactory.createTitledBorder(
                        "Sensores"
                )
        );

        lblEstadoSensor = new JLabel(
                "Radar de corto alcance: ACTIVO"
        );

        lblLecturaProximaCelda = new JLabel(
                "Proxima celda: Escaneando..."
        );

        panelSensores.add(lblEstadoSensor);
        panelSensores.add(lblLecturaProximaCelda);

        // =====================================
        // CONTENEDOR SUPERIOR
        // =====================================

        JPanel panelSuperior = new JPanel();

        panelSuperior.setLayout(
                new BoxLayout(
                        panelSuperior,
                        BoxLayout.Y_AXIS
                )
        );

        panelRecursos.setAlignmentX(LEFT_ALIGNMENT);
        panelSensores.setAlignmentX(LEFT_ALIGNMENT);

        panelSuperior.add(panelRecursos);

        panelSuperior.add(
                Box.createVerticalStrut(6)
        );

        panelSuperior.add(panelSensores);

        // =====================================
        // BITACORA
        // =====================================

        txtBitacora = new JTextArea(12, 26);

        txtBitacora.setEditable(false);

        txtBitacora.setFont(
                new Font(
                        Font.MONOSPACED,
                        Font.PLAIN,
                        11
                )
        );

        txtBitacora.setLineWrap(true);
        txtBitacora.setWrapStyleWord(true);

        JScrollPane scrollBitacora =
                new JScrollPane(txtBitacora);

        scrollBitacora.setBorder(
                BorderFactory.createTitledBorder(
                        "Bitacora de vuelo"
                )
        );

        add(
                panelSuperior,
                BorderLayout.NORTH
        );

        add(
                scrollBitacora,
                BorderLayout.CENTER
        );

        // =====================================
        // REGISTRO DE OBSERVADORES
        // =====================================

        tablero.agregarObservador(this);

        Bitacora.getInstance().agregarObservador(
                this
        );

        refrescarRecursos();
    }

    /**
     * Construye una barra de recursos.
     *
     * Pre: ninguna.
     *
     * Post:
     * - Inicializa el rango entre 0 y 100.
     * - Habilita el texto sobre la barra.
     *
     * @return barra inicializada
     */
    private JProgressBar crearBarra() {

        JProgressBar barra =
                new JProgressBar(0, 100);

        barra.setStringPainted(true);
        barra.setValue(0);

        return barra;
    }

    /**
     * Actualiza los recursos mostrados.
     *
     * Pre:
     * - El panel esta inicializado.
     *
     * Post:
     * - Muestra la nave realmente creada.
     * - Actualiza combustible, energia y desgaste.
     * - Actualiza las gemas.
     * - Informa si la nave esta destruida.
     * - No modifica el modelo.
     */
    public void refrescarRecursos() {

        if (!SwingUtilities.isEventDispatchThread()) {

            SwingUtilities.invokeLater(
                    this::refrescarRecursos
            );

            return;
        }

        Nave nave = proveedorNave.get();

        // =====================================
        // TODAVIA NO SE CREO UNA NAVE
        // =====================================

        if (nave == null) {

            lblNave.setText(
                    "Primero crea una nave"
            );

            actualizarBarra(
                    barraCombustible,
                    "Combustible",
                    0
            );

            actualizarBarra(
                    barraEnergia,
                    "Energia",
                    0
            );

            actualizarBarra(
                    barraDesgaste,
                    "Desgaste",
                    0
            );

            lblGemas.setText("Gemas: -");

            return;
        }

        // =====================================
        // ESTADO GENERAL
        // =====================================

        lblNave.setText(
                "Nave #" + nave.getId()
                        + (nave.estaDestruida()
                        ? " - DESTRUIDA"
                        : " - OPERATIVA")
        );

        // =====================================
        // RECURSOS ACTUALES
        // =====================================

        actualizarBarra(
                barraCombustible,
                "Combustible",
                nave.getCombustible()
        );

        actualizarBarra(
                barraEnergia,
                "Energia",
                nave.getEnergia()
        );

        actualizarBarra(
                barraDesgaste,
                "Desgaste",
                nave.getDesgaste()
        );

        lblGemas.setText(
                "Gemas: " + nave.getGemas()
        );
    }

    /**
     * Actualiza una barra con el valor del recurso.
     *
     * Pre:
     * - barra != null.
     * - nombre != null.
     *
     * Post:
     * - Actualiza el valor de la barra.
     * - Muestra numericamente el recurso.
     *
     * @param barra componente visual
     * @param nombre nombre del recurso
     * @param valor cantidad actual
     */
    private void actualizarBarra(
            JProgressBar barra,
            String nombre,
            int valor) {

        barra.setValue(
                Math.max(0, Math.min(100, valor))
        );

        barra.setString(
                nombre + ": " + valor + "/100"
        );
    }

    /**
     * Recibe notificaciones del tablero.
     *
     * Pre: ninguna.
     *
     * Post:
     * - Programa la actualizacion de recursos.
     * - No modifica el modelo.
     */
    @Override
    public void actualizar() {

        SwingUtilities.invokeLater(
                this::refrescarRecursos
        );
    }

    /**
     * Recibe eventos de la bitacora.
     *
     * Pre: evento != null.
     *
     * Post:
     * - Agrega el evento al registro visual.
     * - Actualiza el ultimo evento de sensores.
     * - Refresca los recursos actuales.
     * - Ejecuta los cambios de Swing en el EDT.
     *
     * @param evento mensaje recibido
     */
    @Override
    public void actualizarBitacora(
            String evento) {

        SwingUtilities.invokeLater(() -> {

            txtBitacora.append(
                    evento + "\n"
            );

            txtBitacora.setCaretPosition(
                    txtBitacora.getDocument().getLength()
            );

            if (evento.contains("Sensor:")
                    || evento.contains("Navegacion:")) {

                String resumen = evento.length() > 38
                        ? evento.substring(0, 35) + "..."
                        : evento;

                lblLecturaProximaCelda.setText(
                        resumen
                );

                lblLecturaProximaCelda.setToolTipText(
                        evento
                );
            }

            refrescarRecursos();
        });
    }
}
