package Pruebas;

import modelo.Tripulacion.Alferez;
import modelo.Tripulacion.Capitan;
import modelo.Tripulacion.Identidad;
import modelo.asistenteDeComandos.AsistenteDeComandos;
import modelo.bitacora.Bitacora;
import modelo.misiones.Intercepcion;
import modelo.misiones.Mision;
import modelo.nave.Nave;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;

/** Escenario B de la ficha E1: recursos insuficientes y ausencia de consumo parcial. */
public class EscenarioB {

    public static void main(String[] args) {
        System.out.println("=== ESCENARIO B: RECURSOS INSUFICIENTES ===");

        // 1. Crear la nave, asistente y tripulación habilitada.
        System.out.println("\n--- 1. PREPARACION ---");
        Nave nave = NaveFactory.crearNave(TipoNave.EXPLORADORA, 10);
        Bitacora bitacora = Bitacora.getInstance();
        bitacora.getRegistro().clear(); // Aislar esta demostracion de otras ejecuciones.
        AsistenteDeComandos asistente = new AsistenteDeComandos(nave, bitacora);
        Capitan capitan = Capitan.getInstancia(new Identidad("Capitan B"), 1, asistente);
        nave.agregarTripulante(capitan);
        for (String nombre : new String[]{"A", "B", "C", "D"}) {
            nave.agregarTripulante(new Alferez(new Identidad(nombre), 1));
        }
        System.out.println("Tripulacion valida: " + nave.tieneTripulacionValida());

        int combAntes = nave.getCombustible();
        int energiaAntes = nave.getEnergia();
        int desgasteAntes = nave.getDesgaste();
        System.out.println("Antes: " + asistente.snapshotEstadoNave());

        // 2. Intercepción imposible: pide 70 unidades, pero solo hay 60.
        System.out.println("\n--- 2. INTENTAR MISION SIN COMBUSTIBLE SUFICIENTE ---");
        Mision mision = new Intercepcion(asistente, 70, 5, 4);
        boolean completada = mision.director();
        System.out.println("Mision completada: " + completada + " (esperado: false)");
        System.out.println("Informe generado: " + mision.getInforme()
                + " (esperado: null; la mision no llego al cierre)");

        // 3. Comprobar que ningún recurso se modificó.
        System.out.println("\n--- 3. COMPROBAR AUSENCIA DE CAMBIOS PARCIALES ---");
        System.out.println("Despues: " + asistente.snapshotEstadoNave());
        boolean recursosIntactos = nave.getCombustible() == combAntes
                && nave.getEnergia() == energiaAntes
                && nave.getDesgaste() == desgasteAntes;
        System.out.println("Recursos intactos: " + recursosIntactos);

        // 4. Mostrar el rechazo en la bitácora.
        System.out.println("\n--- 4. BITACORA ---");
        boolean registrado = false;
        for (String evento : bitacora.getRegistro()) {
            System.out.println(evento);
            if (evento.contains("preparación fallida")) {
                registrado = true;
            }
        }

        if (completada || mision.getInforme() != null || !recursosIntactos || !registrado) {
            throw new IllegalStateException("ERROR: la cancelacion no fue consistente");
        }
        System.out.println("\n[OK] Rechazo registrado sin cambios parciales");
        System.out.println("=== FIN DEL ESCENARIO B ===");
    }
}
