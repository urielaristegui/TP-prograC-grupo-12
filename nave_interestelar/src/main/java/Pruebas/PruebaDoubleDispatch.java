
package Pruebas;

import modelo.Tripulacion.Alferez;
import modelo.Tripulacion.Identidad;
import modelo.nave.Nave;
import modelo.nave.NaveFactory;
import modelo.nave.ResultadoCombate;
import modelo.nave.TipoNave;

/**
 * Prueba automatica del Double Dispatch y sus efectos.
 *
 * Comprueba las nueve combinaciones de naves desde la
 * perspectiva de la nave propia, sin utilizar el tablero.
 */
public final class PruebaDoubleDispatch {

    private PruebaDoubleDispatch() {
    }

    public static void main(String[] args) {

        System.out.println("=== PRUEBA DOUBLE DISPATCH ===");

        TipoNave[] tipos = {
                TipoNave.EXPLORADORA,
                TipoNave.CARGUERO,
                TipoNave.COMBATE
        };

        // Filas: nave propia. Columnas: nave enemiga.
        ResultadoCombate[][] esperados = {
                {
                        ResultadoCombate.PACTO_DE_PAZ,
                        ResultadoCombate.GANA,
                        ResultadoCombate.PIERDE
                },
                {
                        ResultadoCombate.PIERDE,
                        ResultadoCombate.PACTO_DE_PAZ,
                        ResultadoCombate.GANA
                },
                {
                        ResultadoCombate.GANA,
                        ResultadoCombate.PIERDE,
                        ResultadoCombate.PACTO_DE_PAZ
                }
        };

        int casos = 0;

        for (int i = 0; i < tipos.length; i++) {
            for (int j = 0; j < tipos.length; j++) {

                int numero = i * tipos.length + j;

                Nave propia = NaveFactory.crearNave(
                        tipos[i], 5000 + numero
                );

                Nave enemiga = NaveFactory.crearNave(
                        tipos[j], 6000 + numero
                );

                // Tripulante para verificar que se elimine
                // cuando la nave pierde el combate.
                propia.agregarTripulante(
                        new Alferez(
                                new Identidad("Alferez-" + numero),
                                1
                        )
                );

                int combustible = propia.getCombustible();
                int energia = propia.getEnergia();
                int desgaste = propia.getDesgaste();

                ResultadoCombate esperado = esperados[i][j];

                // =====================================
                // 1. VERIFICAR DOUBLE DISPATCH
                // =====================================

                ResultadoCombate obtenido =
                        propia.enfrentar(enemiga);

                exigir(
                        obtenido == esperado,
                        tipos[i] + " vs " + tipos[j]
                                + ": esperado " + esperado
                                + ", obtenido " + obtenido
                );

                // enfrentar() debe devolver el resultado
                // sin alterar los recursos ni destruir naves.

                exigir(
                        !propia.estaDestruida()
                                && propia.getCombustible() == combustible
                                && propia.getEnergia() == energia
                                && propia.getDesgaste() == desgaste
                                && propia.getTripulacion().size() == 1,
                        "enfrentar() modifico el estado de la nave"
                );

                // =====================================
                // 2. APLICAR EL RESULTADO DEL ENCUENTRO
                // =====================================

                enemiga.interactuar(propia);

                if (esperado == ResultadoCombate.PIERDE) {

                    // Si pierde, la nave propia
                    // debe quedar destruida.

                    exigir(
                            propia.estaDestruida(),
                            "Una derrota debe destruir la nave propia"
                    );

                    exigir(
                            propia.getCombustible() == 0
                                    && propia.getEnergia() == 0
                                    && propia.getDesgaste() == 100
                                    && propia.getTripulacion().isEmpty(),
                            "La destruccion debe afectar "
                                    + "recursos y tripulacion"
                    );

                } else {

                    // Si gana o hay pacto de paz,
                    // conserva sus recursos y tripulacion.

                    exigir(
                            !propia.estaDestruida(),
                            "Una victoria o pacto no debe "
                                    + "destruir la nave"
                    );

                    exigir(
                            propia.getCombustible() == combustible
                                    && propia.getEnergia() == energia
                                    && propia.getDesgaste() == desgaste
                                    && propia.getTripulacion().size() == 1,
                            "Victoria o pacto modifico "
                                    + "recursos o tripulacion"
                    );
                }

                // El encuentro no destruye al enemigo.

                exigir(
                        !enemiga.estaDestruida(),
                        "No debe destruirse la nave enemiga"
                );

                casos++;

                System.out.println(
                        "[OK] " + tipos[i]
                                + " vs " + tipos[j]
                                + " -> " + obtenido
                );
            }
        }

        // =====================================
        // 3. VALIDACIONES DE ENFRENTAMIENTO
        // =====================================

        probarValidaciones();

        System.out.println(
                "\n[OK] " + casos
                        + "/9 enfrentamientos "
                        + "y validaciones correctas"
        );
    }

    /**
     * Verifica el rechazo de rivales invalidos.
     *
     * Pre: ninguna.
     *
     * Post:
     * - Rechaza un rival null.
     * - Rechaza el autoenfrentamiento.
     * - Rechaza naves destruidas.
     */
    private static void probarValidaciones() {

        Nave propia = NaveFactory.crearNave(
                TipoNave.EXPLORADORA, 7001
        );

        Nave enemiga = NaveFactory.crearNave(
                TipoNave.CARGUERO, 7002
        );

        // Rival null.
        exigirExcepcion(
                IllegalArgumentException.class,
                () -> propia.enfrentar(null),
                "Rival null"
        );

        // No puede enfrentarse consigo misma.
        exigirExcepcion(
                IllegalArgumentException.class,
                () -> propia.enfrentar(propia),
                "Autoenfrentamiento"
        );

        // No puede enfrentarse a una nave destruida.
        enemiga.destruir();

        exigirExcepcion(
                IllegalStateException.class,
                () -> propia.enfrentar(enemiga),
                "Rival destruida"
        );

        // Una nave destruida no puede combatir.
        Nave otra = NaveFactory.crearNave(
                TipoNave.COMBATE, 7003
        );

        propia.destruir();

        exigirExcepcion(
                IllegalStateException.class,
                () -> propia.enfrentar(otra),
                "Nave propia destruida"
        );

        System.out.println(
                "[OK] Validacion de rivales invalidos"
        );
    }

    /**
     * Verifica que una operacion lance
     * la excepcion esperada.
     */
    private static void exigirExcepcion(
            Class<? extends RuntimeException> tipo,
            Runnable accion,
            String descripcion) {

        try {

            accion.run();

        } catch (RuntimeException e) {

            if (tipo.isInstance(e)) {
                return;
            }

            throw new AssertionError(
                    descripcion + ": excepcion inesperada",
                    e
            );
        }

        throw new AssertionError(
                descripcion + ": no se lanzo "
                        + tipo.getSimpleName()
        );
    }

    /**
     * Falla la prueba si la condicion no se cumple.
     */
    private static void exigir(
            boolean condicion,
            String mensaje) {

        if (!condicion) {

            throw new AssertionError(
                    "ERROR: " + mensaje
            );
        }
    }
}
