
package modelo.sensores;

import java.util.IdentityHashMap;
import java.util.Map;

import modelo.bitacora.Bitacora;
import modelo.destinoytablero.Posicion;
import modelo.destinoytablero.Tablero;
import modelo.elementos.Asteroide;
import modelo.elementos.ElementoDelUniverso;
import modelo.motorWarp.MotorWarp;
import modelo.nave.Nave;

/**
 * Aplica las reglas de navegacion ortogonal mediante los sensores.
 * Un obstaculo (Asteroide) puede desencadenar un salto Warp
 * de dos casillas.
 *
 * Invariantes:
 * - sensores != null.
 * - Cada nave en preparacion conserva la direccion de su Warp.
 * - Cada invocacion de evaluarYEjecutarPaso representa
 *   un turno de la nave.
 *
 * Esta clase no modifica directamente el tablero:
 * AsistenteDeComandos sincroniza el movimiento
 * de Nave con Tablero.
 */
public class ReglasNavegacion {

    private static final int COSTO_SALTO_NORMAL = 2;
    private static final int COSTO_ENERGIA_WARP = 5;
    private static final int DESGASTE_WARP = 3;
    private static final int TURNOS_PREPARACION = 2;
    private static final int TURNOS_ENFRIAMIENTO = 1;

    private final Sensores sensores;

    private final Map<Nave, Posicion> direccionesWarp =
            new IdentityHashMap<>();

    /**
     * Construye las reglas de navegacion.
     *
     * Pre: sensores != null.
     * Post: conserva una referencia a los sensores.
     *
     * @param sensores sensores del universo
     * @throws IllegalArgumentException si sensores es null
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
     * Procesa un turno de navegacion hacia el objetivo.
     *
     * Pre:
     * - nave y destino no son null.
     * - nave.getPosicion() != null.
     * - La nave y el destino estan dentro del tablero.
     * - Nave y Tablero tienen la misma posicion.
     *
     * Post:
     * - Si el camino esta libre, puede avanzar una casilla.
     * - Si hay un asteroide, intenta preparar el Warp.
     * - El Warp avanza dos casillas tras la preparacion.
     * - Durante el enfriamiento no se desplaza.
     * - Nunca mueve la nave fuera del tablero.
     *
     * @param nave nave controlada
     * @param destino coordenada objetivo
     * @throws IllegalArgumentException si los argumentos son invalidos
     * @throws IllegalStateException si la nave no puede navegar
     */
    public void evaluarYEjecutarPaso(
            Nave nave, Posicion destino) {

        if (nave == null || destino == null
                || nave.getPosicion() == null) {

            throw new IllegalArgumentException(
                    "Nave, posicion actual y destino son obligatorios"
            );
        }

        Tablero tablero = sensores.getTablero();

        if (!estaDentro(nave.getPosicion())
                || !estaDentro(destino)) {

            throw new IllegalArgumentException(
                    "La nave y el destino deben estar dentro del tablero"
            );
        }

        if (!tablero.getPosicionNave().equals(
                nave.getPosicion())) {

            throw new IllegalStateException(
                    "La posicion de Nave no coincide con Tablero"
            );
        }

        if (nave.estaDestruida()) {
            throw new IllegalStateException(
                    "No se puede mover una nave destruida"
            );
        }

        MotorWarp motor = nave.getMotorWarp();

        // El enfriamiento tiene prioridad.
        if (motor.estaEnEnfriamiento()) {
            avanzarEnfriamiento(motor);
            return;
        }

        // Si el Warp esta preparando, conserva su direccion.
        if (motor.estaPreparando()) {

            Posicion direccion = direccionesWarp.get(nave);

            if (direccion == null) {
                throw new IllegalStateException(
                        "El motor prepara un Warp sin direccion registrada"
                );
            }

            continuarWarp(
                    nave,
                    direccion.getX(),
                    direccion.getY(),
                    destino
            );

            return;
        }

        Posicion actual = nave.getPosicion();

        int diferenciaX =
                destino.getX() - actual.getX();

        int diferenciaY =
                destino.getY() - actual.getY();

        if (diferenciaX == 0 && diferenciaY == 0) {

            Bitacora.getInstance().registrar(
                    "Navegacion: la nave ya esta en el objetivo"
            );

            return;
        }

        // Elegir primero el eje con mayor distancia.
        int deltaX = 0;
        int deltaY = 0;

        if (Math.abs(diferenciaX) >= Math.abs(diferenciaY)
                && diferenciaX != 0) {

            deltaX = Integer.signum(diferenciaX);

        } else {

            deltaY = Integer.signum(diferenciaY);
        }

        boolean atendido = intentarPasoOrtogonal(
                nave, destino, deltaX, deltaY
        );

        // Si la primera direccion esta bloqueada,
        // intentar avanzar por el otro eje.
        if (!atendido
                && diferenciaX != 0
                && diferenciaY != 0) {

            int alternativoX =
                    deltaX != 0
                            ? 0
                            : Integer.signum(diferenciaX);

            int alternativoY =
                    deltaY != 0
                            ? 0
                            : Integer.signum(diferenciaY);

            Bitacora.getInstance().registrar(
                    "Navegacion: intentando direccion alternativa"
            );

            intentarPasoOrtogonal(
                    nave,
                    destino,
                    alternativoX,
                    alternativoY
            );
        }
    }

