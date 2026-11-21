
package modelo.sensores;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Collections;
import java.util.Set;

import modelo.bitacora.Bitacora;
import modelo.destinoytablero.Posicion;
import modelo.destinoytablero.Tablero;
import modelo.elementos.Asteroide;
import modelo.elementos.ElementoDelUniverso;
import modelo.nave.Nave;

/**
 * Decide el tipo y la direccion de cada movimiento.
 *
 * MotorWarp administra estados, turnos y recursos.
 * ReglasNavegacion administra las coordenadas.
 * AsistenteDeComandos sincroniza Nave con Tablero.
 *
 * Invariantes:
 * - sensores != null.
 * - Los Warp iniciados conservan su direccion.
 */
public class ReglasNavegacion {

    private final Sensores sensores;

    private final Map<Nave, Posicion> direccionesWarp =
            new IdentityHashMap<>();

    private final Set<Nave> enfriamientosPendientes =
            Collections.newSetFromMap(
                    new IdentityHashMap<>()
            );

    /**
     * Construye las reglas de navegacion.
     *
     * Pre: sensores != null.
     * Post: conserva la referencia a los sensores.
     *
     * @param sensores sensores del universo
     */
    public ReglasNavegacion(Sensores sensores) {

        if (sensores == null) {
            throw new IllegalArgumentException(
                    "Los sensores no pueden ser null"
            );
        }

        this.sensores = sensores;
    }

    /**
     * Ejecuta un turno de navegacion.
     *
     * Pre:
     * - Nave y destino no nulos.
     * - Posiciones dentro del tablero.
     * - Nave y Tablero sincronizados.
     *
     * Post:
     * - Como maximo se realiza un movimiento.
     * - El motor gestiona sus propios estados.
     * - No modifica directamente el Tablero.
     *
     * @param nave nave que se desplaza
     * @param destino posicion objetivo
     */
    public void evaluarYEjecutarPaso(
            Nave nave,
            Posicion destino) {

        if (nave == null
                || destino == null
                || nave.getPosicion() == null) {

            throw new IllegalArgumentException(
                    "La nave y el destino deben tener posicion"
            );
        }

        Tablero tablero = sensores.getTablero();

        if (!estaDentro(nave.getPosicion())
                || !estaDentro(destino)) {

            throw new IllegalArgumentException(
                    "La nave o el destino estan fuera del tablero"
            );
        }

        if (!tablero.getPosicionNave().equals(
                nave.getPosicion())) {

            throw new IllegalStateException(
                    "Nave y Tablero tienen posiciones diferentes"
            );
        }

        if (nave.estaDestruida()) {
            throw new IllegalStateException(
                    "No puede navegar una nave destruida"
            );
        }

        // ==========================================
        // ENFRIAMIENTO
        // ==========================================

        // Completar un turno de enfriamiento.
        if (enfriamientosPendientes.remove(nave)) {

            nave.getMotorWarp().saltar(nave);

            return;
        }

        // ==========================================
        // WARP EN PREPARACION
        // ==========================================

        Posicion direccionPendiente =
                direccionesWarp.get(nave);

        if (direccionPendiente != null) {

            continuarWarp(
                    nave,
                    destino,
                    direccionPendiente
            );

            return;
        }

        // ==========================================
        // CALCULO DE DIRECCION
        // ==========================================

        Posicion actual = nave.getPosicion();

        int distanciaX =
                destino.getX() - actual.getX();

        int distanciaY =
                destino.getY() - actual.getY();

        if (distanciaX == 0 && distanciaY == 0) {
            return;
        }

        // Priorizar el eje de mayor distancia.
        int dx = 0;
        int dy = 0;

        if (Math.abs(distanciaX) >= Math.abs(distanciaY)
                && distanciaX != 0) {

            dx = Integer.signum(distanciaX);

        } else {

            dy = Integer.signum(distanciaY);
        }

        // Intentar la direccion principal.
        if (!intentarDireccion(
                nave, destino, dx, dy)
                && distanciaX != 0
                && distanciaY != 0) {

            // Si esta bloqueada, intentar el otro eje.
            int otroDx = dx == 0
                    ? Integer.signum(distanciaX)
                    : 0;

            int otroDy = dy == 0
                    ? Integer.signum(distanciaY)
                    : 0;

            intentarDireccion(
                    nave,
                    destino,
                    otroDx,
                    otroDy
            );
        }
    }

