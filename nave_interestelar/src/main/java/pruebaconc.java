package nave_interestelar;

import Controlador.VisorReconditoController;
import modelo.UniversoAlterno.PlataformaDeCarga;
import vista.JPanelVisorRecondito;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Clase ejecutable de prueba para verificar la concurrencia mediante Monitores
 * y la arquitectura MVC / Observer en el Visor Recóndito.
 */
public class pruebaconc {

    public static void main(String[] args) {
        // Ejecución thread-safe de la interfaz gráfica en el Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                // Aplicar el Look & Feel nativo del sistema operativo
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            // 1. Instanciar el Modelo Concurrente (Plataforma con capacidad máxima de 5 baterías)
            PlataformaDeCarga plataformaModelo = new PlataformaDeCarga(5);

            // 2. Instanciar la Vista (JPanel del Visor Recóndito)
            JPanelVisorRecondito vistaPanel = new JPanelVisorRecondito(plataformaModelo.getCapacidadMaxima());

            // 3. Registrar la Vista como Observador del Modelo (Patrón Observer)
            plataformaModelo.registrarObservador(vistaPanel);

            // 4. Instanciar el Controlador (MVC) enlazando Modelo e Interfaz de Vista
            VisorReconditoController controlador = new VisorReconditoController(plataformaModelo, vistaPanel);

            // 5. Configurar el contenedor JFrame principal de la prueba
            JFrame frame = new JFrame("Prueba Concurrencia — Visor Recóndito (Monitores & MVC)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(vistaPanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // 6. Iniciar la simulación en tiempo real lanzando un hilo productor y un consumidor iniciales
            controlador.agregarGeneradora();
            controlador.agregarNaveConsumidora();
        });
    }
}