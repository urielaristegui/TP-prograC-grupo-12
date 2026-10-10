package Pruebas;

import modelo.Tripulacion.Alferez;
import modelo.Tripulacion.Capitan;
import modelo.Tripulacion.Identidad;
import modelo.asistenteDeComandos.AsistenteDeComandos;
import modelo.bitacora.Bitacora;
import modelo.misiones.InformeMision;
import modelo.misiones.Intercepcion;
import modelo.misiones.Mision;
import modelo.misiones.Recoleccion;
import modelo.misiones.Retorno;
import modelo.nave.Nave;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;

/** Escenario A de la ficha E1: Factory + tripulación + ciclo completo M-01/M-02/M-03. */
public class EscenarioA {

    public static void main(String[] args) {
        System.out.println("=== ESCENARIO A: EJECUCION CORRECTA ===");

        // 1. Crear los tres tipos de nave mediante Factory.
        System.out.println("\n--- 1. CREACION DE NAVES ---");
        Nave exploradora = NaveFactory.crearNave(TipoNave.EXPLORADORA, 1);
        Nave carguero = NaveFactory.crearNave(TipoNave.CARGUERO, 2);
        Nave combate = NaveFactory.crearNave(TipoNave.COMBATE, 3);

        System.out.println("Exploradora: " + exploradora.getCombustible()
                + " combustible, " + exploradora.getEnergia() + " energia");
        System.out.println("Carguero: " + carguero.getCombustible()
                + " combustible, " + carguero.getEnergia() + " energia");
        System.out.println("Combate: " + combate.getCombustible()
                + " combustible, " + combate.getEnergia() + " energia");

        if (exploradora.getCombustible() != 60 || exploradora.getEnergia() != 80
                || carguero.getCombustible() != 100 || carguero.getEnergia() != 60
                || combate.getCombustible() != 80 || combate.getEnergia() != 100) {
            throw new IllegalStateException("ERROR: los recursos iniciales no son los de la ficha E1");
        }

        // 2. Seleccionar la Exploradora y asignarle una tripulación válida.
        System.out.println("\n--- 2. TRIPULACION ---");
        Nave nave = exploradora;
        Bitacora bitacora = Bitacora.getInstance();
        AsistenteDeComandos asistente = new AsistenteDeComandos(nave, bitacora);

        Capitan capitan = Capitan.getInstancia(
                new Identidad("Santiago"), 2, asistente);
        nave.agregarTripulante(capitan);
        for (String nombre : new String[]{"Ana", "Juan", "Pedro", "Maria"}) {
            nave.agregarTripulante(new Alferez(new Identidad(nombre), 1));
        }

        System.out.println("Cantidad: " + nave.getTripulacion().size());
        System.out.println("Tripulacion valida: " + nave.tieneTripulacionValida());
        if (!nave.tieneTripulacionValida()) {
            throw new IllegalStateException("ERROR: la tripulacion no es valida");
        }
        System.out.println("Estado inicial: " + asistente.snapshotEstadoNave());

        // 3. M-01: costo oficial E1 = 4 combustible, 5 energia, 4 desgaste.
        System.out.println("\n--- 3. M-01: INTERCEPCION Y ASISTENCIA ---");
        Mision m01 = new Intercepcion(asistente, 4, 5, 4);
        capitan.ejecutarMision(m01);
        mostrarMision(m01, asistente, nave, 56, 75, 4);

        // 4. M-02: costo oficial E1 = 4 combustible, 5 energia, 4 desgaste.
        System.out.println("\n--- 4. M-02: RECOLECCION ---");
        Mision m02 = new Recoleccion(asistente, 4, 5, 4);
        capitan.ejecutarMision(m02);
        mostrarMision(m02, asistente, nave, 52, 70, 8);

        // 5. M-03: costo oficial E1 = 4 combustible, 0 energia, 4 desgaste.
        System.out.println("\n--- 5. M-03: RETORNO SEGURO ---");
        Mision m03 = new Retorno(asistente, 4, 0, 4);
        capitan.ejecutarMision(m03);
        mostrarMision(m03, asistente, nave, 48, 70, 12);

        // 6. Mostrar los eventos de las tres misiones y los estados del motor.
        System.out.println("\n--- 6. BITACORA COMPLETA (ORDEN DE REGISTRO) ---");
        for (String evento : bitacora.getRegistro()) {
            System.out.println(evento);
        }

        // El informe actual genera objeto y recursos, pero no completa todas las listas.
        if (m01.getInforme().getAccionesPrincipales().isEmpty()
                || m02.getInforme().getAccionesPrincipales().isEmpty()
                || m03.getInforme().getAccionesPrincipales().isEmpty()) {
            System.out.println("\nPENDIENTE E1-10: completar las acciones principales en InformeMision.");
        }
        System.out.println("\n=== FIN DEL ESCENARIO A ===");
    }

    /** Muestra cada resultado y falla si no coincide con los recursos esperados. */
    private static void mostrarMision(Mision mision, AsistenteDeComandos asistente,
                                      Nave nave, int combustible, int energia, int desgaste) {
        InformeMision informe = mision.getInforme();
        System.out.println("Informe: " + informe);
        System.out.println("Estado final: " + asistente.snapshotEstadoNave());
        System.out.println("Esperado: combustible=" + combustible + ", energia=" + energia
                + ", desgaste=" + desgaste + ", resultado=EXITOSA");

        if (informe == null || !"EXITOSA".equals(informe.getResultado())
                || nave.getCombustible() != combustible || nave.getEnergia() != energia
                || nave.getDesgaste() != desgaste) {
            throw new IllegalStateException("ERROR: la mision no cumple el resultado esperado");
        }
        System.out.println("[OK] Mision e invariantes de recursos verificadas");
    }
}
