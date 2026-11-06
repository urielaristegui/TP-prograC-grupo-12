package vista;

import modelo.bitacora.Bitacora;
import modelo.bitacora.IObservadorBitacora;

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
     * <b>post:</b> inicializa la disposicion visual, los componentes del panel y se registra en la bitacora.<br>
     */
    public PanelBitacoraYSensores() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Bitacora de Vuelo y Sensores"));

        // subpanel superior: estado de sensores
        JPanel panelSensores = new JPanel(new GridLayout(2, 1, 5, 5));
        panelSensores.setBorder(BorderFactory.createEtchedBorder());

        lblEstadoSensor = new JLabel("Radar de Corto Alcance: ACTIVO");
        lblEstadoSensor.setFont(new Font("Arial", Font.BOLD, 12));

        lblLecturaProximaCelda = new JLabel("Proxima Celda: Escaneando...");
        lblLecturaProximaCelda.setForeground(Color.BLUE);

        panelSensores.add(lblEstadoSensor);
        panelSensores.add(lblLecturaProximaCelda);

        // subpanel central: registro de bitacora en tiempo real
        txtBitacora = new JTextArea(12, 30);
        txtBitacora.setEditable(false);
        txtBitacora.setFont(new Font("Monospaced", Font.PLAIN, 11));

        JScrollPane scrollBitacora = new JScrollPane(txtBitacora);

        add(panelSensores, BorderLayout.NORTH);
        add(scrollBitacora, BorderLayout.CENTER);

        // auto-registro como observador del singleton de bitacora
        Bitacora.getInstance().agregarObservador(this);
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
        SwingUtilities.invokeLater(() -> {
            txtBitacora.append(evento + "\n");
            txtBitacora.setCaretPosition(txtBitacora.getDocument().getLength());

            if (evento.contains("Sensor:") || evento.contains("Navegacion:")) {
                lblLecturaProximaCelda.setText(evento);
            }
        });
    }
}