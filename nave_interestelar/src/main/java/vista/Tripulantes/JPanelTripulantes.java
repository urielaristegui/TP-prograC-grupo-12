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
import modelo.Tripulacion.Tripulante;
import Controlador.TripulantesController;
import vista.Tripulantes.TripulacionObserver;

public class JPanelTripulantes extends JPanel implements TripulacionObserver {

        private final TripulantesController controlador;
        private final DefaultTableModel modeloTabla;
        private final JTable tablaTripulantes;
        private final JComboBox<String> comboCargo;
        private final JButton btnRegistrarConsejo;
        private final JButton btnActualizar;
        private final JTextArea areaDetalle;
        private final JLabel lblTotalHaberes;

        private List<Tripulante> tripulantesVisibles = new ArrayList<>();

        public JPanelTripulantes(TripulantesController controlador) {
            if (controlador == null) {
                throw new IllegalArgumentException("El controlador no puede ser nulo.");
            }
            this.controlador = controlador;

            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

            // Encabezado y controles
            JPanel panelSuperior = new JPanel(new BorderLayout(8, 8));
            JLabel lblTitulo = new JLabel("Tripulación y Haberes");
            lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 16f));
            panelSuperior.add(lblTitulo, BorderLayout.WEST);

            JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            panelControles.add(new JLabel("Cargo:"));
            comboCargo = new JComboBox<>(new String[]{"Todos", "Capitán", "Consejero", "Teniente", "Alférez"});
            panelControles.add(comboCargo);

            btnRegistrarConsejo = new JButton("Registrar consejo (+2 PG)");
            btnRegistrarConsejo.setEnabled(false);
            panelControles.add(btnRegistrarConsejo);

            btnActualizar = new JButton("Actualizar");
            panelControles.add(btnActualizar);

            panelSuperior.add(panelControles, BorderLayout.EAST);
            add(panelSuperior, BorderLayout.NORTH);

            // Tabla de tripulantes
            String[] columnas = {"Identidad", "Cargo", "Origen", "Antigüedad (años)", "Haber (PG)"};
            modeloTabla = new DefaultTableModel(columnas, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            tablaTripulantes = new JTable(modeloTabla);
            tablaTripulantes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tablaTripulantes.setRowHeight(24);
            tablaTripulantes.getTableHeader().setReorderingAllowed(false);

            JScrollPane scrollTabla = new JScrollPane(tablaTripulantes);
            scrollTabla.setBorder(BorderFactory.createTitledBorder("Tripulantes"));

            // Detalle del tripulante seleccionado
            areaDetalle = new JTextArea(6, 40);
            areaDetalle.setEditable(false);
            areaDetalle.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

            JScrollPane scrollDetalle = new JScrollPane(areaDetalle);
            scrollDetalle.setBorder(BorderFactory.createTitledBorder("Detalle del tripulante"));

            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollTabla, scrollDetalle);
            splitPane.setResizeWeight(0.65);
            add(splitPane, BorderLayout.CENTER);

            // Pie con suma de haberes
            lblTotalHaberes = new JLabel("Total haberes: 0.00 PG | Tripulantes: 0", SwingConstants.RIGHT);
            lblTotalHaberes.setFont(lblTotalHaberes.getFont().deriveFont(Font.BOLD, 12f));
            add(lblTotalHaberes, BorderLayout.SOUTH);

            configurarListeners();
            this.controlador.setVista(this);
        }

        private void configurarListeners() {
            comboCargo.addActionListener(e -> {
                String cargoSeleccionado = (String) comboCargo.getSelectedItem();
                controlador.filtrarPorCargo(cargoSeleccionado);
            });

            btnActualizar.addActionListener(e -> {
                String cargoSeleccionado = (String) comboCargo.getSelectedItem();
                if (cargoSeleccionado == null || "Todos".equals(cargoSeleccionado)) {
                    controlador.refrescar();
                } else {
                    controlador.filtrarPorCargo(cargoSeleccionado);
                }
            });

            tablaTripulantes.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    actualizarDetalle();
                }
            });

            btnRegistrarConsejo.addActionListener(e -> {
                int fila = tablaTripulantes.getSelectedRow();
                if (fila >= 0 && fila < tripulantesVisibles.size()) {
                    controlador.registrarConsejo(tripulantesVisibles.get(fila));
                }
            });
        }

        private void actualizarDetalle() {
            int fila = tablaTripulantes.getSelectedRow();
            if (fila < 0 || fila >= tripulantesVisibles.size()) {
                btnRegistrarConsejo.setEnabled(false);
                areaDetalle.setText("");
                return;
            }

            Tripulante t = tripulantesVisibles.get(fila);
            boolean esConsejero = t.getCargo() != null && t.getCargo().equalsIgnoreCase("Consejero");
            btnRegistrarConsejo.setEnabled(esConsejero);

            double haberBaseYDecoradores = t.getSueldo();
            int consejos = esConsejero ? controlador.getConsejos(t) : 0;
            double plusConsejos = consejos * 2.0;
            double haberTotal = controlador.calcularHaberTotal(t);

            StringBuilder sb = new StringBuilder();
            sb.append("Identidad: ").append(t.getID()).append("\n");
            sb.append("Cargo: ").append(t.getCargo()).append("\n");
            sb.append("Origen: ").append(t.getOrigen()).append("\n");
            sb.append("Antigüedad: ").append(t.getAntiguedad()).append(" años\n");
            sb.append(String.format("Sueldo / Haber (Decorator): %.2f PG%n", haberBaseYDecoradores));
            if (esConsejero) {
                sb.append(String.format("Consejos registrados: %d (+%.2f PG)%n", consejos, plusConsejos));
            }
            sb.append(String.format("Total a percibir: %.2f PG", haberTotal));

            areaDetalle.setText(sb.toString());
            areaDetalle.setCaretPosition(0);
        }

        @Override
        public void onTripulacionActualizada(List<Tripulante> tripulantes) {
            Runnable actualizarUI = () -> {
                Tripulante seleccionadoPrevio = null;
                int filaPrevia = tablaTripulantes.getSelectedRow();
                if (filaPrevia >= 0 && filaPrevia < tripulantesVisibles.size()) {
                    seleccionadoPrevio = tripulantesVisibles.get(filaPrevia);
                }

                this.tripulantesVisibles = new ArrayList<>(tripulantes);
                modeloTabla.setRowCount(0);

                double total = 0.0;
                int filaAReseleccionar = -1;

                for (int i = 0; i < tripulantesVisibles.size(); i++) {
                    Tripulante t = tripulantesVisibles.get(i);
                    double haber = controlador.calcularHaberTotal(t);
                    modeloTabla.addRow(new Object[]{
                            t.getID(),
                            t.getCargo(),
                            t.getOrigen(),
                            t.getAntiguedad(),
                            String.format("%.2f PG", haber)
                    });
                    total += haber;
                    if (seleccionadoPrevio != null && seleccionadoPrevio == t) {
                        filaAReseleccionar = i;
                    }
                }

                lblTotalHaberes.setText(String.format(
                        "Total haberes: %.2f PG | Tripulantes: %d",
                        total, tripulantesVisibles.size()
                ));

                if (filaAReseleccionar >= 0) {
                    tablaTripulantes.setRowSelectionInterval(filaAReseleccionar, filaAReseleccionar);
                } else {
                    actualizarDetalle();
                }
            };

            if (SwingUtilities.isEventDispatchThread()) {
                actualizarUI.run();
            } else {
                SwingUtilities.invokeLater(actualizarUI);
            }
        }
}

