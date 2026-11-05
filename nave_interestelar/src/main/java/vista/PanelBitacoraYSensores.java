package vista;

import modelo.bitacora.IObservadorBitacora;
import modelo.destinoytablero.Observador;

import javax.swing.*;
import java.awt.*;

/**
 * panel de interfaz grafica para visualizar la bitacora y los sensores.<br>
 * <b>invariante:</b> txtBitacora != null && lblEstadoSensor != null && lblLecturaProximaCelda != null.<br>
 */
public class PanelBitacoraYSensores extends JPanel implements IObservadorBitacora {

    private JTextArea txtBitacora;
    private JLabel lblEstadoSensor;
    private JLabel lblLecturaProximaCelda;

    /**
     * constructor de la clase panelBitacoraYSensores.<br>
     * <b>pre:</b> ninguna.<br>
     * <b>post:</b> inicializa la disposicion visual y los componentes del panel.<br>
     */
    public PanelBitacoraYSensores() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Bitacora de Vuelo y Sensores"));

        // --- SUBPANEL SUPERIOR: Estado de Sensores ---
        JPanel panelSensores = new JPanel(new GridLayout(2, 1, 5, 5));
        panelSensores.setBorder(BorderFactory.createEtchedBorder());

        lblEstadoSensor = new JLabel("Radar de Corto Alcance: ACTIVO");
        lblEstadoSensor.setFont(new Font("Arial", Font.BOLD, 12));

        lblLecturaProximaCelda = new JLabel("Proxima Celda: Escaneando...");
        lblLecturaProximaCelda.setForeground(Color.BLUE);

        panelSensores.add(lblEstadoSensor);
        panelSensores.add(lblLecturaProximaCelda);

        // --- SUBPANEL CENTRAL: Registro de Bitacora en Tiempo Real ---
        txtBitacora = new JTextArea(12, 30);
        txtBitacora.setEditable(false);
        txtBitacora.setFont(new Font("Monospaced", Font.PLAIN, 11));

        JScrollPane scrollBitacora = new JScrollPane(txtBitacora);

        add(panelSensores, BorderLayout.NORTH);
        add(scrollBitacora, BorderLayout.CENTER);
    }

    /**
     * metodo invocado cuando la bitacora emite una notificacion.<br>
     * <b>pre:</b> evento != null.<br>
     * <b>post:</b> actualiza el texto del panel de forma thread-safe.<br>
     *
     * @param evento mensaje enviado por la bitacora.
     */
    @Override
    public void actualizarBitacora(String evento) {
        // garantizar la actualizacion thread-safe del componente swing
        SwingUtilities.invokeLater(() -> {
            txtBitacora.append(evento + "\n");
            txtBitacora.setCaretPosition(txtBitacora.getDocument().getLength()); // auto-scroll

            // si el evento contiene informacion de sensores, actualizamos la etiqueta
            if (evento.contains("Sensor:")) {
                lblLecturaProximaCelda.setText(evento);
            }
        });
    }
}
