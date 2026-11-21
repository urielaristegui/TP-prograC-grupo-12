
package Pruebas;

import java.util.ArrayList;

import modelo.destinoytablero.Posicion;
import modelo.elementos.Asteroide;
import modelo.elementos.Anomalia;
import modelo.elementos.ElementoDelUniverso;
import modelo.elementos.EstacionDeCarga;
import modelo.elementos.Gema;
import modelo.nave.Nave;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;
import modelo.universo.Universo;

/**
 * Pruebas.PruebaPanelMisiones.Prueba de integracion del universo dinamico.
 *
 * Comprueba la inicializacion del tablero,
 * los movimientos y las interacciones.
 */
public class PruebaUniverso {

    /**
     * Verifica una condicion de prueba.
     *
     * Pre: descripcion != null.
     * Post:
     * - Si la condicion es verdadera, muestra OK.
     * - Si es falsa, lanza AssertionError.
     *
     * @param condicion condicion a verificar
     * @param descripcion nombre de la prueba
     */
    private static void verificar(
            boolean condicion, String descripcion) {

        if (!condicion) {
            throw new AssertionError(
                    "ERROR: " + descripcion
            );
        }

        System.out.println("OK - " + descripcion);
    }

    /**
     * Busca un elemento dentro del tablero.
     *
     * Pre:
     * - universo != null.
     * - elemento != null.
     * - El elemento pertenece al tablero.
     *
     * Post:
     * - Devuelve la posicion del elemento.
     * - No modifica el universo.
     *
     * @param universo universo donde buscar
     * @param elemento elemento buscado
     * @return posicion del elemento
     * @throws AssertionError si no se encuentra
     */
    private static Posicion buscarPosicion(
            Universo universo,
            ElementoDelUniverso elemento) {

        ElementoDelUniverso[][] grilla =
                universo.getTablero().verTablero();

        for (int fila = 0; fila < grilla.length; fila++) {
            for (int columna = 0;
                 columna < grilla[fila].length;
                 columna++) {

                if (grilla[fila][columna] == elemento) {
                    return new Posicion(fila, columna);
                }
            }
        }

        throw new AssertionError(
                "No se encontro el elemento: " + elemento
        );
    }

    /**
     * Ejecuta las pruebas del universo.
     *
     * Pre: ninguna.
     * Post: verifica tablero, interacciones,
     * movimiento y condiciones de destruccion.
     *
     * @param args no utilizado
     * @throws Exception si falla la creacion del tablero
     */
    public static void main(String[] args) throws Exception {

        System.out.println("=== PRUEBA UNIVERSO ===");

        // 1. CREAR NAVE Y ELEMENTOS

        Nave jugador = NaveFactory.crearNave(
                TipoNave.EXPLORADORA, 1
        );

        Nave enemigo = NaveFactory.crearNave(
                TipoNave.CARGUERO, 2
        );

        Asteroide asteroide = new Asteroide(15);
        EstacionDeCarga estacion =
                new EstacionDeCarga(20, 20);
        Gema gema = new Gema(30);
        Anomalia anomalia = new Anomalia(10, 5, 12);

        ArrayList<ElementoDelUniverso> elementos =
                new ArrayList<>();

        elementos.add(asteroide);
        elementos.add(estacion);
        elementos.add(gema);
        elementos.add(anomalia);
        elementos.add(enemigo);

        // 2. CREAR UNIVERSO

        Universo universo = new Universo(jugador, elementos);

        ElementoDelUniverso[][] grilla =
                universo.getTablero().verTablero();

        verificar(
                grilla.length == 20 && grilla[0].length == 20,
                "Tablero 20x20"
        );

        verificar(
                jugador.getPosicion().equals(new Posicion(10, 10)),
                "Posicion inicial de la nave"
        );

        verificar(
                enemigo.getPosicion().equals(
                        buscarPosicion(universo, enemigo)
                ),
                "Posicion de nave enemiga sincronizada"
        );

        // 3. PROBAR ASTEROIDE

        universo.moverNave(
                buscarPosicion(universo, asteroide)
        );

        universo.interactuarEnPosicionActual();

        verificar(
                jugador.getEnergia() == 65,
                "Asteroide descuenta 15 de energia"
        );

        // 4. PROBAR ESTACION DE CARGA

        universo.moverNave(
                buscarPosicion(universo, estacion)
        );

        universo.interactuarEnPosicionActual();

        verificar(
                jugador.getEnergia() == 85
                        && jugador.getCombustible() == 80,
                "Estacion recarga energia y combustible"
        );

        // 5. PROBAR GEMAS

        universo.moverNave(
                buscarPosicion(universo, gema)
        );

        universo.interactuarEnPosicionActual();

        verificar(
                jugador.getGemas() == 30,
                "Gema recolectada correctamente"
        );

        universo.interactuarEnPosicionActual();

        verificar(
                jugador.getGemas() == 30,
                "La gema no puede recolectarse dos veces"
        );

        // 6. PROBAR ANOMALIA

        universo.moverNave(
                buscarPosicion(universo, anomalia)
        );

        universo.interactuarEnPosicionActual();

        verificar(
                jugador.getEnergia() == 75,
                "Anomalia descuenta energia"
        );

        verificar(
                jugador.getCombustible() == 75,
                "Anomalia descuenta combustible"
        );

        verificar(
                jugador.getDesgaste() == 12,
                "Anomalia genera desgaste"
        );

        // 7. PROBAR TURNO DINAMICO

        universo.avanzarTurnoDestino();

        verificar(
                enemigo.getPosicion().equals(
                        buscarPosicion(universo, enemigo)
                ),
                "Posicion enemiga sincronizada tras el turno"
        );

        // 8. PROBAR COMBATE

        universo.moverNave(
                buscarPosicion(universo, enemigo)
        );

        universo.interactuarEnPosicionActual();

        verificar(
                !jugador.estaDestruida(),
                "Exploradora gana contra Carguero"
        );

        // 9. PROBAR LIMITES DEL TABLERO

        boolean errorDetectado = false;

        try {
            universo.moverNave(new Posicion(20, 20));
        } catch (IllegalArgumentException e) {
            errorDetectado = true;
        }

        verificar(
                errorDetectado,
                "No permite salir del tablero"
        );

        // 10. PROBAR DESTRUCCION

        Nave otraNave = NaveFactory.crearNave(
                TipoNave.EXPLORADORA, 3
        );

        Asteroide fatal = new Asteroide(100);

        ArrayList<ElementoDelUniverso> obstaculos =
                new ArrayList<>();

        obstaculos.add(fatal);

        Universo universoFatal =
                new Universo(otraNave, obstaculos);

        universoFatal.moverNave(
                buscarPosicion(universoFatal, fatal)
        );

        universoFatal.interactuarEnPosicionActual();

        verificar(
                otraNave.estaDestruida(),
                "Asteroide fatal destruye la nave"
        );

        System.out.println(
                "\n=== TODAS LAS PRUEBAS PASARON ==="
        );
    }
}
