package pruebas;

import modelo.bitacora.Bitacora;
import modelo.motorWarp.MotorWarp;

/** Demostración narrativa de las transiciones del patrón State. */
public class PruebaMotorWarp {

    public static void main(String[] args) {

        System.out.println("=== PRUEBA DEL MOTOR WARP ===");

        // 1. Crear motor: debe iniciar Disponible
        MotorWarp motor = new MotorWarp();
        Bitacora bitacora = Bitacora.getInstance();
        System.out.println("\n--- ESTADO INICIAL ---");
        System.out.println("Motor: " + motor.getState().getClass().getSimpleName());

        // 2. Intentar un salto sin preparación
        System.out.println("\n--- TRANSICIÓN INVÁLIDA ---");
        motor.entrarEnWarp();
        System.out.println("Se intentó entrar en Warp sin preparar.");
        System.out.println("Estado actual: " + motor.getState().getClass().getSimpleName()
                + " (esperado: Disponible)");

        // 3. Preparar el salto
        System.out.println("\n--- PREPARAR SALTO ---");
        motor.prepararSalto();
        System.out.println("Estado actual: " + motor.getState().getClass().getSimpleName()
                + " (esperado: PreparandoSalto)");

        // 4. Entrar en Warp
        System.out.println("\n--- ENTRAR EN WARP ---");
        motor.entrarEnWarp();
        System.out.println("Estado actual: " + motor.getState().getClass().getSimpleName()
                + " (esperado: EnWarp)");

        // 5. Comenzar enfriamiento
        System.out.println("\n--- ENFRIAMIENTO ---");
        motor.iniciarEnfriamiento();
        System.out.println("Estado actual: " + motor.getState().getClass().getSimpleName()
                + " (esperado: Enfriamento)");

        // 6. Volver a Disponible
        System.out.println("\n--- FIN DEL CICLO ---");
        motor.volverAlDisponible();
        System.out.println("Estado actual: " + motor.getState().getClass().getSimpleName()
                + " (esperado: Disponible)");

        // 7. Mostrar bitácora, que debe incluir el rechazo inicial
        System.out.println("\n--- BITÁCORA DEL MOTOR ---");
        for (String evento : bitacora.getRegistro()) {
            System.out.println(evento);
        }

        System.out.println("\n=== FIN DE LA PRUEBA DEL MOTOR WARP ===");
    }
}
