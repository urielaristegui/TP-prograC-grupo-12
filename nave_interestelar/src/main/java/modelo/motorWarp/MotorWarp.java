package modelo.motorWarp;

import modelo.bitacora.Bitacora;
import modelo.nave.Nave;

/**
 * contexto del patron state para el motor de la nave.<br>
 * administra los estados operativos, los contadores de preparacion y enfriamiento, y las operaciones de salto.<br>
 * <b>invariante:</b> state != null && turnosPreparacion >= 0 && turnosEnfriamiento >= 0.
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
     * construye el motor warp en estado disponible con sus contadores en cero.<br>
     * <b>pre:</b> ninguna.<br>
     * <b>post:</b> inicializa estado en una nueva instancia de Disponible, turnosPreparacion en 0 y turnosEnfriamiento en 0.<br>
     */
    public MotorWarp() {
        estado = new Disponible(this);
    }

    /**
     * solicita o procesa un salto normal para la nave.<br>
     * <b>pre:</b> nave != null, la nave no esta destruida y este motor pertenece a la nave (o su motor es null).<br>
     * <b>post:</b> si esta en enfriamiento, completa un turno de enfriamiento y retorna false; si no esta disponible o no hay combustible suficiente, registra el evento y retorna false; si esta disponible y hay combustible suficiente, descuenta 2 de combustible y retorna true.<br>
     *
     * @param nave nave propietaria del motor.
     * @return true si se autoriza el salto normal, false en caso contrario.
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
     * solicita o continua un salto warp para la nave.<br>
     * <b>pre:</b> nave != null, la nave no esta destruida, este motor pertenece a la nave (o su motor es null) y la navegacion valido previamente la casilla de aterrizaje.<br>
     * <b>post:</b> si esta en enfriamiento, avanza el enfriamiento y retorna false; si esta disponible, inicia la preparacion; si falta energia, permanece en preparacion y retorna false; si cumple los turnos y la energia, consume 5 de energia, genera 3 de desgaste, pasa a enfriamiento y retorna true.<br>
     *
     * @param nave nave propietaria del motor.
     * @return true unicamente cuando se ejecuta el salto warp, false en caso contrario.
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

        nave.generarDesgaste(DESGASTE_WARP);

        turnosPreparacion = 0;
        turnosEnfriamiento = 0;

        Bitacora.getInstance().registrar(
                "Motor Warp: salto ejecutado "
                        + "(-5 energia, +3 desgaste)"
        );

        return true;
    }

    /**
     * modifica el estado actual del motor warp desde las clases state.<br>
     * <b>pre:</b> nuevo != null.<br>
     * <b>post:</b> reemplaza el atributo estado por nuevo.<br>
     *
     * @param nuevo nuevo estado del motor warp.
     * @throws IllegalArgumentException si nuevo es null.
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
     * procesa un turno de enfriamiento del motor warp.<br>
     * <b>pre:</b> nave != null y el motor se encuentra en estado Enfriamento.<br>
     * <b>post:</b> incrementa turnosEnfriamiento y, al completarse el turno, genera 3 puntos de desgaste en la nave, transiciona a Disponible y reinicia turnosEnfriamiento a 0.<br>
     *
     * @param nave nave propietaria del motor.
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
     * valida que la nave exista, no este destruida y tenga asignado este motor o ninguno.<br>
     * <b>pre:</b> ninguna.<br>
     * <b>post:</b> no modifica el estado del motor ni de la nave; lanza excepcion si no se cumplen las condiciones de validez.<br>
     *
     * @param nave nave a validar.
     * @throws IllegalArgumentException si nave es null o si la nave tiene otro motor warp asignado.
     * @throws IllegalStateException si la nave esta destruida.
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