
package modelo.universo;

import java.util.ArrayList;

import modelo.bitacora.Bitacora;
import modelo.destinoytablero.CantidadDeElementosSuperaCasillaException;
import modelo.destinoytablero.Destino;
import modelo.destinoytablero.DimensionesDeTableroInconsistentesException;
import modelo.destinoytablero.Posicion;
import modelo.destinoytablero.Tablero;
import modelo.elementos.ElementoDelUniverso;
import modelo.nave.Nave;
import modelo.elementos.Gema;
/**
 * Coordina la nave del jugador con el tablero y sus elementos.
 *
 * No calcula rutas ni descuenta el costo de los saltos:
 * esas tareas corresponden al sistema de navegacion
 * y a las misiones.
 *
 * Invariantes:
 * - El tablero tiene 20 filas y 20 columnas.
 * - nave, tablero y destino no son null.
 * - La posicion de la nave coincide con la del tablero
 *   cuando los movimientos se realizan mediante esta clase.
 * - El objetivo es null hasta elegir uno por primera vez.
 */
public class Universo {

    public static final int FILAS = 20;
    public static final int COLUMNAS = 20;

    private Nave nave;
    private final Tablero tablero;
    private final Destino destino;
    private Posicion objetivo;

    /**
     * Crea el universo y distribuye sus elementos.
     *
     * Pre:
     * - nave y elementos no son null.
     * - La lista no contiene null ni la nave del jugador.
     * - Se respeta el limite de 300 elementos.
     *
     * Post:
     * - Se crea un tablero de 20x20.
     * - La nave comienza en (10,10).
     * - Los elementos se distribuyen aleatoriamente.
     * - Las naves enemigas conocen su posicion.
     * - objetivo = null.
     *
     * @param nave nave controlada por el jugador
     * @param elementos elementos del tablero
     * @throws IllegalArgumentException si los parametros
     *         son invalidos
     * @throws DimensionesDeTableroInconsistentesException
     *         si el tablero rechaza las dimensiones
     * @throws CantidadDeElementosSuperaCasillaException
     *         si hay demasiados elementos
     */
    public Universo(Nave nave, ArrayList<ElementoDelUniverso> elementos)
            throws DimensionesDeTableroInconsistentesException, CantidadDeElementosSuperaCasillaException {

        if (nave == null || elementos == null) {
            throw new IllegalArgumentException(
                    "La nave y la lista de elementos no pueden ser null"
            );
        }

        if (nave.estaDestruida() || nave.getEnergia() == 0 || nave.getCombustible() == 0 || nave.getDesgaste() >= 100) {
            throw new IllegalArgumentException(
                    "La nave debe estar operativa al crear el universo"
            );
        }

        for (ElementoDelUniverso elemento : elementos) {
            if (elemento == null || elemento == nave) {
                throw new IllegalArgumentException(
                        "La lista no admite null ni la nave del jugador"
                );
            }
        }

        this.nave = nave;
        this.tablero = new Tablero(FILAS, COLUMNAS, elementos);
        this.destino = new Destino(tablero);
        this.objetivo = null;

        nave.setPosicion(tablero.getPosicionNave());

        sincronizarPosicionesEnemigas();
    }

    /**
     * Pre: ninguna.
     * Post: no modifica el universo.
     *
     * @return nave principal
     */
    public Nave getNave() {
        return nave;
    }

    /**
     * Pre: ninguna.
     * Post: no modifica el universo.
     *
     * @return tablero del universo
     */
    public Tablero getTablero() {
        return tablero;
    }

    /**
     * Pre: ninguna.
     * Post: no modifica el universo.
     *
     * @return objetivo actual o null
     */
    public Posicion getObjetivo() {
        return objetivo;
    }

