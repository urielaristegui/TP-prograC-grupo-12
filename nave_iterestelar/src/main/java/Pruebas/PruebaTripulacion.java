package pruebas;

import modelo.Tripulacion.*;
import modelo.asistenteDeComandos.AsistenteDeComandos;
import modelo.bitacora.Bitacora;
import modelo.nave.*;

/** Demostración de la composición de la tripulación y sus validaciones. */
public class PruebaTripulacion {

    public static void main(String[] args) {

        System.out.println("=== PRUEBA DE TRIPULACIÓN ===");

        // 1. Crear nave y asistente
        Nave nave = NaveFactory.crearNave(TipoNave.CARGUERO, 20);
        AsistenteDeComandos asistente =
                new AsistenteDeComandos(nave, Bitacora.getInstance());

        System.out.println("\n--- NAVE SIN TRIPULACIÓN ---");
        System.out.println("Cantidad: " + nave.getTripulacion().size());
        System.out.println("¿Tripulación válida?: " + nave.tieneTripulacionValida()
                + " (esperado: false)");

        // 2. Agregar cuatro alféreces; todavía no alcanza
        System.out.println("\n--- AGREGAR CUATRO ALFÉRECES ---");
        String[] nombres = {"Ana", "Juan", "Pedro", "María"};
        for (String nombre : nombres) {
            nave.agregarTripulante(new Alferez(new Identidad(nombre), 1));
        }
        System.out.println("Cantidad: " + nave.getTripulacion().size());
        System.out.println("¿Tripulación válida?: " + nave.tieneTripulacionValida()
                + " (esperado: false)");

        // 3. Agregar al capitán para completar cinco integrantes
        System.out.println("\n--- AGREGAR CAPITÁN ---");
        Capitan capitan = Capitan.getInstancia(
                new Identidad("Santiago"), 2, asistente);
        nave.agregarTripulante(capitan);
        System.out.println("Cantidad: " + nave.getTripulacion().size());
        System.out.println("¿Tripulación válida?: " + nave.tieneTripulacionValida()
                + " (esperado: true)");

        // 4. Buscar un tripulante por su ID actual (nombre)
        System.out.println("\n--- BUSCAR TRIPULANTE ---");
        Tripulante encontrado = nave.buscarTripulante("Ana");
        if (encontrado != null) {
            System.out.println("Encontrado: " + encontrado.getID()
                    + " - cargo: " + encontrado.getCargo());
        } else {
            System.out.println("No se encontró el tripulante Ana.");
        }

        // 5. Probar el rechazo de un identificador duplicado
        System.out.println("\n--- INTENTAR AGREGAR UN ID DUPLICADO ---");
        try {
            nave.agregarTripulante(new Alferez(new Identidad("Ana"), 3));
            System.out.println("ERROR: se aceptó un ID duplicado.");
        } catch (IllegalArgumentException e) {
            System.out.println("Duplicado rechazado correctamente: " + e.getMessage());
        }
        System.out.println("Cantidad final: " + nave.getTripulacion().size()
                + " (esperado: 5)");

        // 6. Mostrar integrantes
        System.out.println("\n--- TRIPULACIÓN FINAL ---");
        for (Tripulante tripulante : nave.getTripulacion()) {
            System.out.println(tripulante.getID() + " - " + tripulante.getCargo());
        }

        System.out.println("\n=== FIN DE LA PRUEBA DE TRIPULACIÓN ===");
    }
}