    /**
     * Intenta resolver un movimiento ortogonal.
     *
     * Pre:
     * - deltaX y deltaY definen un paso ortogonal.
     * - La nave esta operativa.
     *
     * Post:
     * - Si el camino esta libre, realiza un salto normal.
     * - Si hay asteroide, intenta iniciar el Warp.
     * - Si no puede avanzar, no cambia la posicion.
     *
     * @return true si se movio o inicio preparacion Warp
     */
    private boolean intentarPasoOrtogonal(
            Nave nave,
            Posicion destino,
            int deltaX,
            int deltaY) {

        Posicion actual = nave.getPosicion();

        Posicion paso1 = nuevaPosicion(
                actual, deltaX, deltaY
        );

        if (!estaDentro(paso1)) {
            return false;
        }

        ElementoDelUniverso[][] corto =
                sensores.escanearCortoAlcance();

        ElementoDelUniverso elemento =
                corto[deltaX + 1][deltaY + 1];

        // Otros elementos no bloquean el movimiento:
        // se debe poder llegar a ellos para interactuar.
        if (!(elemento instanceof Asteroide)) {
            return realizarSaltoNormal(nave, paso1);
        }

        Bitacora.getInstance().registrar(
                "Navegacion: asteroide detectado en ("
                        + paso1.getX() + ","
                        + paso1.getY() + ")"
        );

        // Comprobar que sea posible avanzar dos casillas.
        if (!puedeLlegarDosCasillas(
                actual, destino, deltaX, deltaY)) {

            Bitacora.getInstance().registrar(
                    "Navegacion: Warp no viable por limites "
                            + "o por sobrepasar el objetivo"
            );

            return false;
        }

        // SEGUNDA CASILLA CORREGIDA
        ElementoDelUniverso[][] largo =
                sensores.escanearLargoAlcance();

        if (largo[2 + 2 * deltaX][2 + 2 * deltaY] != null) {

            Bitacora.getInstance().registrar(
                    "Navegacion: destino del Warp ocupado"
            );

            return false;
        }

        MotorWarp motor = nave.getMotorWarp();

        motor.resetearTurnosPreparacion();
        motor.prepararSalto();

        // Guardar la direccion para mantenerla
        // durante los turnos de preparacion.
        direccionesWarp.put(
                nave,
                new Posicion(deltaX, deltaY)
        );

        // Primer turno de preparacion.
        if (nave.getEnergia() >= COSTO_ENERGIA_WARP) {
            motor.avanzarTurnoPreparacion();
        }

        Bitacora.getInstance().registrar(
                "Navegacion: Warp en preparacion (turno "
                        + motor.getTurnosPreparacion()
                        + "/" + TURNOS_PREPARACION + ")"
        );

        return true;
    }

    /**
     * Realiza un salto normal.
     *
     * Pre:
     * - La posicion de destino esta dentro del tablero.
     * - La nave esta operativa.
     *
     * Post:
     * - Si tiene combustible, avanza una casilla
     *   y consume 2 unidades.
     * - Si no tiene combustible, no se mueve.
     *
     * @param nave nave a mover
     * @param nueva nueva posicion
     * @return true si realizo el salto
     */
    private boolean realizarSaltoNormal(
            Nave nave, Posicion nueva) {

        if (nave.getCombustible() < COSTO_SALTO_NORMAL) {

            Bitacora.getInstance().registrar(
                    "ERROR: combustible insuficiente para salto normal"
            );

            return false;
        }

        nave.descontarCombustible(COSTO_SALTO_NORMAL);

        nave.setPosicion(nueva);

        Bitacora.getInstance().registrar(
                "Navegacion: salto normal a ("
                        + nueva.getX() + ","
                        + nueva.getY() + ")"
        );

        return true;
    }

