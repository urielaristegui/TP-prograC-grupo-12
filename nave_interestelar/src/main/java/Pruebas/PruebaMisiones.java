package Pruebas;

import java.util.ArrayList;

import modelo.asistenteDeComandos.AsistenteDeComandos;
import modelo.bitacora.Bitacora;
import modelo.destinoytablero.CantidadDeElementosSuperaCasillaException;
import modelo.destinoytablero.DimensionesDeTableroInconsistentesException;
import modelo.destinoytablero.Tablero;
import modelo.elementos.ElementoDelUniverso;
import modelo.misiones.InformeMision;
import modelo.misiones.Intercepcion;
import modelo.misiones.Mision;
import modelo.misiones.Recoleccion;
import modelo.misiones.Retorno;
import modelo.nave.Nave;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;
import modelo.sensores.ReglasNavegacion;
import modelo.sensores.Sensores;

/**
 * Pruebas.PruebaPanelMisiones.Prueba manual de Parte 2: las tres misiones corriendo sobre el tablero real.
 */
public final class PruebaMisiones {

    public static void main(String[] args) {
        Nave nave = NaveFactory.crearNave(TipoNave.EXPLORADORA, 1);
        Bitacora bitacora = Bitacora.getInstance();

        // Sin elementos para que el recorrido sea predecible. Para ver encuentros,
        // agregá elementos a esta lista; las otras naves también lo son, por ejemplo:
        // elementos.add(NaveFactory.crearNave(TipoNave.EXPLORADORA, 2));
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

        System.out.println("=== CASO A: las tres misiones ===");
        System.out.println("Nave inicial -> " + asistente.snapshotEstadoNave());
        System.out.println();
        ejecutar("M-01 Intercepción y asistencia", new Intercepcion(asistente, 10, 10), asistente);
        ejecutar("M-02 Recolección", new Recoleccion(asistente, 50), asistente);
        ejecutar("M-03 Retorno a punto seguro", new Retorno(asistente), asistente);
        System.out.println("Gemas de la nave: " + nave.getGemas());
        System.out.println();

        System.out.println("=== CASO B: recursos insuficientes ===");
        asistente.ordenarConsumo(97, 0, 0); // deja 3 de combustible, el despegue pide 4
        System.out.println("Nave antes -> " + asistente.snapshotEstadoNave());
        System.out.println();
        ejecutar("M-01 con combustible insuficiente", new Intercepcion(asistente, 0, 0), asistente);

        System.out.println("=== BITÁCORA COMPLETA ===");
        for (String evento : bitacora.getRegistro()) {
            System.out.println(evento);
        }
    }

    private static void ejecutar(String titulo, Mision mision, AsistenteDeComandos asistente) {
        System.out.println("--- " + titulo + " ---");
        boolean seEjecuto = mision.director();
        System.out.println("¿Pasó la preparación? " + seEjecuto);

        InformeMision informe = mision.getInforme();
        if (informe == null) {
            System.out.println("Informe: (no se generó: la misión no llegó a evaluarse)");
        } else {
            System.out.println("Misión ejecutada: " + informe.getMisionEjecutada());
            System.out.println("Resultado: " + informe.getResultado());
            System.out.println("Recursos (balance del trayecto): " + informe.getRecursosConsumidos());
            System.out.println("Estado de la nave (previo al cierre): " + informe.getEstadoNave());
            System.out.println("Observaciones: " + informe.getObservaciones());
            System.out.println("Acciones principales:");
            for (String accion : informe.getAccionesPrincipales()) {
                System.out.println("  - " + accion);
            }
        }
        System.out.println("Nave después -> " + asistente.snapshotEstadoNave());
        System.out.println();
    }
}