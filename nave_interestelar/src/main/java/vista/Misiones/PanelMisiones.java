package vista.Misiones;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;

import Controlador.Misiones.MisionController;
import modelo.asistenteDeComandos.AsistenteDeComandos;
import modelo.bitacora.Bitacora;
import modelo.misiones.InformeMision;

public class PanelMisiones extends JPanel implements MisionController.MisionListener {

    private final MisionController controller;

    private final JComboBox<String> comboMision;
    private final JSpinner spinnerCombustible;
    private final JSpinner spinnerEnergia;
    private final JSpinner spinnerGemas;
    private final JButton btnEjecutar;
    private final JTextArea areaInforme;
    private final JTextArea areaBitacora;
    private final JLabel lblEstadoNave;

    public PanelMisiones(AsistenteDeComandos asistente, Bitacora bitacora) {
        this.controller = new MisionController(asistente, bitacora, this);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelControles = new JPanel(new GridLayout(2, 1, 6, 6));

        JPanel filaSeleccion = new JPanel(new FlowLayout(FlowLayout.LEFT));
        comboMision = new JComboBox<>(new String[]{
                "Intercepcion (M-01)",
                "Recoleccion (M-02)",
                "Retorno (M-03)"
        });
        btnEjecutar = new JButton("Ejecutar misión");
        lblEstadoNave = new JLabel("Estado: " + controller.obtenerEstadoNave());

        filaSeleccion.add(new JLabel("Misión:"));
        filaSeleccion.add(comboMision);
        filaSeleccion.add(btnEjecutar);
        filaSeleccion.add(lblEstadoNave);

        JPanel filaParametros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        spinnerCombustible = new JSpinner(new SpinnerNumberModel(10, 0, 50, 1));
        spinnerEnergia = new JSpinner(new SpinnerNumberModel(10, 0, 50, 1));
        spinnerGemas = new JSpinner(new SpinnerNumberModel(50, 20, 100, 5));

        filaParametros.add(new JLabel("Combustible M-01:"));
        filaParametros.add(spinnerCombustible);
        filaParametros.add(new JLabel("Energía M-01:"));
        filaParametros.add(spinnerEnergia);
        filaParametros.add(new JLabel("Gemas M-02:"));
        filaParametros.add(spinnerGemas);

        panelControles.add(filaSeleccion);
        panelControles.add(filaParametros);
        add(panelControles, BorderLayout.NORTH);

        areaInforme = new JTextArea(12, 40);
        areaInforme.setEditable(false);
        JScrollPane scrollInforme = new JScrollPane(areaInforme);
        scrollInforme.setBorder(BorderFactory.createTitledBorder("Informe de misión"));

        areaBitacora = new JTextArea(10, 40);
        areaBitacora.setEditable(false);
        JScrollPane scrollBitacora = new JScrollPane(areaBitacora);
        scrollBitacora.setBorder(BorderFactory.createTitledBorder("Bitácora"));

        JPanel panelCentro = new JPanel(new GridLayout(2, 1, 8, 8));
        panelCentro.add(scrollInforme);
        panelCentro.add(scrollBitacora);
        add(panelCentro, BorderLayout.CENTER);

        actualizarCamposPorMision();
        comboMision.addActionListener(e -> actualizarCamposPorMision());
        btnEjecutar.addActionListener(e -> controller.ejecutarMision(
                comboMision.getSelectedIndex(),
                (Integer) spinnerCombustible.getValue(),
                (Integer) spinnerEnergia.getValue(),
                (Integer) spinnerGemas.getValue()
        ));
    }

    private void actualizarCamposPorMision() {
        int idx = comboMision.getSelectedIndex();
        spinnerCombustible.setEnabled(idx == 0);
        spinnerEnergia.setEnabled(idx == 0);
        spinnerGemas.setEnabled(idx == 1);
    }

    @Override
    public void onMisionIniciada() {
        btnEjecutar.setEnabled(false);
        areaInforme.setText("Ejecutando misión...\n");
    }

    @Override
    public void onMisionFinalizada(boolean pasoPreparacion, InformeMision informe,
                                   List<String> eventosBitacora, String estadoNave) {
        lblEstadoNave.setText("Estado: " + estadoNave);

        StringBuilder sb = new StringBuilder();
        sb.append("¿Pasó la preparación?: ").append(pasoPreparacion).append("\n");

        if (informe == null) {
            sb.append("Informe: no se generó (la misión no llegó a evaluarse).\n");
        } else {
            sb.append("Misión ejecutada: ").append(informe.getMisionEjecutada()).append("\n");
            sb.append("Resultado: ").append(informe.getResultado()).append("\n");
            sb.append("Recursos: ").append(informe.getRecursosConsumidos()).append("\n");
            sb.append("Estado de la nave (previo al cierre): ").append(informe.getEstadoNave()).append("\n");
            sb.append("Observaciones: ").append(informe.getObservaciones()).append("\n");
            sb.append("Acciones principales:\n");
            for (String accion : informe.getAccionesPrincipales()) {
                sb.append("  - ").append(accion).append("\n");
            }
        }
        areaInforme.setText(sb.toString());

        StringBuilder sbBitacora = new StringBuilder();
        for (String evento : eventosBitacora) {
            sbBitacora.append(evento).append("\n");
        }
        areaBitacora.setText(sbBitacora.toString());

        btnEjecutar.setEnabled(true);
    }

    @Override
    public void onErrorParametro(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Parámetro inválido", JOptionPane.WARNING_MESSAGE);
    }
}