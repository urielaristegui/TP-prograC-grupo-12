package Pruebas;

import modelo.bitacora.Bitacora;
import modelo.motorWarp.Disponible;
import modelo.motorWarp.EnWarp;
import modelo.motorWarp.Enfriamento;
import modelo.motorWarp.MotorWarp;
import modelo.motorWarp.PreparandoSalto;

/** Escenario C de la ficha E1: transiciones State válidas e inválidas. */
public class EscenarioC {

    public static void main(String[] args) {
        System.out.println("=== ESCENARIO C: MOTOR WARP (STATE) ===");

        // 1. Crear un motor independiente en estado Disponible.
        MotorWarp motor = new MotorWarp();
        Bitacora bitacora = Bitacora.getInstance();
        System.out.println("\n--- 1. ESTADO INICIAL ---");
        System.out.println("Actual: " + motor.getState().getClass().getSimpleName());
        if (!(motor.getState() instanceof Disponible)) {
            throw new IllegalStateException("ERROR: el motor no inicia Disponible");
        }

        // 2. Intentar entrar en Warp sin haber preparado el salto.
        System.out.println("\n--- 2. TRANSICION INVALIDA ---");
        int registrosAntes = bitacora.getRegistro().size();
        motor.entrarEnWarp();
        System.out.println("Se intento entrar en Warp directamente desde Disponible.");
        System.out.println("Actual: " + motor.getState().getClass().getSimpleName()
                + " (esperado: Disponible)");
        boolean hayError = false;
        for (int i = registrosAntes; i < bitacora.getRegistro().size(); i++) {
            String evento = bitacora.getRegistro().get(i);
            System.out.println("Evento de rechazo: " + evento);
            if (evento.contains("ERROR")) {
                hayError = true;
            }
        }
        if (!(motor.getState() instanceof Disponible) || !hayError) {
            throw new IllegalStateException("ERROR: la transicion invalida no se rechazo/registro");
        }

        // 3. Secuencia válida de State.
        System.out.println("\n--- 3. SECUENCIA VALIDA ---");
        motor.prepararSalto();
        System.out.println("Preparar: " + motor.getState().getClass().getSimpleName());
        if (!(motor.getState() instanceof PreparandoSalto)) {
            throw new IllegalStateException("ERROR: no paso a PreparandoSalto");
        }

        motor.entrarEnWarp();
        System.out.println("Entrar en Warp: " + motor.getState().getClass().getSimpleName());
        if (!(motor.getState() instanceof EnWarp)) {
            throw new IllegalStateException("ERROR: no paso a EnWarp");
        }

        motor.iniciarEnfriamiento();
        System.out.println("Enfriar: " + motor.getState().getClass().getSimpleName());
        if (!(motor.getState() instanceof Enfriamento)) {
            throw new IllegalStateException("ERROR: no paso a Enfriamento");
        }

        motor.volverAlDisponible();
        System.out.println("Restablecer: " + motor.getState().getClass().getSimpleName());
        if (!(motor.getState() instanceof Disponible)) {
            throw new IllegalStateException("ERROR: no regreso a Disponible");
        }

        // 4. Todos los cambios deben quedar en la bitácora.
        System.out.println("\n--- 4. BITACORA ---");
        for (String evento : bitacora.getRegistro()) {
            System.out.println(evento);
        }
        System.out.println("\n[OK] Transicion invalida registrada y secuencia State completa");
        System.out.println("=== FIN DEL ESCENARIO C ===");
    }
}
