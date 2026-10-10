package Pruebas;

import modelo.nave.Nave;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;

/** Escenario D de la ficha E1: contratos e invariantes de recursos. */
public class EscenarioD {

    public static void main(String[] args) {
        System.out.println("=== ESCENARIO D: CONTRATOS INVALIDOS ===");

        // 1. Crear Exploradora: 60 combustible, 80 energía, 0 desgaste.
        Nave nave = NaveFactory.crearNave(TipoNave.EXPLORADORA, 20);
        System.out.println("\n--- 1. ESTADO INICIAL ---");
        System.out.println("Combustible: " + nave.getCombustible());
        System.out.println("Energia: " + nave.getEnergia());
        System.out.println("Desgaste: " + nave.getDesgaste());

        // 2. La carga de 50 combustible superaría la capacidad 100.
        System.out.println("\n--- 2. CARGA EXCESIVA DE COMBUSTIBLE ---");
        boolean combustibleRechazado = false;
        try {
            nave.cargarCombustible(50);
            System.out.println("ERROR: se acepto una carga excesiva");
        } catch (IllegalArgumentException e) {
            combustibleRechazado = true;
            System.out.println("Rechazo correcto: " + e.getMessage());
        }
        System.out.println("Combustible tras rechazo: " + nave.getCombustible()
                + " (esperado: 60)");

        // 3. La carga de 30 energía superaría la capacidad 100.
        System.out.println("\n--- 3. CARGA EXCESIVA DE ENERGIA ---");
        boolean energiaRechazada = false;
        try {
            nave.cargarEnergia(30);
            System.out.println("ERROR: se acepto una carga excesiva");
        } catch (IllegalArgumentException e) {
            energiaRechazada = true;
            System.out.println("Rechazo correcto: " + e.getMessage());
        }
        System.out.println("Energia tras rechazo: " + nave.getEnergia()
                + " (esperado: 80)");

        // 4. Intentar consumir 61 combustible: no debe descontar nada.
        System.out.println("\n--- 4. CONSUMO INVALIDO: NO HAY CAMBIOS PARCIALES ---");
        boolean consumoRechazado = false;
        try {
            nave.consumirRecursos(61, 10, 5);
            System.out.println("ERROR: se acepto un consumo invalido");
        } catch (IllegalArgumentException e) {
            consumoRechazado = true;
            System.out.println("Rechazo correcto: " + e.getMessage());
        }

        System.out.println("\n--- 5. ESTADO FINAL ---");
        System.out.println("Combustible: " + nave.getCombustible() + " (esperado: 60)");
        System.out.println("Energia: " + nave.getEnergia() + " (esperado: 80)");
        System.out.println("Desgaste: " + nave.getDesgaste() + " (esperado: 0)");

        if (!combustibleRechazado || !energiaRechazada || !consumoRechazado
                || nave.getCombustible() != 60 || nave.getEnergia() != 80
                || nave.getDesgaste() != 0) {
            throw new IllegalStateException("ERROR: se incumplio el contrato de recursos");
        }

        // 6. Mostrar el mantenimiento sin alterar el combustible ni la energía.
        System.out.println("\n--- 6. MANTENIMIENTO ---");
        nave.consumirRecursos(5, 5, 80);
        System.out.println("Desgaste antes: " + nave.getDesgaste());
        System.out.println("Requiere mantenimiento: " + nave.requiereMantenimiento());
        nave.realizarMantenimiento();
        System.out.println("Desgaste despues: " + nave.getDesgaste());
        System.out.println("Combustible y energia despues: "
                + nave.getCombustible() + " / " + nave.getEnergia());
        if (nave.getDesgaste() != 0 || nave.getCombustible() != 55
                || nave.getEnergia() != 75) {
            throw new IllegalStateException("ERROR: mantenimiento incorrecto");
        }

        System.out.println("\n[OK] Contratos, atomicidad y mantenimiento verificados");
        System.out.println("=== FIN DEL ESCENARIO D ===");
    }
}