    /**
     * Selecciona una coordenada objetivo libre.
     *
     * Pre: existen casillas disponibles.
     * Post:
     * - Almacena el nuevo objetivo.
     * - El tablero notifica a sus observadores.
     *
     * @return posicion del nuevo objetivo
     */
    public Posicion elegirObjetivo() {
        objetivo = tablero.obtenerObjetivo();

        return objetivo;
    }

    /**
     * Comprueba si una coordenada pertenece al tablero.
     *
     * Pre: posicion != null.
     * Post: no modifica el universo.
     *
     * @param posicion coordenada a comprobar
     * @return true si pertenece al tablero
     * @throws IllegalArgumentException si posicion es null
     */
    public boolean estaDentro(Posicion posicion) {
        if (posicion == null) {
            throw new IllegalArgumentException(
                    "La posicion no puede ser null"
            );
        }

        return posicion.getX() >= 0
                && posicion.getX() < FILAS
                && posicion.getY() >= 0
                && posicion.getY() < COLUMNAS;
    }

    /**
     * Actualiza la posicion de la nave y del tablero.
     *
     * Este metodo NO descuenta combustible ni valida
     * la longitud del salto. Eso corresponde al navegador.
     *
     * Pre:
     * - La nave esta operativa.
     * - La nueva posicion pertenece al tablero.
     *
     * Post:
     * - Nave y tablero tienen la misma posicion.
     * - El tablero notifica a sus observadores.
     *
     * @param nuevaPosicion coordenada de destino
     * @throws IllegalArgumentException si la posicion
     *         esta fuera del tablero
     * @throws IllegalStateException si la nave esta destruida
     */
    public void moverNave(Posicion nuevaPosicion) {
        validarPosicion(nuevaPosicion);
        exigirNaveOperativa();

        nave.setPosicion(nuevaPosicion);
        tablero.moverNave(nuevaPosicion);
    }

    /**
     * Sincroniza el tablero con la posicion de la nave.
     *
     * Se utiliza cuando otra clase, como ReglasNavegacion,
     * modifica directamente la posicion de Nave.
     *
     * Pre:
     * - nave.getPosicion() != null.
     * - La posicion pertenece al tablero.
     *
     * Post:
     * - El tablero queda en la posicion de la nave.
     * - Se comprueba si los recursos vitales se agotaron.
     *
     * @throws IllegalArgumentException si la posicion
     *         de la nave no es valida
     */
    public void sincronizarPosicionDesdeNave() {
        Posicion posicion = nave.getPosicion();

        validarPosicion(posicion);

        tablero.moverNave(posicion);

        verificarDestruccionPorRecursos();
    }

    /**
     * Consulta el elemento de la casilla actual.
     *
     * Pre: ninguna.
     * Post: no modifica el universo.
     *
     * @return elemento encontrado o null si esta vacia
     */
    public ElementoDelUniverso inspeccionarPosicionActual() {
        return tablero.inspeccionarPosicion();
    }


    /**
     * Interactua con el elemento de la posicion actual.
     *
     * Pre: la nave esta operativa.
     * Post:
     * - Ejecuta la interaccion si existe un elemento.
     * - Verifica si la nave debe ser destruida.
     * - No modifica directamente el tablero.
     *
     * @return true si existe un elemento, false si no
     */
    public boolean interactuarEnPosicionActual() {
        exigirNaveOperativa();

        ElementoDelUniverso elemento =
                tablero.inspeccionarPosicion();

        if (elemento == null) {
            return false;
        }

        elemento.interactuar(nave);

        verificarDestruccionPorRecursos();

        return true;
    }


    /**
     * Ejecuta un turno del tablero.
     *
     * Destino intenta mover aleatoriamente la mitad
     * de los elementos del universo.
     *
     * No mueve la nave ni ejecuta interacciones.
     * Esos pasos corresponden al ciclo de la mision.
     *
     * Pre: la nave esta operativa.
     *
     * Post:
     * - Se ejecuta turnoDestino().
     * - Se actualizan las posiciones de naves enemigas.
     * - El tablero notifica a sus observadores.
     *
     * @throws IllegalStateException si la nave esta destruida
     */
    public void avanzarTurnoDestino() {
        exigirNaveOperativa();

        destino.turnoDestino();

        sincronizarPosicionesEnemigas();
    }