    /**
     * Intenta avanzar en una direccion ortogonal.
     *
     * Pre:
     * - dx y dy representan un paso ortogonal.
     * - La nave se encuentra operativa.
     *
     * Post:
     * - Ante un asteroide intenta Warp.
     * - En otro caso intenta salto normal.
     * - Si no es posible avanzar, conserva posicion.
     *
     * @param nave nave a desplazar
     * @param destino posicion objetivo
     * @param dx desplazamiento horizontal
     * @param dy desplazamiento vertical
     * @return true si se atendio la direccion
     */
    private boolean intentarDireccion(
            Nave nave,
            Posicion destino,
            int dx,
            int dy) {

        Posicion actual = nave.getPosicion();

        Posicion paso1 = desplazar(
                actual,
                dx,
                dy
        );

        if (!estaDentro(paso1)) {
            return false;
        }

        // ==========================================
        // RADAR DE CORTO ALCANCE
        // ==========================================

        ElementoDelUniverso[][] corto =
                sensores.escanearCortoAlcance();

        // Si no hay asteroide, salto normal.
        if (!(corto[1 + dx][1 + dy]
                instanceof Asteroide)) {

            if (nave.getMotorWarp().saltar(nave)) {

                nave.setPosicion(paso1);
            }

            return true;
        }

        // ==========================================
        // ASTEROIDE DETECTADO
        // ==========================================

        if (!puedeSaltarDos(
                actual,
                destino,
                dx,
                dy)) {

            Bitacora.getInstance().registrar(
                    "Navegacion: Warp no viable por limites "
                            + "o por sobrepasar el objetivo"
            );

            return false;
        }

        // ==========================================
        // RADAR DE LARGO ALCANCE
        // ==========================================

        ElementoDelUniverso[][] largo =
                sensores.escanearLargoAlcance();

        // El aterrizaje esta exactamente
        // a DOS casillas de distancia.
        if (largo[2 + 2 * dx][2 + 2 * dy] != null) {

            Bitacora.getInstance().registrar(
                    "Navegacion: la casilla de aterrizaje "
                            + "esta ocupada"
            );

            return false;
        }

        // ==========================================
        // INICIAR MANIOBRA WARP
        // ==========================================

        Posicion direccion =
                new Posicion(dx, dy);

        direccionesWarp.put(
                nave,
                direccion
        );

        continuarWarp(
                nave,
                destino,
                direccion
        );

        return true;
    }

    /**
     * Continua un Warp pendiente.
     *
     * Pre:
     * - Existe una direccion de Warp registrada.
     * - Nave y destino son validos.
     *
     * Post:
     * - Mantiene la direccion durante la preparacion.
     * - Solicita al MotorWarp avanzar su ciclo.
     * - Si el motor autoriza, mueve dos casillas.
     * - Registra un turno de enfriamiento pendiente.
     *
     * @param nave nave que realiza el Warp
     * @param destino coordenada objetivo
     * @param direccion direccion del salto
     */
    private void continuarWarp(
            Nave nave,
            Posicion destino,
            Posicion direccion) {

        Posicion actual = nave.getPosicion();

        int dx = direccion.getX();
        int dy = direccion.getY();

        // ==========================================
        // VERIFICAR LIMITES
        // ==========================================

        if (!puedeSaltarDos(
                actual,
                destino,
                dx,
                dy)) {

            Bitacora.getInstance().registrar(
                    "Navegacion: Warp pendiente, "
                            + "aterrizaje fuera de la ruta"
            );

            return;
        }

        // ==========================================
        // VERIFICAR CASILLA DE ATERRIZAJE
        // ==========================================

        ElementoDelUniverso[][] largo =
                sensores.escanearLargoAlcance();

        if (largo[2 + 2 * dx][2 + 2 * dy] != null) {

            Bitacora.getInstance().registrar(
                    "Navegacion: Warp pendiente, "
                            + "aterrizaje ocupado"
            );

            return;
        }

        // ==========================================
        // SOLICITAR SALTO AL MOTOR
        // ==========================================

        // MotorWarp administra internamente:
        // - Preparacion de 2 turnos.
        // - Consumo de 5 de energia.
        // - Generacion de 3 de desgaste.
        // - Transiciones del patron State.
        if (nave.getMotorWarp().saltarWarp(nave)) {

            nave.setPosicion(
                    desplazar(
                            actual,
                            2 * dx,
                            2 * dy
                    )
            );

            // Finalizo la preparacion.
            direccionesWarp.remove(nave);

            // Queda pendiente un turno
            // de enfriamiento.
            enfriamientosPendientes.add(nave);
        }
    }

    /**
     * Comprueba la viabilidad de un Warp.
     *
     * Pre:
     * - actual y destino son posiciones validas.
     * - dx y dy representan una direccion ortogonal.
     *
     * Post:
     * - No modifica ningun estado.
     * - Devuelve false si el aterrizaje supera
     *   los limites o sobrepasa el objetivo.
     *
     * @return true si el salto es viable
     */
    private boolean puedeSaltarDos(
            Posicion actual,
            Posicion destino,
            int dx,
            int dy) {

        Posicion aterrizaje = desplazar(
                actual,
                2 * dx,
                2 * dy
        );

        if (!estaDentro(aterrizaje)) {
            return false;
        }

        if (dx != 0) {

            return Math.abs(
                    destino.getX() - actual.getX()
            ) >= 2;
        }

        return Math.abs(
                destino.getY() - actual.getY()
        ) >= 2;
    }

    /**
     * Comprueba los limites del tablero.
     *
     * Pre: p != null.
     * Post: no modifica ningun estado.
     *
     * @param p posicion a comprobar
     * @return true si esta dentro del tablero
     */
    private boolean estaDentro(Posicion p) {

        Tablero tablero = sensores.getTablero();

        return p.getX() >= 0
                && p.getX() < tablero.getX()
                && p.getY() >= 0
                && p.getY() < tablero.getY();
    }

    /**
     * Construye una posicion desplazada.
     *
     * Pre: p != null.
     * Post: devuelve una nueva posicion.
     *
     * @param p posicion inicial
     * @param dx desplazamiento horizontal
     * @param dy desplazamiento vertical
     * @return nueva posicion
     */
    private static Posicion desplazar(
            Posicion p,
            int dx,
            int dy) {

        return new Posicion(
                p.getX() + dx,
                p.getY() + dy
        );
    }

    /**
     * Devuelve los sensores utilizados.
     *
     * Pre: ninguna.
     * Post: no modifica ningun estado.
     *
     * @return sensores de navegacion
     */
    public Sensores getSensores() {
        return sensores;
    }
}
