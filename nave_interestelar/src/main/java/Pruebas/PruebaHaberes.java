
package Pruebas;

import modelo.Tripulacion.Alferez;
import modelo.Tripulacion.Capitan;
import modelo.Tripulacion.Consejero;
import modelo.Tripulacion.Haber;
import modelo.Tripulacion.Identidad;
import modelo.Tripulacion.Marciano;
import modelo.Tripulacion.Teniente;
import modelo.Tripulacion.Terricola;
import modelo.Tripulacion.Tripulante;
import modelo.Tripulacion.Vulcano;
import modelo.asistenteDeComandos.AsistenteDeComandos;
import modelo.bitacora.Bitacora;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;

/**
 * Comprueba los haberes por cargo, antiguedad,
 * consejos y origen planetario.
 *
 * Verifica el uso de Decorator sin acumular
 * subsidios de diferentes origenes.
 */
public class PruebaHaberes {

    private static final double TOLERANCIA = 0.0001;

    public static void main(String[] args) {

        System.out.println(
                "=== PRUEBA DE HABERES (DECORATOR) ==="
        );

        // =====================================
        // 1. CUATRO CARGOS
        // =====================================

        Capitan capitan = Capitan.getInstancia(
                new Identidad("Capitan Haberes"),
                2,
                new AsistenteDeComandos(
                        NaveFactory.crearNave(
                                TipoNave.EXPLORADORA, 30
                        ),
                        Bitacora.getInstance()
                )
        );

        Consejero consejero = new Consejero(
                new Identidad("Consejero Haberes"), 2
        );

        Teniente teniente = new Teniente(
                new Identidad("Teniente Haberes"), 2
        );

        Alferez alferez = new Alferez(
                new Identidad("Alferez Haberes"), 2
        );

        consejero.registrarConsejo(
                "Revisar condiciones de la nave"
        );

        consejero.registrarConsejo(
                "Planificar la siguiente ruta"
        );

        Tripulante[] cargos = {
                capitan, consejero, teniente, alferez
        };

        double[] esperados = {
                1400, 664, 424, 202
        };

        System.out.println(
                "\n--- 1. CUATRO CARGOS ---"
        );

        for (int i = 0; i < cargos.length; i++) {

            Tripulante tripulante = cargos[i];

            exigirIgual(
                    tripulante.getSueldo(),
                    esperados[i],
                    "Haber incorrecto para "
                            + tripulante.getCargo()
            );

            System.out.println(
                    tripulante.getCargo()
                            + ": "
                            + tripulante.getSueldo()
                            + " PG [OK]"
            );
        }

        exigir(
                consejero.getCantidadConsejos() == 2,
                "El consejero debe registrar dos consejos"
        );

        // =====================================
        // 2. DECORADORES POR ORIGEN
        // =====================================

        System.out.println(
                "\n--- 2. DECORADORES (12 COMBINACIONES) ---"
        );

        for (Tripulante cargo : cargos) {

            Haber tierra = new Terricola(cargo);
            Haber marte = new Marciano(cargo);
            Haber vulcano = new Vulcano(cargo);

            exigirIgual(
                    tierra.getSueldo(),
                    cargo.getSueldo() + 20,
                    "Subsidio Terricola incorrecto para "
                            + cargo.getCargo()
            );

            exigirIgual(
                    marte.getSueldo(),
                    cargo.getSueldo() + 18,
                    "Subsidio Marciano incorrecto para "
                            + cargo.getCargo()
            );

            exigirIgual(
                    vulcano.getSueldo(),
                    cargo.getSueldo() + 30,
                    "Subsidio Vulcano incorrecto para "
                            + cargo.getCargo()
            );

            System.out.println(
                    cargo.getCargo()
                            + " | Terricola: "
                            + tierra.getSueldo()
                            + " | Marciano: "
                            + marte.getSueldo()
                            + " | Vulcano: "
                            + vulcano.getSueldo()
                            + " [OK]"
            );
        }

        // =====================================
        // 3. ASIGNACION Y REEMPLAZO DE ORIGEN
        // =====================================

        System.out.println(
                "\n--- 3. ASIGNACION Y REEMPLAZO DE ORIGEN ---"
        );

        double base = alferez.getSueldo();

        // Sin origen debe devolver el sueldo base.
        exigirIgual(
                alferez.getSueldoConOrigen(),
                base,
                "Sin origen debe devolver el sueldo base"
        );

        // Asignar origen Terricola: +20.
        verificarOrigen(
                alferez,
                Tripulante.Origen.TERRICOLA,
                base + 20
        );

        // Cambiar a Marciano: +18.
        // No debe acumular el subsidio anterior.
        verificarOrigen(
                alferez,
                Tripulante.Origen.MARCIANO,
                base + 18
        );

        // Cambiar a Vulcano: +30.
        // Nuevamente reemplaza el origen anterior.
        verificarOrigen(
                alferez,
                Tripulante.Origen.VULCANO,
                base + 30
        );

        // El cambio de origen no modifica
        // el sueldo propio del cargo.
        exigirIgual(
                alferez.getSueldo(),
                base,
                "Cambiar de origen no debe alterar "
                        + "el sueldo del cargo"
        );

        // =====================================
        // 4. VALIDACION DE ORIGEN INVALIDO
        // =====================================

        System.out.println(
                "\n--- 4. ORIGEN INVALIDO ---"
        );

        boolean rechazoNull = false;

        try {
            alferez.asignarOrigen(null);

        } catch (IllegalArgumentException e) {
            rechazoNull = true;
        }

        exigir(
                rechazoNull,
                "Se debe rechazar un origen null"
        );

        // El intento fallido no debe modificar
        // el subsidio que ya estaba asignado.
        exigirIgual(
                alferez.getSueldoConOrigen(),
                base + 30,
                "Un origen invalido no debe cambiar "
                        + "el subsidio vigente"
        );

        System.out.println(
                "[OK] Origen invalido rechazado"
        );

        // =====================================
        // RESULTADO FINAL
        // =====================================

        System.out.println(
                "\n[OK] Cuatro cargos, doce subsidios "
                        + "y reemplazo de origen"
        );

        System.out.println(
                "=== FIN DE PRUEBA DE HABERES ==="
        );
    }

