
package vista.Creacion;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import Controlador.CreacionNaveController;
import Controlador.CreacionNaveController.Cargo;
import modelo.Tripulacion.Tripulante;
import modelo.nave.Nave;
import modelo.nave.TipoNave;

/**
 * Vista MVC para crear naves y asignar tripulantes.
 *
 * Invariantes:
 * - controlador != null.
 * - Los componentes Swing estan inicializados.
 * - Las reglas de negocio estan en el controlador.
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
    private final JSpinner spinnerAntiguedad;
    private final JButton btnAgregar;

    private final DefaultTableModel modeloTabla;

    /**
     * Construye el panel grafico.
     *
     * Pre: controlador != null.
     *
     * Post:
     * - Inicializa los componentes.
     * - Registra los listeners.
     * - Muestra el estado inicial.
     *
     * @param controlador controlador de creacion
     * @throws IllegalArgumentException si es null
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

        // ===========================================
        // ENCABEZADO
        // ===========================================

        JPanel panelSuperior =
                new JPanel(new BorderLayout(10, 10));

        JLabel titulo = new JLabel(
                "Configuracion de nave y tripulacion"
        );

        titulo.setFont(
                titulo.getFont().deriveFont(Font.BOLD, 19f)
        );

        panelSuperior.add(titulo, BorderLayout.NORTH);

        // ===========================================
        // CREACION DE NAVE
        // ===========================================

        JPanel formularioNave =
                new JPanel(new GridBagLayout());

        formularioNave.setBorder(
                BorderFactory.createTitledBorder(
                        "1. Crear nave"
                )
        );

        GridBagConstraints c = constraints();

        comboTipo = new JComboBox<>(TipoNave.values());

        spinnerId = new JSpinner(
                new SpinnerNumberModel(10, 1, 999999, 1)
        );

        btnCrear = new JButton("Crear nave");

        agregarCampo(
                formularioNave,
                c,
                0,
                "Tipo de nave:",
                comboTipo
        );

        agregarCampo(
                formularioNave,
                c,
                1,
                "ID de la nave:",
                spinnerId
        );

        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;

        formularioNave.add(btnCrear, c);

        // ===========================================
        // RESUMEN DE NAVE - CORREGIDO
        // ===========================================

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

        lblNave = new JLabel(
                "Todavia no se creo una nave para esta sesion"
        );

        lblRecursos = new JLabel(
                "Combustible: - | Energia: - | Desgaste: -"
        );

        lblEstado = new JLabel(
                "Tripulacion: 0/5 | Falta Capitan"
        );

        // Una etiqueta por fila.
        resumen.add(lblNave);
        resumen.add(lblRecursos);
        resumen.add(lblEstado);

        // ===========================================
        // CONTENEDOR SUPERIOR
        // ===========================================

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

        panelSuperior.add(
                seccionNave,
                BorderLayout.CENTER
        );

        add(panelSuperior, BorderLayout.NORTH);

        // ===========================================
        // ASIGNACION DE TRIPULANTES
        // ===========================================

        JPanel panelTripulacion =
                new JPanel(new BorderLayout(10, 10));

        panelTripulacion.setBorder(
                BorderFactory.createTitledBorder(
                        "2. Asignar tripulantes"
                )
        );

        JPanel datosTripulante =
                new JPanel(new GridBagLayout());

        GridBagConstraints t = constraints();

        txtIdentidad = new JTextField(18);

        comboCargo = new JComboBox<>(Cargo.values());

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
                datosTripulante,
                t,
                0,
                "Identidad / Nombre:",
                txtIdentidad
        );

        agregarCampo(
                datosTripulante,
                t,
                1,
                "Cargo:",
                comboCargo
        );

        agregarCampo(
                datosTripulante,
                t,
                2,
                "Antiguedad (anios):",
                spinnerAntiguedad
        );

        t.gridx = 1;
        t.gridy = 3;
        t.weightx = 0;
        t.fill = GridBagConstraints.NONE;

        datosTripulante.add(btnAgregar, t);

        panelTripulacion.add(
                datosTripulante,
                BorderLayout.NORTH
        );

        // ===========================================
        // TABLA DE TRIPULANTES
        // ===========================================

        modeloTabla = new DefaultTableModel(
                new String[]{
                        "Identidad",
                        "Cargo",
                        "Antiguedad",
                        "Haber base"
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

        JScrollPane scrollTabla =
                new JScrollPane(tabla);

        scrollTabla.setPreferredSize(
                new Dimension(500, 180)
        );

        panelTripulacion.add(
                scrollTabla,
                BorderLayout.CENTER
        );

        // ===========================================
        // MENSAJE DE AYUDA
        // ===========================================

        JPanel ayuda = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        ayuda.add(
                new JLabel(
                        "Para operar: minimo 5 tripulantes y un Capitan."
                )
        );

        panelTripulacion.add(
                ayuda,
                BorderLayout.SOUTH
        );

        add(
                panelTripulacion,
                BorderLayout.CENTER
        );

        // ===========================================
        // LISTENERS
        // ===========================================

        btnCrear.addActionListener(
                e -> crearNave()
        );

        btnAgregar.addActionListener(
                e -> agregarTripulante()
        );

        refrescar();
    }

    /**
     * Crea las restricciones para GridBagLayout.
     *
     * Pre: ninguna.
     *
     * Post:
     * - Devuelve restricciones inicializadas.
     * - No modifica ningun componente.
     *
     * @return restricciones de posicionamiento
     */
    private GridBagConstraints constraints() {

        GridBagConstraints c =
                new GridBagConstraints();

        c.insets = new Insets(5, 7, 5, 7);
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 0;
        c.weighty = 0;

        return c;
    }

    /**
     * Agrega un campo y su etiqueta al formulario.
     *
     * Pre:
     * - panel != null.
     * - c != null.
     * - componente != null.
     *
     * Post:
     * - Agrega la etiqueta y el componente.
     *
     * @param panel formulario
     * @param c restricciones de GridBagLayout
     * @param fila fila del formulario
     * @param etiqueta texto del campo
     * @param componente componente a agregar
     */
    private void agregarCampo(
            JPanel panel,
            GridBagConstraints c,
            int fila,
            String etiqueta,
            Component componente) {

        c.gridy = fila;
        c.gridx = 0;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;

        panel.add(
                new JLabel(etiqueta),
                c
        );

        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;

        panel.add(componente, c);
    }

    /**
     * Solicita la creacion de una nave.
     *
     * Pre:
     * - Se ejecuta desde un evento de Swing.
     *
     * Post:
     * - Si tiene exito, actualiza la vista.
     * - Deshabilita una segunda creacion.
     * - Si falla, informa el error al usuario.
     */
    private void crearNave() {

        try {

            TipoNave tipo =
                    (TipoNave) comboTipo.getSelectedItem();

            int id = (Integer) spinnerId.getValue();

            controlador.crearNave(tipo, id);

            btnCrear.setEnabled(false);
            comboTipo.setEnabled(false);
            spinnerId.setEnabled(false);

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
     * Solicita agregar un tripulante a la nave.
     *
     * Pre:
     * - La nave fue creada.
     * - Se ejecuta desde un evento de Swing.
     *
     * Post:
     * - Incorpora al tripulante si es valido.
     * - Actualiza tabla y resumen.
     * - Si falla, informa el error al usuario.
     */
    private void agregarTripulante() {

        try {

            String nombre =
                    txtIdentidad.getText();

            Cargo cargo =
                    (Cargo) comboCargo.getSelectedItem();

            double antiguedad =
                    ((Number) spinnerAntiguedad.getValue())
                            .doubleValue();

            controlador.agregarTripulante(
                    nombre,
                    cargo,
                    antiguedad
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
     * Muestra un mensaje de error.
     *
     * Pre: mensaje != null.
     *
     * Post:
     * - Informa el error mediante JOptionPane.
     *
     * @param mensaje descripcion del error
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
     * Actualiza la informacion visual de la nave.
     *
     * Pre:
     * - controlador != null.
     *
     * Post:
     * - Actualiza los recursos y estado de la nave.
     * - Actualiza la tabla de tripulantes.
     * - No modifica el modelo.
     */
    public void refrescar() {

        Nave nave = controlador.getNave();

        // =======================================
        // NAVE TODAVIA NO CREADA
        // =======================================

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

            modeloTabla.setRowCount(0);

            return;
        }

        // =======================================
        // INFORMACION DE LA NAVE
        // =======================================

        lblNave.setText(
                "Nave #" + nave.getId()
                        + " - "
                        + nave.getClass().getSimpleName()
        );

        lblRecursos.setText(
                "Combustible: "
                        + nave.getCombustible()
                        + " | Energia: "
                        + nave.getEnergia()
                        + " | Desgaste: "
                        + nave.getDesgaste()
        );

        // =======================================
        // ESTADO DE LA TRIPULACION
        // =======================================

        List<Tripulante> lista =
                controlador.getTripulantes();

        boolean capitan = lista.stream().anyMatch(
                tripulante ->
                        "Capitan".equals(
                                tripulante.getCargo()
                        )
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

        // =======================================
        // TABLA DE TRIPULANTES
        // =======================================

        modeloTabla.setRowCount(0);

        for (Tripulante tripulante : lista) {

            modeloTabla.addRow(
                    new Object[]{
                            tripulante.getID(),
                            tripulante.getCargo(),
                            tripulante.getAntiguedad(),
                            String.format(
                                    "%.2f PG",
                                    tripulante.getSueldo()
                            )
                    }
            );
        }

        revalidate();
        repaint();
    }
}
