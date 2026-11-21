
package vista.Creacion;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

import Controlador.CreacionNaveController;
import Controlador.CreacionNaveController.Cargo;
import modelo.Tripulacion.Tripulante;
import modelo.Tripulacion.Tripulante.Origen;
import modelo.nave.Nave;
import modelo.nave.TipoNave;

/**
 * Vista MVC para crear la nave y asignarle tripulantes.
 *
 * Invariante:
 * - El controlador existe.
 * - Las reglas de negocio se delegan en el controlador.
 */
public final class JPanelCreacionNave extends JPanel {

    private final CreacionNaveController controlador;

    private final JComboBox<TipoNave> comboTipo;
    private final JSpinner spinnerId;
    private final JButton btnCrear;

    private final JLabel lblNave;
    private final JLabel lblRecursos;
    private final JLabel lblEstado;

    private final JTextField txtIdentidad;
    private final JComboBox<Cargo> comboCargo;
    private final JComboBox<Origen> comboOrigen;
    private final JSpinner spinnerAntiguedad;
    private final JButton btnAgregar;

    private final DefaultTableModel modeloTabla;

    /**
     * Pre: controlador != null.
     * Post: construye y conecta el formulario.
     */
    public JPanelCreacionNave(
            CreacionNaveController controlador) {

        if (controlador == null) {
            throw new IllegalArgumentException(
                    "El controlador no puede ser nulo"
            );
        }

        this.controlador = controlador;

        setLayout(new BorderLayout(12, 12));
        setBorder(
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        );

        // =====================================
        // ENCABEZADO
        // =====================================

        JPanel superior =
                new JPanel(new BorderLayout(10, 10));

        JLabel titulo = new JLabel(
                "Configuracion de nave y tripulacion"
        );

        titulo.setFont(
                titulo.getFont().deriveFont(Font.BOLD, 19f)
        );

        superior.add(titulo, BorderLayout.NORTH);

        // =====================================
        // 1. CREACION DE NAVE
        // =====================================

        JPanel formularioNave =
                new JPanel(new GridBagLayout());

        formularioNave.setBorder(
                BorderFactory.createTitledBorder(
                        "1. Crear nave"
                )
        );

        comboTipo = new JComboBox<>(TipoNave.values());

        spinnerId = new JSpinner(
                new SpinnerNumberModel(10, 1, 999999, 1)
        );

        btnCrear = new JButton("Crear nave");

        agregarCampo(
                formularioNave,
                0,
                "Tipo de nave:",
                comboTipo
        );

        agregarCampo(
                formularioNave,
                1,
                "ID de la nave:",
                spinnerId
        );

        agregarBoton(formularioNave, 2, btnCrear);

        // =====================================
        // RESUMEN DE NAVE
        // =====================================

        // GridLayout evita superponer las etiquetas.
        JPanel resumen = new JPanel(
                new GridLayout(3, 1, 0, 8)
        );

        resumen.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(
                                "Nave seleccionada"
                        ),
                        BorderFactory.createEmptyBorder(
                                8, 12, 10, 12
                        )
                )
        );

        resumen.setPreferredSize(
                new Dimension(450, 115)
        );

        lblNave = new JLabel();
        lblRecursos = new JLabel();
        lblEstado = new JLabel();

        resumen.add(lblNave);
        resumen.add(lblRecursos);
        resumen.add(lblEstado);

        JPanel seccionNave =
                new JPanel(new BorderLayout(10, 12));

        seccionNave.add(
                formularioNave,
                BorderLayout.NORTH
        );

        seccionNave.add(
                resumen,
                BorderLayout.SOUTH
        );

        superior.add(
                seccionNave,
                BorderLayout.CENTER
        );

        add(superior, BorderLayout.NORTH);

        // =====================================
        // 2. ASIGNACION DE TRIPULANTES
        // =====================================

        JPanel seccionTripulacion =
                new JPanel(new BorderLayout(10, 10));

        seccionTripulacion.setBorder(
                BorderFactory.createTitledBorder(
                        "2. Asignar tripulantes"
                )
        );

        JPanel formularioTripulante =
                new JPanel(new GridBagLayout());

        txtIdentidad = new JTextField(18);

        comboCargo = new JComboBox<>(
                Cargo.values()
        );

        comboOrigen = new JComboBox<>(
                Origen.values()
        );

        spinnerAntiguedad = new JSpinner(
                new SpinnerNumberModel(
                        0.0, 0.0, 80.0, 1.0
                )
        );

        btnAgregar = new JButton(
                "Agregar tripulante"
        );

        btnAgregar.setEnabled(false);

        agregarCampo(
                formularioTripulante,
                0,
                "Identidad / Nombre:",
                txtIdentidad
        );

        agregarCampo(
                formularioTripulante,
                1,
                "Cargo:",
                comboCargo
        );

        agregarCampo(
                formularioTripulante,
                2,
                "Antiguedad (anios):",
                spinnerAntiguedad
        );

        agregarCampo(
                formularioTripulante,
                3,
                "Origen:",
                comboOrigen
        );

        agregarBoton(
                formularioTripulante,
                4,
                btnAgregar
        );

        seccionTripulacion.add(
                formularioTripulante,
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
                        "Antiguedad",
                        "Haber con origen"
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

        JTable tabla = new JTable(modeloTabla);

        tabla.setRowHeight(25);
        tabla.setFillsViewportHeight(true);

        tabla.getTableHeader().setReorderingAllowed(
                false
        );

        JScrollPane scroll =
                new JScrollPane(tabla);

        scroll.setPreferredSize(
                new Dimension(500, 180)
        );

        seccionTripulacion.add(
                scroll,
                BorderLayout.CENTER
        );

        JPanel ayuda = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        ayuda.add(
                new JLabel(
                        "Para operar: minimo 5 tripulantes y un Capitan."
                )
        );

        seccionTripulacion.add(
                ayuda,
                BorderLayout.SOUTH
        );

        add(
                seccionTripulacion,
                BorderLayout.CENTER
        );

        // =====================================
        // LISTENERS
        // =====================================

        btnCrear.addActionListener(
                e -> crearNave()
        );

        btnAgregar.addActionListener(
                e -> agregarTripulante()
        );

        refrescar();
    }

    /**
     * Agrega un campo al formulario.
     *
     * Pre: panel, etiqueta y campo validos.
     * Post: agrega una fila al formulario.
     */
    private void agregarCampo(
            JPanel panel,
            int fila,
            String etiqueta,
            Component campo) {

        GridBagConstraints c =
                new GridBagConstraints();

        c.insets = new Insets(5, 7, 5, 7);
        c.anchor = GridBagConstraints.WEST;
        c.gridy = fila;
        c.gridx = 0;

        panel.add(
                new JLabel(etiqueta),
                c
        );

        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;

        panel.add(campo, c);
    }

    /**
     * Agrega un boton al formulario.
     *
     * Pre: boton != null.
     * Post: agrega el boton en la fila indicada.
     */
    private void agregarBoton(
            JPanel panel,
            int fila,
            JButton boton) {

        GridBagConstraints c =
                new GridBagConstraints();

        c.insets = new Insets(5, 7, 5, 7);
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 1;
        c.gridy = fila;

        panel.add(boton, c);
    }

    /**
     * Crea la nave seleccionada.
     *
     * Pre: datos validos.
     *
     * Post:
     * - Crea la nave mediante el controlador.
     * - Bloquea una segunda creacion.
     * - Habilita la asignacion de tripulantes.
     */
    private void crearNave() {

        try {

            controlador.crearNave(
                    (TipoNave) comboTipo.getSelectedItem(),
                    (Integer) spinnerId.getValue()
            );

            comboTipo.setEnabled(false);
            spinnerId.setEnabled(false);
            btnCrear.setEnabled(false);
            btnAgregar.setEnabled(true);

            refrescar();

            JOptionPane.showMessageDialog(
                    this,
                    "Nave creada correctamente.\n"
                            + "Ahora agrega la tripulacion."
            );

        } catch (
                IllegalArgumentException |
                IllegalStateException ex
        ) {

            mostrarError(ex.getMessage());
        }
    }

    /**
     * Incorpora un tripulante con origen.
     *
     * Pre:
     * - Existe una nave creada.
     * - Los datos ingresados son validos.
     *
     * Post:
     * - Incorpora al tripulante.
     * - Aplica Decorator por origen.
     * - Actualiza el resumen y la tabla.
     */
    private void agregarTripulante() {

        try {

            controlador.agregarTripulante(
                    txtIdentidad.getText(),
                    (Cargo) comboCargo.getSelectedItem(),
                    ((Number) spinnerAntiguedad.getValue())
                            .doubleValue(),
                    (Origen) comboOrigen.getSelectedItem()
            );

            txtIdentidad.setText("");

            refrescar();

        } catch (
                IllegalArgumentException |
                IllegalStateException ex
        ) {

            mostrarError(ex.getMessage());
        }
    }

    /**
     * Muestra los errores al usuario.
     */
    private void mostrarError(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Datos invalidos",
                JOptionPane.WARNING_MESSAGE
        );
    }

    /**
     * Actualiza el resumen y la tabla.
     *
     * Pre: vista inicializada.
     *
     * Post:
     * - Refleja los datos reales de la nave.
     * - Incluye origen y haber con Decorator.
     * - No modifica el modelo.
     */
    public void refrescar() {

        Nave nave = controlador.getNave();

        modeloTabla.setRowCount(0);

        if (nave == null) {

            lblNave.setText(
                    "Todavia no se creo una nave para esta sesion"
            );

            lblRecursos.setText(
                    "Combustible: - | Energia: - | Desgaste: -"
            );

            lblEstado.setText(
                    "Tripulacion: 0/5 | Falta Capitan"
            );

            btnAgregar.setEnabled(false);

            return;
        }

        lblNave.setText(
                "Nave #" + nave.getId()
                        + " - "
                        + nave.getClass().getSimpleName()
        );

        lblRecursos.setText(
                "Combustible: " + nave.getCombustible()
                        + " | Energia: " + nave.getEnergia()
                        + " | Desgaste: " + nave.getDesgaste()
        );

        List<Tripulante> lista =
                controlador.getTripulantes();

        boolean capitan = lista.stream().anyMatch(
                t -> "Capitan".equals(t.getCargo())
        );

        String estado =
                "Tripulacion: "
                        + lista.size()
                        + "/5 | "
                        + (capitan
                        ? "Con Capitan"
                        : "Falta Capitan");

        if (nave.tieneTripulacionValida()) {
            estado += " | LISTA PARA MISIONES";
        }

        lblEstado.setText(estado);
        btnAgregar.setEnabled(true);

        for (Tripulante t : lista) {

            modeloTabla.addRow(
                    new Object[]{
                            t.getID(),
                            t.getCargo(),
                            t.getOrigen(),
                            t.getAntiguedad(),
                            String.format(
                                    "%.2f PG",
                                    t.getSueldoConOrigen()
                            )
                    }
            );
        }

        revalidate();
        repaint();
    }
}
