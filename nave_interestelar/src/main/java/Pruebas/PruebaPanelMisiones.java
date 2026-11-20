package Pruebas;

import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import modelo.asistenteDeComandos.AsistenteDeComandos;
import modelo.bitacora.Bitacora;
import modelo.destinoytablero.CantidadDeElementosSuperaCasillaException;
import modelo.destinoytablero.DimensionesDeTableroInconsistentesException;
import modelo.destinoytablero.Tablero;
import modelo.elementos.ElementoDelUniverso;
import modelo.nave.Nave;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;
import modelo.sensores.ReglasNavegacion;
import modelo.sensores.Sensores;
import vista.Misiones.PanelMisiones;

public class PruebaPanelMisiones {

    public static void main(String[] args) {
        Nave nave = NaveFactory.crearNave(TipoNave.EXPLORADORA, 1);
        Bitacora bitacora = Bitacora.getInstance();

        ArrayList<ElementoDelUniverso> elementos = new ArrayList<ElementoDelUniverso>();

        Tablero tablero;
        try {
            tablero = new Tablero(7, 7, elementos);
        } catch (DimensionesDeTableroInconsistentesException | CantidadDeElementosSuperaCasillaException e) {
            System.out.println("No se pudo crear el tablero: " + e.getMessage());
            return;
        }

        AsistenteDeComandos asistente = new AsistenteDeComandos(nave, bitacora, tablero);
        asistente.setReglasNavegacion(new ReglasNavegacion(new Sensores(tablero)));

        SwingUtilities.invokeLater(() -> {
            JFrame ventana = new JFrame("Panel de Misiones");
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setContentPane(new PanelMisiones(asistente, bitacora));
            ventana.pack();
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        });
    }
}
