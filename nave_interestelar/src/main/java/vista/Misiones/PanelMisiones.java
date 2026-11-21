
package vista.Misiones;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;

import Controlador.Misiones.MisionController;

import modelo.asistenteDeComandos.AsistenteDeComandos;
import modelo.bitacora.Bitacora;
import modelo.misiones.InformeMision;
import modelo.nave.Nave;

/**
 * Vista MVC para ejecutar misiones y consultar
 * los informes generados durante la ejecucion.
 *
 * Invariantes:
 * - controller != null.
 * - El historial visual corresponde al controlador.
 * - Los datos no se persisten en archivos.
 */
public class PanelMisiones extends JPanel
        implements MisionController.MisionListener {

    private final MisionController controller;

    private final JComboBox<String> comboMision;

    private final JSpinner spinnerCombustible;
    private final JSpinner spinnerEnergia;
    private final JSpinner spinnerGemas;

    private final JButton btnEjecutar;

    private final JTextArea areaInforme;
    private final JTextArea areaBitacora;

    private final JLabel lblEstadoNave;
    private final JLabel lblCantidadInformes;

    private final DefaultListModel<String> modeloHistorial;
    private final JList<String> listaHistorial;

    /**
     * Constructor para pruebas independientes.
     *
     * Pre: asistente y bitacora no nulos.
     * Post: utiliza la nave del asistente.
     */
    public PanelMisiones(
            AsistenteDeComandos asistente,
            Bitacora bitacora) {

        this(asistente, bitacora, null);
    }

    /**
     * Construye el panel con historial.
     *
     * Pre:
     * - asistente != null.
     * - bitacora != null.
     *
     * Post:
     * - Inicializa los componentes.
     * - Conecta las acciones al controlador.
     * - Inicializa el historial vacio.
     *
     * @param asistente asistente de comandos
     * @param bitacora bitacora compartida
     * @param proveedorNave nave creada por el usuario
     */
    public PanelMisiones(
            AsistenteDeComandos asistente,
            Bitacora bitacora,
            Supplier<Nave> proveedorNave) {

        controller = new MisionController(
                asistente,
                bitacora,
                this,
                proveedorNave
        );

        setLayout(new BorderLayout(10, 10));

        setBorder(
                BorderFactory.createEmptyBorder(
                        10, 10, 10, 10
                )
        );

        // =====================================
        // PANEL SUPERIOR - MISIONES
        // =====================================

        JPanel panelControles = new JPanel(
                new GridLayout(2, 1, 6, 6)
        );

        JPanel filaSeleccion = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        comboMision = new JComboBox<>(
                new String[]{
                        "Intercepcion (M-01)",
                        "Recoleccion (M-02)",
                        "Retorno (M-03)"
                }
        );

        btnEjecutar = new JButton(
                "Ejecutar misión"
        );

        lblEstadoNave = new JLabel(
                "Estado: " + controller.obtenerEstadoNave()
        );

        filaSeleccion.add(
                new JLabel("Misión:")
        );

        filaSeleccion.add(comboMision);
        filaSeleccion.add(btnEjecutar);
        filaSeleccion.add(lblEstadoNave);

        // =====================================
        // PARAMETROS
        // =====================================

        JPanel filaParametros = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        spinnerCombustible = new JSpinner(
                new SpinnerNumberModel(10, 0, 50, 1)
        );

        spinnerEnergia = new JSpinner(
                new SpinnerNumberModel(10, 0, 50, 1)
        );

        spinnerGemas = new JSpinner(
                new SpinnerNumberModel(50, 20, 100, 5)
        );

        filaParametros.add(
                new JLabel("Combustible M-01:")
        );

        filaParametros.add(spinnerCombustible);

        filaParametros.add(
                new JLabel("Energía M-01:")
        );

        filaParametros.add(spinnerEnergia);

        filaParametros.add(
                new JLabel("Gemas M-02:")
        );

        filaParametros.add(spinnerGemas);

        panelControles.add(filaSeleccion);
        panelControles.add(filaParametros);

        add(panelControles, BorderLayout.NORTH);

        // =====================================
        // HISTORIAL DE MISIONES
        // =====================================

        modeloHistorial =
                new DefaultListModel<>();

        listaHistorial =
                new JList<>(modeloHistorial);

        listaHistorial.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scrollHistorial =
                new JScrollPane(listaHistorial);

        scrollHistorial.setBorder(
                BorderFactory.createTitledBorder(
                        "Historial de misiones"
                )
        );

        scrollHistorial.setPreferredSize(
                new Dimension(240, 250)
        );

        lblCantidadInformes = new JLabel(
                "Informes guardados: 0"
        );

        JPanel panelHistorial = new JPanel(
                new BorderLayout(5, 5)
        );

        panelHistorial.add(
                scrollHistorial,
                BorderLayout.CENTER
        );

        panelHistorial.add(
                lblCantidadInformes,
                BorderLayout.SOUTH
        );

        // =====================================
        // INFORME SELECCIONADO
        // =====================================

        areaInforme = new JTextArea(12, 40);

        areaInforme.setEditable(false);
        areaInforme.setLineWrap(true);
        areaInforme.setWrapStyleWord(true);

        areaInforme.setText(
                "Todavia no se ejecutaron misiones.\n"
        );

        JScrollPane scrollInforme =
                new JScrollPane(areaInforme);

        scrollInforme.setBorder(
                BorderFactory.createTitledBorder(
                        "Informe seleccionado"
                )
        );

        // =====================================
        // BITACORA
        // =====================================

        areaBitacora = new JTextArea(10, 40);
        areaBitacora.setEditable(false);

        JScrollPane scrollBitacora =
                new JScrollPane(areaBitacora);

        scrollBitacora.setBorder(
                BorderFactory.createTitledBorder(
                        "Bitácora de eventos"
                )
        );

        // =====================================
        // DISTRIBUCION DE PANELES
        // =====================================

        JSplitPane divisionVertical = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                scrollInforme,
                scrollBitacora
        );

        divisionVertical.setResizeWeight(0.55);
        divisionVertical.setBorder(null);

        JSplitPane divisionHorizontal = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                panelHistorial,
                divisionVertical
        );

        divisionHorizontal.setResizeWeight(0.26);
        divisionHorizontal.setBorder(null);

        add(
                divisionHorizontal,
                BorderLayout.CENTER
        );

        // =====================================
        // LISTENERS
        // =====================================

        actualizarCamposPorMision();

        comboMision.addActionListener(
                e -> actualizarCamposPorMision()
        );

        btnEjecutar.addActionListener(e -> {

            controller.ejecutarMision(
                    comboMision.getSelectedIndex(),
                    (Integer) spinnerCombustible.getValue(),
                    (Integer) spinnerEnergia.getValue(),
                    (Integer) spinnerGemas.getValue()
            );
        });

        // Mostrar el informe elegido del historial.
        listaHistorial.addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {

                int indice =
                        listaHistorial.getSelectedIndex();

                List<InformeMision> informes =
                        controller.obtenerHistorial();

                if (indice >= 0
                        && indice < informes.size()) {

                    mostrarInforme(
                            informes.get(indice)
                    );
                }
            }
        });
    }

    /**
     * Actualiza el estado visual de la nave.
     *
     * Pre: panel inicializado.
     * Post: muestra el estado actual sin modificarlo.
     */
    public void refrescarEstadoNave() {

        lblEstadoNave.setText(
                "Estado: "
                        + controller.obtenerEstadoNave()
        );
    }

    /**
     * Habilita solo los parametros relevantes.
     *
     * Pre: comboMision inicializado.
     * Post: actualiza los controles habilitados.
     */
    private void actualizarCamposPorMision() {

        int indice =
                comboMision.getSelectedIndex();

        spinnerCombustible.setEnabled(
                indice == 0
        );

        spinnerEnergia.setEnabled(
                indice == 0
        );

        spinnerGemas.setEnabled(
                indice == 1
        );
    }

    /**
     * Muestra los datos de un informe.
     *
     * Pre: informe != null.
     *
     * Post:
     * - Actualiza el texto del panel.
     * - No modifica el historial.
     * - No modifica la nave.
     *
     * @param informe informe a mostrar
     */
    private void mostrarInforme(
            InformeMision informe) {

        StringBuilder texto =
                new StringBuilder();

        texto.append("Misión: ")
                .append(informe.getMisionEjecutada())
                .append("\n");

        texto.append("Resultado: ")
                .append(informe.getResultado())
                .append("\n");

        texto.append("Balance de recursos: ")
                .append(informe.getRecursosConsumidos())
                .append("\n");

        texto.append("Estado final de la nave: ")
                .append(informe.getEstadoNave())
                .append("\n");

        texto.append("Observaciones: ")
                .append(informe.getObservaciones())
                .append("\n\n");

        texto.append("Acciones principales:\n");

        for (String accion :
                informe.getAccionesPrincipales()) {

            texto.append(" - ")
                    .append(accion)
                    .append("\n");
        }

        areaInforme.setText(
                texto.toString()
        );

        areaInforme.setCaretPosition(0);
    }

    /**
     * Notifica el inicio de una mision.
     *
     * Pre: la mision fue validada.
     *
     * Post:
     * - Deshabilita el boton.
     * - Conserva el historial.
     */
    @Override
    public void onMisionIniciada() {

        btnEjecutar.setEnabled(false);

        areaInforme.setText(
                "Ejecutando misión...\n"
        );
    }

    /**
     * Actualiza el panel al finalizar una mision.
     *
     * Pre:
     * - El controlador termino su trabajo.
     * - La llamada se realiza en el EDT.
     *
     * Post:
     * - Incorpora el informe al historial visual.
     * - Muestra el informe mas reciente.
     * - Actualiza bitacora y recursos.
     * - Habilita el boton.
     */
    @Override
    public void onMisionFinalizada(
            boolean pasoPreparacion,
            InformeMision informe,
            List<String> eventosBitacora,
            String estadoNave) {

        lblEstadoNave.setText(
                "Estado: " + estadoNave
        );

        // =====================================
        // AGREGAR INFORME AL HISTORIAL
        // =====================================

        if (informe != null) {

            int numero =
                    modeloHistorial.size() + 1;

            modeloHistorial.addElement(
                    numero + ". "
                            + informe.getMisionEjecutada()
                            + " - "
                            + informe.getResultado()
            );

            lblCantidadInformes.setText(
                    "Informes guardados: " + numero
            );

            // Seleccionar automaticamente el ultimo.
            listaHistorial.setSelectedIndex(
                    numero - 1
            );

            listaHistorial.ensureIndexIsVisible(
                    numero - 1
            );

        } else {

            areaInforme.setText(
                    "La misión no pudo generar un informe.\n"
            );
        }

        // =====================================
        // ACTUALIZAR BITACORA
        // =====================================

        StringBuilder eventos =
                new StringBuilder();

        for (String evento : eventosBitacora) {

            eventos.append(evento)
                    .append("\n");
        }

        areaBitacora.setText(
                eventos.toString()
        );

        areaBitacora.setCaretPosition(0);

        btnEjecutar.setEnabled(true);
    }

    /**
     * Informa errores de validacion.
     *
     * Pre: mensaje != null.
     * Post: muestra una advertencia al usuario.
     */
    @Override
    public void onErrorParametro(
            String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Parámetro inválido",
                JOptionPane.WARNING_MESSAGE
        );
    }
}
