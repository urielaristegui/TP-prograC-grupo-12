import modelo.Tripulacion.*;
import modelo.asistenteDeComandos.AsistenteDeComandos;
import modelo.bitacora.Bitacora;
import modelo.motorWarp.MotorWarp;
import modelo.nave.Nave;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;
/**
 * Prueba manual de las operaciones principales del proyecto.
 */
public final class Prueba {
    public static void main(String[] args) {
        System.out.println("--- INICIANDO PRUEBA DE EVIDENCIAS FUNCIONALES ---");

        // 1. Prueba de invariante en Bitácora: Forzar un evento nulo/vacío
        System.out.println("\n1. Probando invariante de Bitácora...");
        Bitacora bitacora = Bitacora.getInstance();
        try {
            bitacora.registrar("");
        } catch (IllegalArgumentException e) {
            // Se captura la excepción en el cliente para evitar que el programa colapse
            System.out.println("Validación exitosa - El sistema rechazó el evento: " + e.getMessage());
        }

        // 2. Prueba del Motor Warp: Secuencia Válida
        System.out.println("\n2. Ejecutando secuencia válida del Motor Warp...");
        MotorWarp motor = new MotorWarp(); // La fábrica asegurará que inicie en estado válido (Disponible)

        motor.prepararSalto();        // Disponible -> Preparando Salto
        motor.entrarEnWarp();         // Preparando Salto -> En Warp
        motor.iniciarEnfriamiento();  // En Warp -> Enfriamiento
        motor.volverAlDisponible();   // Enfriamiento -> Disponible

        // 3. Prueba del Motor Warp: Transición Inválida
        System.out.println("\n3. Forzando transición inválida...");
        // El motor está Disponible, intentar entrar en Warp directamente debe ser rechazado
        motor.entrarEnWarp();

        // 4. Comprobación de separación de E/S y orden cronológico
        System.out.println("\n--- EXTRACCIÓN DEL REGISTRO TEMPORAL (BITÁCORA) ---");
        // El Main solicita los datos y se encarga de imprimirlos, protegiendo el dominio
        for (String evento : Bitacora.getInstance().getRegistro()) {
            System.out.println(evento);
        }
    }
}