    /**
     * Valida una posicion.
     *
     * Pre: ninguna.
     * Post: no modifica el universo.
     *
     * @param posicion coordenada a validar
     * @throws IllegalArgumentException si es null
     *         o esta fuera del tablero
     */
    private void validarPosicion(Posicion posicion) {
        if (!estaDentro(posicion)) {
            throw new IllegalArgumentException(
                    "Posicion fuera del tablero: ("
                            + posicion.getX() + ", "
                            + posicion.getY() + ")"
            );
        }
    }

    /**
     * Comprueba las condiciones de destruccion.
     *
     * Pre: ninguna.
     *
     * Post:
     * - Si energia <= 0, combustible <= 0
     *   o desgaste >= 100, destruye la nave.
     * - Si la nave sigue operativa, no la modifica.
     */
    private void verificarDestruccionPorRecursos() {
        if (!nave.estaDestruida()
                && (nave.getEnergia() <= 0
                || nave.getCombustible() <= 0
                || nave.getDesgaste() >= 100)) {

            nave.destruir();

            Bitacora.getInstance().registrar(
                    "Nave destruida: energia o combustible agotado, "
                            + "o desgaste maximo alcanzado."
            );
        }
    }

    /**
     * Comprueba que la nave pueda operar.
     *
     * Pre: ninguna.
     * Post: verifica su estado antes de continuar.
     *
     * @throws IllegalStateException si la nave esta destruida
     */
    private void exigirNaveOperativa() {
        verificarDestruccionPorRecursos();

        if (nave.estaDestruida()) {
            throw new IllegalStateException(
                    "La nave esta destruida y no puede continuar"
            );
        }
    }

    /**
     * Sincroniza las posiciones de las naves enemigas
     * con las casillas ocupadas en el tablero.
     *
     * Pre: tablero inicializado.
     *
     * Post:
     * - Cada nave enemiga tiene la posicion
     *   correspondiente a su casilla.
     * - No modifica los recursos de las naves.
     */
    private void sincronizarPosicionesEnemigas() {
        ElementoDelUniverso[][] grilla =
                tablero.verTablero();

        for (int fila = 0; fila < FILAS; fila++) {
            for (int columna = 0; columna < COLUMNAS; columna++) {

                if (grilla[fila][columna] instanceof Nave enemiga
                        && enemiga != nave) {

                    enemiga.setPosicion(
                            new Posicion(fila, columna)
                    );
                }
            }
        }
    }


    /**
     * Cambia la nave principal del universo.
     *
     * Pre:
     * - nuevaNave != null.
     * - La nave no esta destruida.
     * - Posee energia y combustible positivos.
     * - Su desgaste es menor a 100.
     *
     * Post:
     * - La nueva nave pasa a ser la del jugador.
     * - Se sincroniza con la posicion del tablero.
     * - Se conservan todos los elementos del universo.
     * - Se notifica a los observadores del tablero.
     *
     * @param nuevaNave nueva nave principal
     * @throws IllegalArgumentException si la nave es invalida
     */
    public void cambiarNaveJugador(Nave nuevaNave) {

        if (nuevaNave == null || nuevaNave.estaDestruida()
                || nuevaNave.getEnergia() <= 0
                || nuevaNave.getCombustible() <= 0
                || nuevaNave.getDesgaste() >= 100) {

            throw new IllegalArgumentException(
                    "La nueva nave debe estar operativa"
            );
        }

        nuevaNave.setPosicion(
                tablero.getPosicionNave()
        );

        this.nave = nuevaNave;

        tablero.moverNave(
                nuevaNave.getPosicion()
        );
    }

}
