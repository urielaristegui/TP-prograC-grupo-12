package vista;

import modelo.UniversoAlterno.PlataformaObserver;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionListener;

public class JPanelVisorRecondito extends JPanel implements IVisorReconditoVista, PlataformaObserver {

    private JButton btnAddGeneradora;
    private JButton btnAddNave;
    private JProgressBar barraCapacidad;
    private JTextArea consolaLog;

    public JPanelVisorRecondito(int capacidadMaximaInicial) {
        initComponents(capacidadMaximaInicial);
    }

    private void initComponents(int capacidadMaxima) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Visor del Universo Paralelo — Monitores & MVC"));

        // Panel Superior: Botones de control e indicador de estado
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        btnAddGeneradora = new JButton("⚡ Crear Generadora de Energía");
        btnAddNave = new JButton("🚀 Crear Nave Consumidora");

        barraCapacidad = new JProgressBar(0, capacidadMaxima);
        barraCapacidad.setValue(0);
        barraCapacidad.setStringPainted(true);
        barraCapacidad.setString("0 / " + capacidadMaxima);

        panelSuperior.add(btnAddGeneradora);
        panelSuperior.add(btnAddNave);
        panelSuperior.add(new JLabel(" | Plataforma: "));
        panelSuperior.add(barraCapacidad);

        add(panelSuperior, BorderLayout.NORTH);

        // Panel Central: Consola de texto para el visor
        consolaLog = new JTextArea(15, 50);
        consolaLog.setEditable(false);
        consolaLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        consolaLog.setBackground(new Color(20, 24, 30));
        consolaLog.setForeground(new Color(0, 230, 120));

        DefaultCaret caret = (DefaultCaret) consolaLog.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        add(new JScrollPane(consolaLog), BorderLayout.CENTER);
    }

    // ----- MÉTODOS DE LA INTERFAZ IVisorReconditoView -----

    @Override
    public void setAgregarGeneradoraListener(ActionListener listener) {
        btnAddGeneradora.addActionListener(listener);
    }

    @Override
    public void setAgregarNaveListener(ActionListener listener) {
        btnAddNave.addActionListener(listener);
    }

    @Override
    public void mostrarMensajeLog(String mensaje) {
        SwingUtilities.invokeLater(() -> consolaLog.append(mensaje + "\n"));
    }

    @Override
    public void actualizarCapacidad(int bateriasActuales, int capacidadMaxima) {
        SwingUtilities.invokeLater(() -> {
            barraCapacidad.setMaximum(capacidadMaxima);
            barraCapacidad.setValue(bateriasActuales);
            barraCapacidad.setString(bateriasActuales + " / " + capacidadMaxima);
        });
    }

    @Override
    public void habilitarControles(boolean habilitado) {
        SwingUtilities.invokeLater(() -> {
            btnAddGeneradora.setEnabled(habilitado);
            btnAddNave.setEnabled(habilitado);
        });
    }

    // ----- MÉTODOS DEL PATRÓN OBSERVER (PlataformaObserver) -----

    @Override
    public void onPlataformaCambio(String evento, int bateriasActuales, int capacidadMaxima) {
        mostrarMensajeLog(evento);
        actualizarCapacidad(bateriasActuales, capacidadMaxima);
    }
}