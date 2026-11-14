
package Pruebas;

import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import modelo.elementos.Asteroide;
import modelo.elementos.ElementoDelUniverso;
import modelo.elementos.EstacionDeCarga;
import modelo.nave.Nave;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;
import modelo.universo.Universo;
import vista.JPanelTablero;

/**
 * Prueba visual del tablero del universo.
 */
public class PruebaJPanelTablero {

    /**
     * Inicia la simulacion grafica.
     *
     * Pre: ninguna.
     * Post:
     * - Crea un universo de 20x20.
     * - Distribuye elementos aleatoriamente.
     * - Abre una ventana Swing.
     * - Ejecuta turnos del tablero cada 600 ms.
     *
     * @param args no utilizado
     * @throws Exception si el tablero rechaza la configuracion
     */
    public static void main(String[] args) throws Exception {

        Nave jugador = NaveFactory.crearNave(
                TipoNave.EXPLORADORA, 1
        );

        ArrayList<ElementoDelUniverso> elementos =
                new ArrayList<>();

        // Crear asteroides
        for (int i = 0; i < 20; i++) {
            elementos.add(new Asteroide(10));
        }

        // Crear estaciones
        for (int i = 0; i < 6; i++) {
            elementos.add(new EstacionDeCarga(20, 20));
        }

        // Crear naves enemigas
        elementos.add(NaveFactory.crearNave(
                TipoNave.EXPLORADORA, 2
        ));

        elementos.add(NaveFactory.crearNave(
                TipoNave.CARGUERO, 3
        ));

        elementos.add(NaveFactory.crearNave(
                TipoNave.COMBATE, 4
        ));

        Universo universo = new Universo(jugador, elementos);

        universo.elegirObjetivo();

        SwingUtilities.invokeLater(() -> {

            JFrame ventana =
                    new JFrame("Prueba del tablero espacial");

            JPanelTablero panel =
                    new JPanelTablero(universo);

            ventana.setDefaultCloseOperation(
                    JFrame.EXIT_ON_CLOSE
            );

            ventana.add(panel);

            ventana.pack();
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);

            // Simular turnos del tablero
            Timer timer = new Timer(600, e -> {
                universo.avanzarTurnoDestino();
            });

            timer.start();
        });
    }
}
