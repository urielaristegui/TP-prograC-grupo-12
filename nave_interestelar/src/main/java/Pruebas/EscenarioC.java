package Pruebas;

import modelo.bitacora.Bitacora;
import modelo.motorWarp.MotorWarp;
import modelo.nave.Nave;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;

/**
 * Comprueba el ciclo completo del patron State
 * utilizando exclusivamente las operaciones
 * publicas del motor.
 */
public class EscenarioC {

    public static void main(String[] args) {

        Nave nave = NaveFactory.crearNave(
                TipoNave.EXPLORADORA, 7001
        );

        MotorWarp motor = nave.getMotorWarp();

        Bitacora.getInstance().getRegistro().clear();

        int energiaInicial = nave.getEnergia();
        int desgasteInicial = nave.getDesgaste();
        int combustibleInicial = nave.getCombustible();

        // TURNO 1: preparacion
        exigir(
                !motor.saltarWarp(nave),
                "Turno 1: solo debe preparar"
        );

        // TURNO 2: preparacion
        exigir(
                !motor.saltarWarp(nave),
                "Turno 2: solo debe preparar"
        );

        exigir(
                nave.getEnergia() == energiaInicial,
                "La preparacion no debe consumir energia"
        );

        // TURNO 3: salto Warp
        exigir(
                motor.saltarWarp(nave),
                "Turno 3: debe autorizar Warp"
        );

        exigir(
                nave.getEnergia() == energiaInicial - 5,
                "Warp debe consumir 5 de energia"
        );

        exigir(
                nave.getDesgaste() == desgasteInicial,
                "El Warp no debe generar desgaste antes del enfriamiento"
        );

        // TURNO 4: enfriamiento
        exigir(
                !motor.saltar(nave),
                "Turno 4: debe permanecer enfriando"
        );

        exigir(
                nave.getDesgaste() == desgasteInicial + 3,
                "El enfriamiento debe generar exactamente 3 de desgaste"
        );

        // TURNO 5: salto normal
        exigir(
                motor.saltar(nave),
                "Turno 5: debe estar disponible"
        );

        exigir(
                nave.getCombustible() == combustibleInicial - 2,
                "El salto normal debe consumir 2 de combustible"
        );

        System.out.println(
                "[OK] State, preparacion, Warp, "
                        + "enfriamiento y salto normal"
        );
    }

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