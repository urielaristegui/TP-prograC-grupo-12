package Pruebas;

import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import modelo.Tripulacion.*;
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

    /**
     * Pruebas.PruebaPanelMisiones.Prueba manual de las operaciones principales del proyecto.
     *
     */

    public static final class Prueba {
            public static void main(String[] args) {
                    Nave nave = NaveFactory.crearNave(TipoNave.EXPLORADORA, 1);
                    Bitacora bitacora = Bitacora.getInstance();
                    AsistenteDeComandos asistente = new AsistenteDeComandos(nave, bitacora);

                    Capitan capitan = Capitan.getInstancia(
                            new Identidad("Capitán"), 10, asistente);
                    Tripulante alferez = new Alferez(new Identidad("Alférez"), 3);
                    Tripulante consejero = new Consejero(new Identidad("Consejero"), 4);
                    Tripulante teniente = new Teniente(new Identidad("Teniente"), 5);
                    Tripulante segundoAlferez = new Alferez(
                            new Identidad("Alférez auxiliar"), 2);

                    nave.agregarTripulante(capitan);
                    nave.agregarTripulante(alferez);
                    nave.agregarTripulante(consejero);
                    nave.agregarTripulante(teniente);
                    nave.agregarTripulante(segundoAlferez);

                    System.out.println("Nave: " + nave.getId());
                    System.out.println("Tripulantes: " + nave.getTripulacion().size());
                    System.out.println("Tripulación válida: " + nave.tieneTripulacionValida());
                    System.out.println("Sueldo del capitán: " + capitan.getSueldo());
                    System.out.println("Capitán encontrado: "
                            + (nave.buscarTripulante(String.valueOf(capitan.getID())) != null));

                    nave.consumirRecursos(10, 15, 20);
                    System.out.println("Combustible restante: " + nave.getCombustible());
                    System.out.println("Energía restante: " + nave.getEnergia());
                    System.out.println("Desgaste: " + nave.getDesgaste());

                    asistente.registrarEvento("Pruebas.PruebaPanelMisiones.Prueba ejecutada correctamente");
                    System.out.println("Eventos en bitácora: " + bitacora.getRegistro().size());
            }
    }
}
