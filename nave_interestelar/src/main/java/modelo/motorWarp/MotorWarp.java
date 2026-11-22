
package modelo.motorWarp;

import modelo.bitacora.Bitacora;
import modelo.nave.Nave;

/**
 * Contexto del patron State para el motor de la nave.
 *
 * Las unicas operaciones publicas de vuelo son:
 * - saltar(): salto normal.
 * - saltarWarp(): salto Warp.
 *
 * Cada llamada representa como maximo un turno.
 *
 * Invariantes:
 * - estado != null.
 * - Los contadores de turnos son no negativos.
 */
public class MotorWarp {

    private static final int COMBUSTIBLE_NORMAL = 2;
    private static final int ENERGIA_WARP = 5;
    private static final int DESGASTE_WARP = 3;
    private static final int TURNOS_PREPARACION = 2;

    private State estado;
    private int turnosPreparacion;
    private int turnosEnfriamiento;

    /**
     * Pre: ninguna.
     * Post: inicializa el motor Disponible.
     */
    public MotorWarp() {
        estado = new Disponible(this);
    }


    /**
     * Solicita un salto normal.
     *
     * Pre:
     * - nave != null.
     * - La nave pertenece a este motor.
     *
     * Post:
     * - Si esta Disponible, consume 2 de combustible
     *   y autoriza un salto normal.
     * - Si esta Enfriamento, procesa el turno pendiente,
     *   genera 3 de desgaste y no permite desplazarse.
     * - Si esta PreparandoSalto, no interrumpe el Warp.
     *
     * @param nave nave propietaria
     * @return true si autoriza el salto normal
     */
    public boolean saltar(Nave nave) {

        validarNave(nave);

        if (estado instanceof Enfriamento) {
            completarEnfriamiento(nave);
            return false;
        }

        if (!(estado instanceof Disponible)) {

            Bitacora.getInstance().registrar(
                    "Motor Warp: salto normal no disponible "
                            + "durante la maniobra"
            );

            return false;
        }

        if (nave.getCombustible() < COMBUSTIBLE_NORMAL) {

            Bitacora.getInstance().registrar(
                    "ERROR: combustible insuficiente "
                            + "para salto normal"
            );

            return false;
        }

        nave.descontarCombustible(COMBUSTIBLE_NORMAL);

        return true;
    }


    /**
     * Solicita o continua un salto Warp.
     *
     * Pre:
     * - nave != null.
     * - La nave pertenece a este motor.
     * - La navegacion valida previamente
     *   la casilla de aterrizaje.
     *
     * Post:
     * - Exige dos turnos de preparacion.
     * - Sin energia suficiente permanece preparando.
     * - Cuando esta listo, consume 5 de energia,
     *   genera 3 de desgaste y pasa a enfriamiento.
     * - Autoriza avanzar dos casillas al devolver true.
     *
     * @param nave nave propietaria
     * @return true solamente al ejecutar el Warp
     */
    public boolean saltarWarp(Nave nave) {

        validarNave(nave);

        if (estado instanceof Enfriamento) {
            completarEnfriamiento(nave);
            return false;
        }

        if (estado instanceof Disponible) {
            turnosPreparacion = 0;
            estado.prepararSalto();
        }

        if (!(estado instanceof PreparandoSalto)) {
            return false;
        }

        if (nave.getEnergia() < ENERGIA_WARP) {

            if (turnosPreparacion >= TURNOS_PREPARACION) {
                // Se aborta el intento y vuelve a empezar
                // la preparacion, sin salir de ese estado.
                turnosPreparacion = 0;
            }

            Bitacora.getInstance().registrar(
                    "Motor Warp: esperando energia "
                            + "durante la preparacion"
            );

            return false;
        }

        if (turnosPreparacion < TURNOS_PREPARACION) {

            turnosPreparacion++;

            Bitacora.getInstance().registrar(
                    "Motor Warp: preparacion "
                            + turnosPreparacion
                            + "/"
                            + TURNOS_PREPARACION
            );

            return false;
        }

        // Transiciones delegadas a State.
        estado.entrarEnWarp();

        nave.descontarEnergia(ENERGIA_WARP);

        estado.iniciarEnfriamiento();

        turnosPreparacion = 0;
        turnosEnfriamiento = 0;

        Bitacora.getInstance().registrar(
                "Motor Warp: salto ejecutado "
                        + "(-5 energia, +3 desgaste)"
        );

        return true;
    }

    /**
     * Modifica el estado desde las clases State.
     *
     * Visibilidad de paquete: no se expone
     * a los controladores ni a la navegacion.
     *
     * Pre: nuevo != null.
     * Post: reemplaza el estado actual.
     *
     * @param nuevo nuevo estado
     */
    void setState(State nuevo) {

        if (nuevo == null) {
            throw new IllegalArgumentException(
                    "El estado no puede ser null"
            );
        }

        estado = nuevo;
    }


    /**
     * Procesa el turno de enfriamiento del Motor Warp.
     *
     * Pre:
     * - nave != null.
     * - El motor esta en estado Enfriamento.
     *
     * Post:
     * - Avanza un turno de enfriamiento.
     * - Genera exactamente 3 puntos de desgaste.
     * - Devuelve el motor al estado Disponible.
     * - No desplaza la nave.
     *
     * @param nave nave propietaria del motor
     */
    private void completarEnfriamiento(Nave nave) {

        turnosEnfriamiento++;

        Bitacora.getInstance().registrar(
                "Motor Warp: enfriamiento "
                        + turnosEnfriamiento
                        + "/1"
        );

        if (turnosEnfriamiento >= 1) {

            // Aplicar desgaste durante el enfriamiento.
            nave.generarDesgaste(DESGASTE_WARP);

            // Transicion mediante State.
            estado.volverADisponible();

            turnosEnfriamiento = 0;

            Bitacora.getInstance().registrar(
                    "Motor Warp: enfriamiento completo "
                            + "(+3 desgaste)"
            );
        }
    }


    /**
     * Valida que la nave pueda utilizar el motor.
     *
     * Pre: ninguna.
     * Post: no modifica el estado.
     *
     * @param nave nave a validar
     */
    private void validarNave(Nave nave) {

        if (nave == null) {
            throw new IllegalArgumentException(
                    "La nave no puede ser null"
            );
        }

        if (nave.estaDestruida()) {
            throw new IllegalStateException(
                    "No puede utilizarse el motor "
                            + "de una nave destruida"
            );
        }

        if (nave.getMotorWarp() != null
                && nave.getMotorWarp() != this) {

            throw new IllegalArgumentException(
                    "El motor no pertenece a la nave indicada"
            );
        }
    }
}
