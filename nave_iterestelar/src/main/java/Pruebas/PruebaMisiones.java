package pruebas;

import modelo.Tripulacion.*;
import modelo.asistenteDeComandos.AsistenteDeComandos;
import modelo.bitacora.Bitacora;
import modelo.misiones.*;
import modelo.nave.*;

/** Demostración de una misión exitosa y otra cancelada por falta de recursos. */
public class PruebaMisiones {

    public static void main(String[] args) {

        System.out.println("=== SISTEMA DE MISIONES ESPACIALES ===");

        // 1. Crear la nave y su asistente
        Nave nave = NaveFactory.crearNave(TipoNave.EXPLORADORA, 1);
        Bitacora bitacora = Bitacora.getInstance();
        AsistenteDeComandos asistente =
                new AsistenteDeComandos(nave, bitacora);

        System.out.println("\n--- NAVE CREADA ---");
        System.out.println("Tipo: " + nave.getClass().getSimpleName());
        System.out.println(asistente.snapshotEstadoNave());

        // 2. Formar tripulación mínima
        System.out.println("\n--- ASIGNANDO TRIPULACIÓN ---");
        Capitan capitan = Capitan.getInstancia(
                new Identidad("Santiago"), 2, asistente);
        nave.agregarTripulante(capitan);

        String[] nombres = {"Ana", "Juan", "Pedro", "María"};
        for (String nombre : nombres) {
            nave.agregarTripulante(new Alferez(new Identidad(nombre), 1));
        }
        System.out.println("Integrantes: " + nave.getTripulacion().size());
        System.out.println("Tripulación válida: " + nave.tieneTripulacionValida());

        // 3. Ejecutar misión de recolección con recursos suficientes
        System.out.println("\n--- MISIÓN 1: RECOLECCIÓN ---");
        Mision recoleccion = new Recoleccion(asistente, 10, 15, 5);
        capitan.ejecutarMision(recoleccion);
        System.out.println("Informe: " + recoleccion.getInforme());
        System.out.println("Recursos luego de Recolección:");
        System.out.println(asistente.snapshotEstadoNave());

        // 4. Intentar una misión con recursos insuficientes
        System.out.println("\n--- MISIÓN 2: INTERCEPCIÓN ---");
        Mision intercepcion = new Intercepcion(asistente, 100, 100, 10);
        capitan.ejecutarMision(intercepcion);
        if (intercepcion.getInforme() == null) {
            System.out.println("Misión cancelada en preparación: recursos insuficientes.");
        } else {
            System.out.println("Informe: " + intercepcion.getInforme());
        }

        // 5. Comprobar que la misión cancelada no gastó recursos
        System.out.println("\n--- ESTADO FINAL DE LA NAVE ---");
        System.out.println(asistente.snapshotEstadoNave());
        System.out.println("Esperado: Combustible 50, Energía 65, Desgaste 5");

        // 6. Mostrar los eventos reales de ambas misiones
        System.out.println("\n--- BITÁCORA COMPLETA ---");
        for (String evento : bitacora.getRegistro()) {
            System.out.println(evento);
        }

        System.out.println("\n=== FIN DE LA DEMOSTRACIÓN DE MISIONES ===");
    }
}