    /**
     * Continua una maniobra Warp ya iniciada.
     *
     * Pre:
     * - El motor esta en PreparandoSalto.
     * - La direccion fue almacenada previamente.
     *
     * Post:
     * - Completa la preparacion requerida.
     * - Si hay energia y aterrizaje disponible,
     *   avanza dos casillas.
     * - Consume 5 de energia.
     * - Genera 3 de desgaste.
     * - Inicia el estado de enfriamiento.
     *
     * @param nave nave que realiza el Warp
     * @param deltaX componente horizontal
     * @param deltaY componente vertical
     * @param destino objetivo de navegacion
     */
    private void continuarWarp(
            Nave nave,
            int deltaX,
            int deltaY,
            Posicion destino) {

        MotorWarp motor = nave.getMotorWarp();

        if (nave.getEnergia() < COSTO_ENERGIA_WARP) {

            Bitacora.getInstance().registrar(
                    "Navegacion: faltan 5 de energia; "
                            + "Warp sigue en preparacion"
            );

            return;
        }

        // Completar los turnos de preparacion.
        if (motor.getTurnosPreparacion()
                < TURNOS_PREPARACION) {

            motor.avanzarTurnoPreparacion();

            Bitacora.getInstance().registrar(
                    "Navegacion: preparacion Warp (turno "
                            + motor.getTurnosPreparacion()
                            + "/" + TURNOS_PREPARACION + ")"
            );

            return;
        }

        Posicion actual = nave.getPosicion();

        if (!puedeLlegarDosCasillas(
                actual, destino, deltaX, deltaY)) {

            Bitacora.getInstance().registrar(
                    "Navegacion: Warp preparado; "
                            + "aterrizaje fuera de ruta o limites"
            );

            return;
        }

        // Volver a revisar el aterrizaje.
        ElementoDelUniverso[][] largo =
                sensores.escanearLargoAlcance();

        if (largo[2 + 2 * deltaX][2 + 2 * deltaY] != null) {

            Bitacora.getInstance().registrar(
                    "Navegacion: Warp preparado; "
                            + "esperando que se libere el aterrizaje"
            );

            return;
        }

        // Comprobar recursos justo antes del salto.
        nave.descontarEnergia(COSTO_ENERGIA_WARP);

        motor.entrarEnWarp();

        Posicion nueva = nuevaPosicion(
                actual,
                2 * deltaX,
                2 * deltaY
        );

        nave.setPosicion(nueva);

        motor.iniciarEnfriamiento();

        motor.resetearTurnosEnfriamiento();
        motor.resetearTurnosPreparacion();

        nave.generarDesgaste(DESGASTE_WARP);

        direccionesWarp.remove(nave);

        Bitacora.getInstance().registrar(
                "Navegacion: Warp ejecutado a ("
                        + nueva.getX() + ","
                        + nueva.getY()
                        + "); -5 energia y +3 desgaste"
        );
    }

    /**
     * Procesa el enfriamiento del motor.
     *
     * Pre: el motor esta en Enfriamento.
     *
     * Post:
     * - Registra un turno de enfriamiento.
     * - Cuando termina, devuelve el motor a Disponible.
     *
     * @param motor motor que debe enfriarse
     */
    private void avanzarEnfriamiento(MotorWarp motor) {

        motor.setTurnosEnfriamiento(
                motor.getTurnosEnfriamiento() + 1
        );

        Bitacora.getInstance().registrar(
                "Navegacion: enfriamiento Warp ("
                        + motor.getTurnosEnfriamiento()
                        + "/" + TURNOS_ENFRIAMIENTO + ")"
        );

        if (motor.getTurnosEnfriamiento()
                >= TURNOS_ENFRIAMIENTO) {

            motor.volverAlDisponible();
            motor.resetearTurnosEnfriamiento();
        }
    }

    /**
     * Comprueba si puede realizarse un Warp
     * de dos casillas en la direccion indicada.
     *
     * Pre:
     * - actual y destino estan dentro del tablero.
     * - La direccion es ortogonal.
     *
     * Post:
     * - No modifica el estado del universo.
     * - Devuelve false si el aterrizaje supera
     *   los limites o sobrepasa el objetivo.
     *
     * @return true si el desplazamiento es valido
     */
    private boolean puedeLlegarDosCasillas(
            Posicion actual,
            Posicion destino,
            int deltaX,
            int deltaY) {

        Posicion aterrizaje = nuevaPosicion(
                actual,
                2 * deltaX,
                2 * deltaY
        );

        if (!estaDentro(aterrizaje)) {
            return false;
        }

        if (deltaX != 0) {

            return Math.abs(
                    destino.getX() - actual.getX()
            ) >= 2;
        }

        return Math.abs(
                destino.getY() - actual.getY()
        ) >= 2;
    }

    /**
     * Comprueba si una posicion pertenece al tablero.
     *
     * Pre: p != null.
     * Post: no modifica el tablero.
     *
     * @param p posicion a verificar
     * @return true si pertenece al tablero
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
     * Pre: actual != null.
     * Post: devuelve una posicion nueva sin modificar
     *       la posicion original.
     *
     * @param actual posicion inicial
     * @param deltaX desplazamiento horizontal
     * @param deltaY desplazamiento vertical
     * @return nueva posicion
     */
    private Posicion nuevaPosicion(
            Posicion actual,
            int deltaX,
            int deltaY) {

        return new Posicion(
                actual.getX() + deltaX,
                actual.getY() + deltaY
        );
    }

    /**
     * Devuelve los sensores utilizados.
     *
     * Pre: ninguna.
     * Post: no modifica el estado.
     *
     * @return sensores de navegacion
     */
    public Sensores getSensores() {
        return sensores;
    }
}
