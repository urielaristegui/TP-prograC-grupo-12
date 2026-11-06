
package modelo.sensores;

import modelo.destinoytablero.Posicion;
import modelo.elementos.ElementoDelUniverso;
import modelo.motorWarp.MotorWarp;
import modelo.nave.Nave;
import modelo.bitacora.Bitacora;

/**
 * clase encargada de evaluar las lecturas de los sensores y aplicar las reglas de navegacion en 4 direcciones (arriba, abajo, izquierda, derecha).<br>
 * determina si la nave debe realizar un avance normal o activar la secuencia warp para esquivar obstaculos sin moverse en diagonal.<br>
 *
 * <b>invariante:</b> sensores != null.
 */
public class ReglasNavegacion {

    private final Sensores sensores;

    /**
     * constructor de la clase reglasNavegacion.<br>
     * <b>pre:</b> sensores != null.<br>
     * <b>post:</b> inicializa la referencia al sistema de sensores.<br>
     *
     * @param sensores objeto de lectura del entorno.
     * @throws IllegalArgumentException si sensores es null.
     */
    public ReglasNavegacion(Sensores sensores) {
        if (sensores == null) {
            throw new IllegalArgumentException("los sensores no pueden ser null");
        }
        this.sensores = sensores;
    }

    /**
     * evalua el estado de la ruta y ejecuta el movimiento ortogonal (4 direcciones) correspondiente para el turno actual.<br>
     * <b>pre:</b> nave != null && destino != null.<br>
     * <b>post:</b> avanza en un solo eje por turno (arriba, abajo, izquierda o derecha) via salto normal de 1 casilla o salto warp de 2 casillas.<br>
     *
     * @param nave referencia a la nave en vuelo.
     * @param destino coordenada objetivo a alcanzar.
     * @throws IllegalArgumentException si nave o destino son null.
     */
    public void evaluarYEjecutarPaso(Nave nave, Posicion destino) {
        if (nave == null || destino == null) {
            throw new IllegalArgumentException("la nave y el destino no pueden ser null");
        }

        Posicion posActual = nave.getPosicion();

        int diffX = destino.getX() - posActual.getX();
        int diffY = destino.getY() - posActual.getY();

        if (diffX == 0 && diffY == 0) {
            Bitacora.getInstance().registrar("Navegacion: la nave ya se encuentra en la posicion objetivo");
            return;
        }

        // determinar direccion ortogonal principal (avanzar en X o en Y, nunca ambos a la vez)
        int deltaX = 0;
        int deltaY = 0;

        if (Math.abs(diffX) >= Math.abs(diffY) && diffX != 0) {
            deltaX = Integer.compare(diffX, 0);   //asigna 1 o -1
        } else if (diffY != 0) {
            deltaY = Integer.compare(diffY, 0);   //asigna 1 o -1
        }

        // intentar paso en la direccion principal
        boolean ejecutado = intentarPasoOrtogonal(nave, deltaX, deltaY);

        // si la direccion principal estaba bloqueada y habia diferencia en el otro eje, intentar el otro eje
        if (!ejecutado && diffX != 0 && diffY != 0) {
            int altDeltaX = (deltaX != 0) ? 0 : Integer.compare(diffX, 0);
            int altDeltaY = (deltaY != 0) ? 0 : Integer.compare(diffY, 0);

            Bitacora.getInstance().registrar("Navegacion: intentando direccion alternativa ortogonal");
            intentarPasoOrtogonal(nave, altDeltaX, altDeltaY);
        }
    }

    /**
     * intenta ejecutar un paso normal o warp en la direccion ortogonal dada.<br>
     * <b>pre:</b> nave != null && (deltaX == 0 || deltaY == 0) && (deltaX != 0 || deltaY != 0).<br>
     * <b>post:</b> retorna true si se pudo ejecutar un salto normal o warp en esa direccion.<br>
     *
     * @param nave referencia a la nave.
     * @param deltaX direccion en filas (-1, 0, 1).
     * @param deltaY direccion en columnas (-1, 0, 1).
     * @return true si se realizo el movimiento, false en caso contrario.
     */
    private boolean intentarPasoOrtogonal(Nave nave, int deltaX, int deltaY) {
        ElementoDelUniverso[][] cortoAlcance = sensores.escanearCortoAlcance();
        ElementoDelUniverso[][] largoAlcance = sensores.escanearLargoAlcance();

        ElementoDelUniverso celdaPaso1 = cortoAlcance[deltaX + 1][deltaY + 1];  //se le suma una porq en la matriz de 3x3 el centro ([1][1]) es la nave
        ElementoDelUniverso celdaPaso2 = largoAlcance[deltaX + 2][deltaY + 2];  //lo mismo pasa aca con la matriz de 5x5

        if (celdaPaso1 == null) {
            realizarSaltoNormal(nave, deltaX, deltaY);
            return true;
        } else {
            String tipoObstaculo = celdaPaso1.toString();
            Bitacora.getInstance().registrar("Navegacion: obstaculo detectado en direccion ortogonal (" + tipoObstaculo + ")");

            if (celdaPaso2 == null) {
                return realizarSaltoWarp(nave, deltaX, deltaY);
            } else {
                Bitacora.getInstance().registrar("ALERTA: maniobra warp bloqueada a 2 casillas en esta direccion");
                return false;
            }
        }
    }