    /**
     * Comprueba la asignacion de un origen.
     *
     * Pre:
     * - tripulante != null.
     * - origen != null.
     *
     * Post:
     * - Comprueba el origen almacenado.
     * - Comprueba el sueldo con subsidio.
     */
    private static void verificarOrigen(
            Tripulante tripulante,
            Tripulante.Origen origen,
            double sueldoEsperado) {

        tripulante.asignarOrigen(origen);

        exigir(
                origen.toString().equals(
                        tripulante.getOrigen()
                ),
                "El origen guardado no coincide con "
                        + origen
        );

        exigirIgual(
                tripulante.getSueldoConOrigen(),
                sueldoEsperado,
                "El origen "
                        + origen
                        + " no reemplazo correctamente "
                        + "el anterior"
        );

        System.out.println(
                origen
                        + ": "
                        + tripulante.getSueldoConOrigen()
                        + " PG [OK]"
        );
    }

    /**
     * Verifica dos importes con una tolerancia.
     */
    private static void exigirIgual(
            double obtenido,
            double esperado,
            String mensaje) {

        exigir(
                Math.abs(obtenido - esperado) < TOLERANCIA,
                mensaje
                        + " (obtenido=" + obtenido
                        + ", esperado=" + esperado + ")"
        );
    }

    /**
     * Falla la prueba si no se cumple la condicion.
     */
    private static void exigir(
            boolean condicion,
            String mensaje) {

        if (!condicion) {
            throw new IllegalStateException(
                    "ERROR: " + mensaje
            );
        }
    }
}
