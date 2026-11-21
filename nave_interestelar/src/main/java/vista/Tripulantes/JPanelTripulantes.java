
package vista.Tripulantes;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import Controlador.TripulantesController;
import modelo.Tripulacion.Tripulante;

/**
 * Vista MVC de tripulantes, origen y haberes.
 *
 * Invariantes:
 * - El controlador no es nulo.
 * - La lista visible respeta el filtro seleccionado.
 * - Los haberes se calculan en el modelo.
 */
public class JPanelTripulantes extends JPanel
        implements TripulacionObserver {

    private final TripulantesController controlador;

    private final DefaultTableModel modeloTabla;
    private final JTable tablaTripulantes;

    private final JComboBox<String> comboCargo;
    private final JButton btnRegistrarConsejo;

    private final JTextArea areaDetalle;
    private final JLabel lblTotalHaberes;

    private List<Tripulante> tripulantesVisibles =
            new ArrayList<>();

    /**
     * Construye la interfaz de tripulantes.
     *
     * Pre: controlador != null.
     *
     * Post:
     * - Inicializa los componentes.
     * - Registra los listeners.
     * - Registra la vista como observadora.
     *
     * @param controlador controlador de tripulantes
     */
    public JPanelTripulantes(
            TripulantesController controlador) {

        if (controlador == null) {

            throw new IllegalArgumentException(
                    "El controlador no puede ser nulo"
            );
        }

        this.controlador = controlador;

        setLayout(new BorderLayout(10, 10));

        setBorder(
                BorderFactory.createEmptyBorder(
                        12, 12, 12, 12
                )
        );

        // =====================================
        // ENCABEZADO
        // =====================================

        JPanel superior = new JPanel(
                new BorderLayout(8, 8)
        );

        JLabel titulo = new JLabel(
                "Tripulacion y Haberes"
        );

        titulo.setFont(
                titulo.getFont().deriveFont(
                        Font.BOLD, 16f
                )
        );

        superior.add(
                titulo,
                BorderLayout.WEST
        );

        // =====================================
        // FILTROS Y CONTROLES
        // =====================================

        JPanel controles = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 0)
        );

        controles.add(
                new JLabel("Cargo:")
        );

        comboCargo = new JComboBox<>(
                new String[]{
                        "Todos",
                        "Capitán",
                        "Consejero",
                        "Teniente",
                        "Alférez"
                }
        );

        controles.add(comboCargo);

        btnRegistrarConsejo = new JButton(
                "Registrar consejo (+2 PG)"
        );

        btnRegistrarConsejo.setEnabled(false);

        controles.add(btnRegistrarConsejo);

        JButton btnActualizar = new JButton(
                "Actualizar"
        );

        controles.add(btnActualizar);

        superior.add(
                controles,
                BorderLayout.EAST
        );

        add(
                superior,
                BorderLayout.NORTH
        );

        // =====================================
        // TABLA DE TRIPULANTES
        // =====================================

        modeloTabla = new DefaultTableModel(
                new String[]{
                        "Identidad",
                        "Cargo",
                        "Origen",
                        "Antiguedad (años)",
                        "Haber (PG)"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(
                    int fila,
                    int columna) {

                return false;
            }
        };

        tablaTripulantes = new JTable(
                modeloTabla
        );

        tablaTripulantes.setRowHeight(24);

        tablaTripulantes.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tablaTripulantes.getTableHeader()
                .setReorderingAllowed(false);

        JScrollPane scrollTabla =
                new JScrollPane(tablaTripulantes);

        scrollTabla.setBorder(
                BorderFactory.createTitledBorder(
                        "Tripulantes"
                )
        );

        // =====================================
        // DETALLE DEL TRIPULANTE
        // =====================================

        areaDetalle = new JTextArea(6, 40);

        areaDetalle.setEditable(false);

        areaDetalle.setFont(
                new Font(
                        Font.MONOSPACED,
                        Font.PLAIN,
                        12
                )
        );

        JScrollPane scrollDetalle =
                new JScrollPane(areaDetalle);

        scrollDetalle.setBorder(
                BorderFactory.createTitledBorder(
                        "Detalle del tripulante"
                )
        );

        // =====================================
        // DISTRIBUCION CENTRAL
        // =====================================

        JSplitPane split = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                scrollTabla,
                scrollDetalle
        );

        split.setResizeWeight(0.65);

        add(
                split,
                BorderLayout.CENTER
        );

        // =====================================
        // TOTAL DE HABERES
        // =====================================

        lblTotalHaberes = new JLabel(
                "Total haberes: 0.00 PG | Tripulantes: 0",
                SwingConstants.RIGHT
        );

        lblTotalHaberes.setFont(
                lblTotalHaberes.getFont().deriveFont(
                        Font.BOLD, 12f
                )
        );

        add(
                lblTotalHaberes,
                BorderLayout.SOUTH
        );

        // =====================================
        // LISTENERS
        // =====================================

        // Filtrar tripulantes por cargo.
        comboCargo.addActionListener(e -> {

            controlador.filtrarPorCargo(
                    (String) comboCargo.getSelectedItem()
            );
        });

        // Actualizar la tabla manualmente.
        btnActualizar.addActionListener(
                e -> controlador.refrescar()
        );

        // Mostrar detalle del tripulante.
        tablaTripulantes.getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()) {
                        actualizarDetalle();
                    }
                });

        // Registrar consejo en el modelo.
        btnRegistrarConsejo.addActionListener(e -> {

            int fila =
                    tablaTripulantes.getSelectedRow();

            if (fila >= 0
                    && fila < tripulantesVisibles.size()) {

                controlador.registrarConsejo(
                        tripulantesVisibles.get(fila)
                );
            }
        });

        // Registrar la vista en el controlador.
        controlador.setVista(this);
    }

    /**
     * Actualiza el detalle del tripulante seleccionado.
     *
     * Pre: la tabla esta inicializada.
     *
     * Post:
     * - Muestra identidad, cargo y origen.
     * - Muestra sueldo con Decorator.
     * - Incluye los consejos sin duplicar el bono.
     * - Habilita registrar consejos solo
     *   cuando corresponde.
     */
    private void actualizarDetalle() {

        int fila =
                tablaTripulantes.getSelectedRow();

        if (fila < 0
                || fila >= tripulantesVisibles.size()) {

            btnRegistrarConsejo.setEnabled(false);

            areaDetalle.setText("");

            return;
        }

        Tripulante t =
                tripulantesVisibles.get(fila);

        boolean esConsejero =
                "Consejero".equalsIgnoreCase(
                        t.getCargo()
                );

        btnRegistrarConsejo.setEnabled(
                esConsejero
        );

        // El modelo ya incluye el subsidio
        // del origen y los consejos registrados.
        double haber =
                controlador.calcularHaberTotal(t);

        int consejos = esConsejero
                ? controlador.getConsejos(t)
                : 0;

        // =====================================
        // ARMAR DETALLE
        // =====================================

        StringBuilder detalle =
                new StringBuilder();

        detalle.append("Identidad: ")
                .append(t.getID())
                .append('\n');

        detalle.append("Cargo: ")
                .append(t.getCargo())
                .append('\n');

        detalle.append("Origen: ")
                .append(t.getOrigen())
                .append('\n');

        detalle.append("Antiguedad: ")
                .append(t.getAntiguedad())
                .append(" años\n");

        detalle.append(
                String.format(
                        "Haber con origen y consejos: %.2f PG%n",
                        haber
                )
        );

        if (esConsejero) {

            detalle.append(
                    String.format(
                            "Consejos registrados: %d "
                                    + "(%.2f PG ya incluidos)%n",
                            consejos,
                            consejos * 2.0
                    )
            );
        }

        detalle.append(
                String.format(
                        "Total a percibir: %.2f PG",
                        haber
                )
        );

        areaDetalle.setText(
                detalle.toString()
        );

        areaDetalle.setCaretPosition(0);
    }

    /**
     * Recibe la tripulacion actualizada.
     *
     * Pre:
     * - tripulantes != null.
     *
     * Post:
     * - Actualiza la tabla.
     * - Recalcula el total de haberes.
     * - Conserva la seleccion cuando es posible.
     * - Actualiza los componentes en el EDT.
     *
     * @param tripulantes tripulantes filtrados
     */
    @Override
    public void onTripulacionActualizada(
            List<Tripulante> tripulantes) {

        Runnable actualizar = () -> {

            // =====================================
            // CONSERVAR LA SELECCION
            // =====================================

            Tripulante anterior = null;

            int fila =
                    tablaTripulantes.getSelectedRow();

            if (fila >= 0
                    && fila < tripulantesVisibles.size()) {

                anterior =
                        tripulantesVisibles.get(fila);
            }

            // =====================================
            // ACTUALIZAR LISTA Y TABLA
            // =====================================

            tripulantesVisibles =
                    new ArrayList<>(tripulantes);

            modeloTabla.setRowCount(0);

            int filaAnterior = -1;
            double total = 0;

            for (int i = 0;
                 i < tripulantesVisibles.size();
                 i++) {

                Tripulante t =
                        tripulantesVisibles.get(i);

                double haber =
                        controlador.calcularHaberTotal(t);

                modeloTabla.addRow(
                        new Object[]{
                                t.getID(),
                                t.getCargo(),
                                t.getOrigen(),
                                t.getAntiguedad(),
                                String.format(
                                        "%.2f PG",
                                        haber
                                )
                        }
                );

                total += haber;

                if (t == anterior) {
                    filaAnterior = i;
                }
            }

            // =====================================
            // TOTAL DE HABERES
            // =====================================

            lblTotalHaberes.setText(
                    String.format(
                            "Total haberes: %.2f PG "
                                    + "| Tripulantes: %d",
                            total,
                            tripulantesVisibles.size()
                    )
            );

            // =====================================
            // RESTAURAR SELECCION
            // =====================================

            if (filaAnterior >= 0) {

                tablaTripulantes.setRowSelectionInterval(
                        filaAnterior,
                        filaAnterior
                );
            }

            actualizarDetalle();
        };

        // Actualizar Swing solo en el EDT.
        if (SwingUtilities.isEventDispatchThread()) {

            actualizar.run();

        } else {

            SwingUtilities.invokeLater(
                    actualizar
            );
        }
    }
}