    /**
     * ejecuta un salto normal de 1 casilla ortogonal.<br>
     * <b>pre:</b> nave != null.<br>
     * <b>post:</b> avanza 1 casilla ortogonal y consume 2 de combustible.<br>
     *
     * @param nave referencia a la nave.
     * @param deltaX direccion en filas (-1, 0, 1).
     * @param deltaY direccion en columnas (-1, 0, 1).
     */
    private void realizarSaltoNormal(Nave nave, int deltaX, int deltaY) {
        if (nave.getCombustible() >= 2) {
            int nuevaFila = nave.getPosicion().getX() + deltaX;
            int nuevaColumna = nave.getPosicion().getY() + deltaY;
            Posicion nuevaPosicion = new Posicion(nuevaFila, nuevaColumna);

            nave.setPosicion(nuevaPosicion);
            nave.descontarCombustible(2);

            Bitacora.getInstance().registrar("Navegacion: salto normal ortogonal ejecutado a " + nuevaPosicion);
        } else {
            Bitacora.getInstance().registrar("ERROR: combustible insuficiente para el salto normal");
        }
    }

    /**
     * administra y ejecuta la secuencia del salto warp respetando los estados del motor.<br>
     * <b>pre:</b> nave != null && (deltaX == 0 || deltaY == 0) && (deltaX != 0 || deltaY != 0).<br>
     * <b>post:</b> avanza 2 casillas en la direccion ortogonal si el motor completo la preparacion y hay 5 de energia, o avanza las fases de preparacion y enfriamiento.<br>
     *
     * @param nave referencia a la nave.
     * @param deltaX direccion en filas (-1, 0, 1).
     * @param deltaY direccion en columnas (-1, 0, 1).
     * @return true si se realizo el desplazamiento warp en este turno.
     */
    private boolean realizarSaltoWarp(Nave nave, int deltaX, int deltaY) {
        MotorWarp motor = nave.getMotorWarp();

        // si el motor esta disponible, inicia la preparacion del salto
        if (motor.estaDisponible()) {
            motor.prepararSalto();
            Bitacora.getInstance().registrar("Navegacion: iniciando fase de preparacion del motor warp");
            return false;
        }

        // si esta en fase de preparacion, evalua turnos de preparacion y recursos
        if (motor.estaPreparando()) {
            if (nave.getEnergia() < 5) {
                Bitacora.getInstance().registrar("ALERTA: energia insuficiente para el salto warp (requiere 5). continua en preparacion");
                return false;
            }

            if (!motor.estaListoParaSaltar()) {
                motor.avanzarTurnoPreparacion();
                Bitacora.getInstance().registrar("Navegacion: avanzando turno de preparacion del motor warp");
                return false;
            }

            // preparacion completa y recursos suficientes: efectua el salto
            motor.entrarEnWarp();

            int nuevaFila = nave.getPosicion().getX() + (2 * deltaX);
            int nuevaColumna = nave.getPosicion().getY() + (2 * deltaY);
            Posicion nuevaPosicion = new Posicion(nuevaFila, nuevaColumna);

            nave.setPosicion(nuevaPosicion);
            nave.descontarEnergia(5);

            // transiciona a enfriamiento y genera 3 de desgaste en la nave
            motor.iniciarEnfriamiento();
            nave.generarDesgaste(3);

            Bitacora.getInstance().registrar("Navegacion: salto warp ortogonal completado a " + nuevaPosicion);
            return true;
        }

        // si el motor esta en enfriamiento, no puede realizar maniobra warp
        if (motor.estaEnEnfriamiento()) {
            Bitacora.getInstance().registrar("Navegacion: motor en enfriamiento. maniobra warp no disponible");
            return false;
        }

        return false;
    }


    /**
     * devuelve la referencia al sistema de sensores.<br>
     * <b>pre:</b> ninguna.<br>
     * <b>post:</b> retorna el objeto sensores.<br>
     *
     * @return instancia de Sensores.
     */
    public Sensores getSensores() {
        return sensores;
    }
}
