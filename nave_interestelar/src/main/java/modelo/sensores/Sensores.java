package modelo.sensores;
import modelo.destinoytablero.Tablero;
import modelo.destinoytablero.Posicion;
import modelo.bitacora.Bitacora;
import modelo.elementos.*;
/**
 * clase que representa el sistema de sensores de la nave.<br>
 * permite escanear el entorno cercano (radar de corto alcance) y el mapa completo (radar de largo alcance) utilizando los metodos de consulta del tablero.<br>
 *
 * <b>invariante:</b> tablero != null.
 */
public class Sensores {

    private final Tablero tablero;

    /**
     * constructor de la clase sensores.<br>
     * <b>pre:</b> tablero != null.<br>
     * <b>post:</b> inicializa la referencia al tablero del universo.<br>
     *
     * @param tablero referencia al tablero donde opera la nave.
     * @throws IllegalArgumentException si el tablero es null.
     */
    public Sensores(Tablero tablero) {
        if (tablero == null) {
            throw new IllegalArgumentException("el tablero no puede ser null");
        }
        this.tablero = tablero;
    }

    /**
     * realiza un escaneo de corto alcance obteniendo el vecindario de 3x3 centrado en la nave.<br>
     * <b>pre:</b> ninguna.<br>
     * <b>post:</b> retorna la matriz de 3x3 de elementos del universo y asienta la lectura en la bitacora.<br>
     *
     * @return matriz de 3x3 con los elementos vecinos a la nave.
     */
    public ElementoDelUniverso[][] escanearCortoAlcance() {
        ElementoDelUniverso[][] alrededores = tablero.observarAlrededores();
        Bitacora.getInstance().registrar("Sensor: escaneo de corto alcance realizado alrededor de la nave");
        return alrededores;
    }

    /**
     * realiza un escaneo de largo alcance obteniendo la matriz de 5x5 alrededor de la nave (2 casillas a la redonda).<br>
     * <b>pre:</b> ninguna.<br>
     * <b>post:</b> retorna la matriz de 5x5 con los elementos en el rango de 2 casilleros y asienta el escaneo en la bitacora.<br>
     *
     * @return matriz de 5x5 con los elementos a 2 casillas de distancia.
     */
    public ElementoDelUniverso[][] escanearLargoAlcance() {
        ElementoDelUniverso[][] largoAlcance = new ElementoDelUniverso[5][5];
        Posicion posNave = tablero.getPosicionNave();
        ElementoDelUniverso[][] mapa = tablero.verTablero();

        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                int fila = posNave.getX() + dx;
                int columna = posNave.getY() + dy;

                if (fila >= 0 && fila < tablero.getX() && columna >= 0 && columna < tablero.getY()) {
                    largoAlcance[dx + 2][dy + 2] = mapa[fila][columna];
                } else {
                    largoAlcance[dx + 2][dy + 2] = null;
                }
            }
        }

        Bitacora.getInstance().registrar("Sensor: escaneo de largo alcance realizado (2 casillas a la redonda, 5x5)");
        return largoAlcance;
    }

    /**
     * inspecciona la celda actual donde esta ubicada la nave.<br>
     * <b>pre:</b> ninguna.<br>
     * <b>post:</b> retorna el elemento presente en la posicion actual de la nave o null si esta vacia.<br>
     *
     * @return elemento del universo en la casilla de la nave o null.
     */
    public ElementoDelUniverso escanearCeldaActual() {
        ElementoDelUniverso elemento = tablero.inspeccionarPosicion();
        String estado = (elemento == null) ? "vacia" : elemento.getClass().getSimpleName();
        Bitacora.getInstance().registrar("Sensor: inspeccion de posicion actual -> " + estado);
        return elemento;
    }

    /**
     * escanea una casilla especifica dada por su posicion en el mapa.<br>
     * <b>pre:</b> posicion != null.<br>
     * <b>post:</b> retorna el elemento ubicado en dicha coordenada o null si esta vacia o fuera de limites.<br>
     *
     * @param posicion coordenada a consultar en el tablero.
     * @return elemento en la posicion dada o null.
     * @throws IllegalArgumentException si posicion es null.
     */
    public ElementoDelUniverso escanearPosicion(Posicion posicion) {
        if (posicion == null) {
            throw new IllegalArgumentException("la posicion a escanear no puede ser null");
        }
        int fila = posicion.getX();
        int columna = posicion.getY();

        if (fila < 0 || fila >= tablero.getX() || columna < 0 || columna >= tablero.getY()) {
            Bitacora.getInstance().registrar("Sensor: la posicion " + posicion + " esta fuera de limites");
            return null;
        }

        ElementoDelUniverso[][] mapa = tablero.verTablero();
        ElementoDelUniverso elemento = mapa[fila][columna];
        String contenido = (elemento == null) ? "vacia" : elemento.getClass().getSimpleName();
        Bitacora.getInstance().registrar("Sensor: escaneando celda " + posicion + " -> " + contenido);
        return elemento;
    }

    /**
     * devuelve la referencia al tablero consultado.<br>
     * <b>pre:</b> ninguna.<br>
     * <b>post:</b> retorna el tablero del sistema.<br>
     *
     * @return referencia al tablero.
     */
    public Tablero getTablero() {
        return tablero;
    }
}