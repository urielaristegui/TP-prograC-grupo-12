package pruebas;

import modelo.nave.*;

/** Demostración de consumos, cargas, mantenimiento y validación de recursos. */
public class PruebaRecursos {

    public static void main(String[] args) {

        System.out.println("=== PRUEBA DE RECURSOS DE UNA NAVE ===");

        // 1. Crear nave y mostrar su estado inicial
        Nave nave = NaveFactory.crearNave(TipoNave.EXPLORADORA, 10);
        System.out.println("\n--- ESTADO INICIAL ---");
        System.out.println("Combustible: " + nave.getCombustible());
        System.out.println("Energía: " + nave.getEnergia());
        System.out.println("Desgaste: " + nave.getDesgaste());

        // 2. Ejecutar un consumo válido
        System.out.println("\n--- CONSUMO NORMAL (10, 15, 5) ---");
        nave.consumirRecursos(10, 15, 5);
        System.out.println("Combustible: " + nave.getCombustible() + " (esperado: 50)");
        System.out.println("Energía: " + nave.getEnergia() + " (esperado: 65)");
        System.out.println("Desgaste: " + nave.getDesgaste() + " (esperado: 5)");

        // 3. Cargar combustible y energía
        System.out.println("\n--- RECARGAR RECURSOS ---");
        nave.cargarCombustible(20);
        nave.cargarEnergia(10);
        System.out.println("Combustible: " + nave.getCombustible() + " (esperado: 70)");
        System.out.println("Energía: " + nave.getEnergia() + " (esperado: 75)");

        // 4. Intentar un consumo superior a los recursos disponibles
        System.out.println("\n--- CONSUMO INVÁLIDO ---");
        try {
            nave.consumirRecursos(90, 5, 10);
            System.out.println("ERROR: se permitió consumir combustible insuficiente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Consumo rechazado correctamente: " + e.getMessage());
        }
        System.out.println("Estado luego del intento (debe seguir igual):");
        System.out.println("Combustible: " + nave.getCombustible());
        System.out.println("Energía: " + nave.getEnergia());
        System.out.println("Desgaste: " + nave.getDesgaste());

        // 5. Alcanzar el umbral de mantenimiento
        System.out.println("\n--- MANTENIMIENTO ---");
        nave.consumirRecursos(0, 0, 75);
        System.out.println("Desgaste: " + nave.getDesgaste() + " (esperado: 80)");
        System.out.println("¿Requiere mantenimiento?: " + nave.requiereMantenimiento()
                + " (esperado: true)");
        nave.realizarMantenimiento();
        System.out.println("Desgaste después de mantenimiento: " + nave.getDesgaste()
                + " (esperado: 0)");
        System.out.println("¿Requiere mantenimiento?: " + nave.requiereMantenimiento()
                + " (esperado: false)");

        System.out.println("\n=== FIN DE LA PRUEBA DE RECURSOS ===");
    }
}
