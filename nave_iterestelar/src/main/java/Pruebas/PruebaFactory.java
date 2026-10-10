package pruebas;

import modelo.nave.*;

/** Demostración manual de la creación de naves mediante Simple Factory. */
public class PruebaFactory {

    public static void main(String[] args) {

        System.out.println("=== PRUEBA DE NAVE FACTORY ===");

        // 1. Crear una nave exploradora
        System.out.println("\n--- NAVE EXPLORADORA ---");
        Nave exploradora = NaveFactory.crearNave(TipoNave.EXPLORADORA, 1);
        System.out.println("Tipo creado: " + exploradora.getClass().getSimpleName());
        System.out.println("ID: " + exploradora.getId());
        System.out.println("Combustible: " + exploradora.getCombustible() + " (esperado: 60)");
        System.out.println("Energía: " + exploradora.getEnergia() + " (esperado: 80)");

        // 2. Crear un carguero
        System.out.println("\n--- NAVE CARGUERO ---");
        Nave carguero = NaveFactory.crearNave(TipoNave.CARGUERO, 2);
        System.out.println("Tipo creado: " + carguero.getClass().getSimpleName());
        System.out.println("ID: " + carguero.getId());
        System.out.println("Combustible: " + carguero.getCombustible() + " (esperado: 100)");
        System.out.println("Energía: " + carguero.getEnergia() + " (esperado: 60)");

        // 3. Crear una nave de combate
        System.out.println("\n--- NAVE COMBATE ---");
        Nave combate = NaveFactory.crearNave(TipoNave.COMBATE, 3);
        System.out.println("Tipo creado: " + combate.getClass().getSimpleName());
        System.out.println("ID: " + combate.getId());
        System.out.println("Combustible: " + combate.getCombustible() + " (esperado: 80)");
        System.out.println("Energía: " + combate.getEnergia() + " (esperado: 100)");

        // 4. Intentar crear una nave sin tipo
        System.out.println("\n--- TIPO DE NAVE INVÁLIDO ---");
        try {
            NaveFactory.crearNave(null, 4);
            System.out.println("ERROR: se creó una nave con tipo null.");
        } catch (IllegalArgumentException e) {
            System.out.println("Creación rechazada correctamente: " + e.getMessage());
        }

        System.out.println("\n=== FIN DE LA PRUEBA DE FACTORY ===");
    }
}
