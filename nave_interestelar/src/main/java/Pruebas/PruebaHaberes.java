package Pruebas;

import modelo.Tripulacion.Alferez;
import modelo.Tripulacion.Capitan;
import modelo.Tripulacion.Consejero;
import modelo.Tripulacion.Haber;
import modelo.Tripulacion.Identidad;
import modelo.Tripulacion.Marciano;
import modelo.Tripulacion.Teniente;
import modelo.Tripulacion.Terricola;
import modelo.Tripulacion.Vulcano;
import modelo.Tripulacion.Tripulante;
import modelo.asistenteDeComandos.AsistenteDeComandos;
import modelo.bitacora.Bitacora;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;

/** Evidencia adicional E1-08: remuneración por cargo, antigüedad, origen y Decorator. */
public class PruebaHaberes {

    public static void main(String[] args) {
        System.out.println("=== DEMOSTRACION DE HABERES (DECORATOR) ===");

        // 1. Crear un representante de cada uno de los cuatro cargos.
        // Todos tienen 2 años de antigüedad para comparar los adicionales.
        System.out.println("\n--- 1. CUATRO CARGOS ---");
        Capitan capitan = Capitan.getInstancia(
                new Identidad("Capitan Haberes"), 2,
                new AsistenteDeComandos(
                        NaveFactory.crearNave(TipoNave.EXPLORADORA, 30),
                        Bitacora.getInstance()));
        Consejero consejero = new Consejero(new Identidad("Consejero Haberes"), 2);
        Teniente teniente = new Teniente(new Identidad("Teniente Haberes"), 2);
        Alferez alferez = new Alferez(new Identidad("Alferez Haberes"), 2);

        // Un consejero registra dos consejos: 2 PG por cada uno.
        consejero.registrarConsejo("Revisar condiciones de la nave");
        consejero.registrarConsejo("Planificar la siguiente ruta");

        Tripulante[] cargos = {capitan, consejero, teniente, alferez};
        double[] sueldoEsperado = {1400, 664, 424, 202};

        for (int i = 0; i < cargos.length; i++) {
            Tripulante tripulante = cargos[i];
            System.out.println(tripulante.getCargo() + ": base + antiguedad"
                    + " (+ consejos en el Consejero) = " + tripulante.getSueldo()
                    + " PG (esperado: " + sueldoEsperado[i] + ")");
            if (Math.abs(tripulante.getSueldo() - sueldoEsperado[i]) > 0.0001) {
                throw new IllegalStateException("ERROR: haber incorrecto para " + tripulante.getCargo());
            }
        }

        // 2. Aplicar cada uno de los tres decoradores por origen a cada cargo.
        System.out.println("\n--- 2. ORIGENES (12 COMBINACIONES) ---");
        for (Tripulante cargo : cargos) {
            Haber tierra = new Terricola(cargo);
            Haber marte = new Marciano(cargo);
            Haber vulcano = new Vulcano(cargo);

            System.out.println(cargo.getCargo()
                    + " | Terricola (+20): " + tierra.getSueldo()
                    + " | Marciano (+18): " + marte.getSueldo()
                    + " | Vulcano (+30): " + vulcano.getSueldo());

            if (Math.abs(tierra.getSueldo() - (cargo.getSueldo() + 20)) > 0.0001
                    || Math.abs(marte.getSueldo() - (cargo.getSueldo() + 18)) > 0.0001
                    || Math.abs(vulcano.getSueldo() - (cargo.getSueldo() + 30)) > 0.0001) {
                throw new IllegalStateException("ERROR: subsidio incorrecto para " + cargo.getCargo());
            }
        }

        // 3. Demostrar que la implementación permite anidar dos Decorators.
        // NOTA: no es una liquidación válida de un único origen, porque combina dos planetas.
        // Falta un segundo tipo de decorador que represente OTRO concepto remunerativo.
        System.out.println("\n--- 3. COMPOSICION TECNICA DE DOS DECORADORES ---");
        Haber dosDecoradores = new Marciano(new Terricola(alferez));
        System.out.println("Alferez: " + alferez.getSueldo() + " PG");
        System.out.println("Cadena Alferez + Terricola + Marciano: "
                + dosDecoradores.getSueldo() + " PG (esperado: 240)");
        if (Math.abs(dosDecoradores.getSueldo() - 240) > 0.0001) {
            throw new IllegalStateException("ERROR: composición Decorator incorrecta");
        }
        System.out.println("ADVERTENCIA: el ejemplo solo demuestra anidamiento tecnico."
                + " Una persona no deberia tener dos origenes a la vez.");
        System.out.println("[PENDIENTE E1-08] Hace falta un decorador para un concepto distinto "
                + "al origen. Dos origenes apilados NO constituyen una liquidacion valida.");

        System.out.println("\n[OK] Calculados los 4 cargos y los 3 subsidios sin usar JUnit");
        System.out.println("=== FIN DE LA DEMOSTRACION DE HABERES ===");
    }
}
