package Pruebas;

import modelo.destinoytablero.CantidadDeElementosSuperaCasillaException;
import modelo.destinoytablero.DimensionesDeTableroInconsistentesException;
import modelo.elementos.ElementoDelUniverso;
import modelo.nave.Nave;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;
import modelo.destinoytablero.Tablero;
import modelo.destinoytablero.Posicion;
import modelo.asistenteDeComandos.AsistenteDeComandos;
import modelo.bitacora.Bitacora;
import modelo.sensores.Sensores;
import modelo.sensores.ReglasNavegacion;
import modelo.Tripulacion.Capitan;
import modelo.Tripulacion.Teniente;
import modelo.Tripulacion.Consejero;
import modelo.Tripulacion.Alferez;
import modelo.Tripulacion.Identidad;

import java.util.ArrayList;

/**
 * Prueba ejecutable de integración para el subsistema de Navegación,
 * Sensores, AsistenteDeComandos y Bitácora.
 */
public class Prueba_reglas {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  PRUEBA DE INTEGRACIÓN: SENSORES Y NAVEGACIÓN    ");
        System.out.println("==================================================\n");

        // 1. Inicialización de componentes base del Tablero y la Nave
        ArrayList<ElementoDelUniverso> elementosIniciales = new ArrayList<>();

        Tablero tablero = null;
        try {
            tablero = new Tablero(10, 10, elementosIniciales);
        } catch (DimensionesDeTableroInconsistentesException | CantidadDeElementosSuperaCasillaException e) {
            throw new RuntimeException("Error al inicializar el tablero: " + e.getMessage(), e);
        }
        Nave nave = NaveFactory.crearNave(TipoNave.EXPLORADORA, 1);
        Bitacora bitacora = Bitacora.getInstance();

        // 2. Creación del AsistenteDeComandos (asigna el tablero y posiciona la nave en la base)
        AsistenteDeComandos asistente = new AsistenteDeComandos(nave, bitacora, tablero);

        // 3. Carga de la Tripulación mínima obligatoria (mínimo 5 integrantes)
        asistente.registrarEvento("Iniciando la carga de tripulación...");
        nave.agregarTripulante(Capitan.getInstancia(new Identidad("Capitán Picard"), 10, asistente));
        nave.agregarTripulante(new Teniente(new Identidad("Data"), 9));
        nave.agregarTripulante(new Consejero(new Identidad("Troi"), 7));
        nave.agregarTripulante(new Alferez(new Identidad("Crusher"), 5));
        nave.agregarTripulante(new Alferez(new Identidad("La Forge"), 5));

        System.out.println("¿Tripulación válida? (esperado true): " + nave.tieneTripulacionValida());
        System.out.println("Posición inicial en tablero (Base): (" + nave.getPosicion().getX() + ", " + nave.getPosicion().getY() + ")\n");

        // 4. Instanciación de Sensores y ReglasNavegacion
        Sensores sensores = new Sensores(tablero);
        ReglasNavegacion reglas = new ReglasNavegacion(sensores);

        // Conectamos tus reglas de navegación al AsistenteDeComandos
        asistente.setReglasNavegacion(reglas);

        // 5. Escaneo inicial con tus Sensores
        System.out.println("--- 1. Prueba de Escaneo de Entorno ---");
        sensores.escanearCortoAlcance();
        sensores.escanearLargoAlcance();
        System.out.println("✔ Escaneos de 3x3 y 5x5 ejecutados exitosamente.\n");

        // 6. Prueba de avance progresivo a través del Asistente (delegando en ReglasNavegacion)
        System.out.println("--- 2. Prueba de Navegación (Asistente -> ReglasNavegacion) ---");
        Posicion destinoObjetivo = new Posicion(5, 8);
        int combustibleInicial = nave.getCombustible();
        int energiaInicial = nave.getEnergia();

        System.out.println("Ordenando al Asistente avanzar hacia el objetivo: (" + destinoObjetivo.getX() + ", " + destinoObjetivo.getY() + ")");

        // Ejecutamos 2 turnos de paso
        asistente.avanzarHacia(destinoObjetivo);
        asistente.avanzarHacia(destinoObjetivo);

        System.out.println("Nueva Posición Nave: (" + nave.getPosicion().getX() + ", " + nave.getPosicion().getY() + ")");
        System.out.println("Combustible restante: " + nave.getCombustible() + " (Consumido: " + (combustibleInicial - nave.getCombustible()) + ")");
        System.out.println("Energía restante: " + nave.getEnergia() + " (Consumida: " + (energiaInicial - nave.getEnergia()) + ")");
        System.out.println("Desgaste acumulado: " + nave.getDesgaste());
        System.out.println("✔ Ciclo de navegación ejecutado correctamente.\n");

        // 7. Inspección de la Bitácora
        System.out.println("--- 3. Verificación de Eventos en Bitácora ---");
        System.out.println("Historial de eventos registrados:");
        for (String evento : bitacora.getRegistro()) {
            System.out.println("  • " + evento);
        }
        System.out.println("\n==================================================");
        System.out.println("  ¡PRUEBA DEL SUBSISTEMA COMPLETADA CON ÉXITO!    ");
        System.out.println("==================================================");
    }
}