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
import modelo.misiones.ResultadoMision;
import modelo.misiones.Retorno;
import modelo.nave.Nave;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;

/** Escenario A: Factory, tripulación y ciclo completo de M-01, M-02 y M-03. */
public class EscenarioA {

    public static void main(String[] args) {
        System.out.println("=== ESCENARIO A: EJECUCION CORRECTA ===");

        // 1. Crear los tres tipos de nave mediante Factory.
        System.out.println("\n--- 1. CREACION DE NAVES ---");
        Nave exploradora = NaveFactory.crearNave(TipoNave.EXPLORADORA, 1);
        Nave carguero = NaveFactory.crearNave(TipoNave.CARGUERO, 2);
        Nave combate = NaveFactory.crearNave(TipoNave.COMBATE, 3);

        System.out.println("Exploradora: combustible=" + exploradora.getCombustible()
                + ", energia=" + exploradora.getEnergia());
        System.out.println("Carguero: combustible=" + carguero.getCombustible()
                + ", energia=" + carguero.getEnergia());
        System.out.println("Combate: combustible=" + combate.getCombustible()
                + ", energia=" + combate.getEnergia());

        if (exploradora.getCombustible() != 60 || exploradora.getEnergia() != 80
                || carguero.getCombustible() != 100 || carguero.getEnergia() != 60
                || combate.getCombustible() != 80 || combate.getEnergia() != 100) {
            throw new IllegalStateException("Las naves no tienen los recursos iniciales oficiales");
        }

        // 2. Formar una tripulación válida para la Exploradora.
        System.out.println("\n--- 2. TRIPULACION ---");
        Nave nave = exploradora;
        Bitacora bitacora = Bitacora.getInstance();
        bitacora.getRegistro().clear(); // Mantener la demostración reproducible.
        AsistenteDeComandos asistente = new AsistenteDeComandos(nave, bitacora);

        Capitan capitan = Capitan.getInstancia(new Identidad("Santiago"), 2, asistente);
        nave.agregarTripulante(capitan);
        for (String nombre : new String[]{"Ana", "Juan", "Pedro", "Maria"}) {
            nave.agregarTripulante(new Alferez(new Identidad(nombre), 1));
        }
        System.out.println("Tripulantes: " + nave.getTripulacion().size());
        System.out.println("Tripulacion valida: " + nave.tieneTripulacionValida());
        System.out.println("Estado inicial: " + asistente.snapshotEstadoNave());
        if (!nave.tieneTripulacionValida()) {
            throw new IllegalStateException("La tripulación debería ser válida");
        }

        // 3. M-01: 4 de combustible, 5 de energía y 4 de desgaste.
        System.out.println("\n--- 3. M-01: INTERCEPCION Y ASISTENCIA ---");
        Mision m01 = new Intercepcion(asistente, 4, 5, 4);
        capitan.ejecutarMision(m01);
        boolean accionM01 = mostrarMision("M-01", m01, nave, 56, 75, 4,
                "Combustible: 4, Energía: 5, Desgaste: 4", "M1 Intercepción");

        // 4. M-02: 4 de combustible, 5 de energía y 4 de desgaste.
        System.out.println("\n--- 4. M-02: RECOLECCION ---");
        Mision m02 = new Recoleccion(asistente, 4, 5, 4);
        capitan.ejecutarMision(m02);
        boolean accionM02 = mostrarMision("M-02", m02, nave, 52, 70, 8,
                "Combustible: 4, Energía: 5, Desgaste: 4", "M2 Recolección");

        // 5. M-03: 4 de combustible, 0 de energía y 4 de desgaste.
        System.out.println("\n--- 5. M-03: RETORNO SEGURO ---");
        Mision m03 = new Retorno(asistente, 4, 0, 4);
        capitan.ejecutarMision(m03);
        boolean accionM03 = mostrarMision("M-03", m03, nave, 48, 70, 12,
                "Combustible: 4, Energía: 0, Desgaste: 4", "M3 Retorno");

        // 6. Visualizar el orden de los eventos registrados.
        System.out.println("\n--- 6. BITACORA COMPLETA ---");
        for (String evento : bitacora.getRegistro()) {
            System.out.println(evento);
        }

        System.out.println("\n--- 7. BALANCE DE LA DEMOSTRACION ---");
        System.out.println("[OK] Las tres misiones finalizaron EXITOSAS con recursos correctos");
        if (!accionM01 || !accionM02 || !accionM03) {
            System.out.println("[PENDIENTE E1-10] El informe no refleja todas las acciones "
                    + "operativas de alguna misión. Revisar Mision.cerrar(), "
                    + "AsistenteDeComandos.eventosDe() y Retorno.ejecutar().");
        } else {
            System.out.println("[OK] Todas las acciones operativas figuran en los informes");
        }
        System.out.println("=== FIN DEL ESCENARIO A ===");
    }

    /** Imprime todos los campos del InformeMision y verifica lo esencial. */
    private static boolean mostrarMision(String codigo, Mision mision, Nave nave,
                                         int combustibleFinal, int energiaFinal, int desgasteFinal,
                                         String recursosEsperados, String prefijoAccion) {
        InformeMision informe = mision.getInforme();
        if (informe == null) {
            throw new IllegalStateException(codigo + ": no se generó el informe");
        }

        System.out.println("Mision ejecutada: " + informe.getMisionEjecutada());
        System.out.println("Resultado: " + informe.getResultado());
        System.out.println("Recursos consumidos: " + informe.getRecursosConsumidos());
        System.out.println("Estado final (informe): " + informe.getEstadoNave());
        System.out.println("Acciones principales:");
        for (String accion : informe.getAccionesPrincipales()) {
            System.out.println("  - " + accion);
        }
        System.out.println("Observaciones: " + (informe.getObservaciones() == null
                || informe.getObservaciones().isBlank()
                ? "(sin observaciones)" : informe.getObservaciones()));

        String estadoEsperado = "Combustible: " + combustibleFinal
                + ", Energía: " + energiaFinal + ", Desgaste: " + desgasteFinal;
        if (!informe.getMisionEjecutada().equals(mision.getClass().getSimpleName())
                || informe.getResultado() != ResultadoMision.EXITOSA
                || !informe.getRecursosConsumidos().equals(recursosEsperados)
                || !informe.getEstadoNave().equals(estadoEsperado)
                || nave.getCombustible() != combustibleFinal
                || nave.getEnergia() != energiaFinal
                || nave.getDesgaste() != desgasteFinal
                || informe.getAccionesPrincipales().isEmpty()) {
            throw new IllegalStateException(codigo + ": resultado o recursos inconsistentes");
        }
        System.out.println("[OK] " + codigo + ": resultado, recursos e informe coherentes");

        // Comprobar si el informe contiene además la acción real del objetivo.
        boolean accionPresente = informe.getAccionesPrincipales().stream()
                .anyMatch(accion -> accion.contains(prefijoAccion));
        if (!accionPresente) {
            System.out.println("[PENDIENTE] No figura la acción operativa: " + prefijoAccion);
        }
        return accionPresente;
    }
}
